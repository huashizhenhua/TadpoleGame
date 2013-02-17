package com.itap.voiceemoticon.activity;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URL;
import java.util.Properties;

import android.util.Log;

import com.itap.voiceemoticon.activity.HttpParser.Range;
import com.itap.voiceemoticon.activity.HttpParser.StatusLine;

/**
 * <br>=
 * ========================= <br>
 * author：Zenip <br>
 * email：lxyczh@gmail.com <br>
 * create：2013-2-1 <br>=
 * =========================
 */
public class LocalRemoteIOComunicator implements Runnable {
    // Delimiter between Http Header and Http Content
    private static final String HEADER_CONTENT_DELIMITER = "\r\n\r\n";
    private static final String LOCATION = "Location";
    private static final String CONTENT_LENGTH = "Content-Length";

    private InputStream mLocalIn = null;
    private OutputStream mLocalOut = null;
    private int mLocalProxyPort = -1;

    private InputStream mRemoteIn;
    private OutputStream mRemoteOut;

    private HttpGetProxy mHttpGetProxy;
    private Socket mRemoteSocket;
    private Socket mLocalSocket;
    private String mRootUrl;


    private boolean mUsingCache = false;
    private boolean cacheHeaderHadWritten = false;


    private Runnable mWriteCacheHeaderTask;

    /**
     * a lock to ensure that reading cached response before reading remote response
     */
    private Object tmpWriteBodyCacheLock = new Object();

    private int mWriteRangeStart = 0;

    /**
     * local http range header
     */
    private Range mLocalRequestRange = new Range();

    public String getMusicCachePath() {
        return mRootUrl;
    }

    public LocalRemoteIOComunicator(HttpGetProxy httpProxy, Socket localSocket, int localPort) {
        mHttpGetProxy = httpProxy;
        mLocalProxyPort = localPort;
        mLocalSocket = localSocket;
    }

    @Override
    public void run() {
        try {
            //            System.out.println("=====>init local Socket I/O");
            // mLocalSocket.setSoTimeout(20000);
            mLocalIn = mLocalSocket.getInputStream();
            mLocalOut = mLocalSocket.getOutputStream();
            LocalRemoteIOComunicator.this.requestFromLocalToRemote();
        } catch (Exception e) {
//            Log.e("LocalRemoteIOComunicator", e.getMessage());
            e.printStackTrace();
            closeIO();
        }
    }

    public void connectRemote(String remoteHost, int remotePort) throws IOException {
        SocketAddress address = new InetSocketAddress(remoteHost, remotePort);
        // --------连接目标服务器---------//
        mRemoteSocket = new Socket();
        mRemoteSocket.setSoTimeout(20000);
        mRemoteSocket.connect(address);
        //        System.out.println("=====>remote Server connected");
        mRemoteOut = mRemoteSocket.getOutputStream();
        mRemoteIn = mRemoteSocket.getInputStream();
        //        System.out.println("=====>init remote Server I/O");
    }


    public void requestFromLocalToRemote() throws IOException, InterruptedException {
        //        System.out.println("=====>local start to receive");
        long byteRead = 0;
        byte[] buffer = new byte[5120];
        String bufferStr = "";
        System.out.println("=====>local request receive");
        while ((byteRead = mLocalIn.read(buffer)) != -1) {

            String reqStr = new String(buffer);
            String formatReqStr = reqStr.replace("\r", "CR");
            formatReqStr = formatReqStr.replace("\n", "LF");
            System.out.println("----->localSocket[local-proxy]:" + formatReqStr);
            bufferStr = bufferStr + reqStr;
            if (bufferStr.contains("GET") && bufferStr.contains(HEADER_CONTENT_DELIMITER)) {

                // ---把request中的本地ip改为远程ip---//
                String messageHeaer = bufferStr;
                String requestUri = HttpParser.getRequestLine(messageHeaer).uri;
                String remoteAddr = HttpParser.getRemoteAddrFromHackedUri(requestUri);
                String[] arr = remoteAddr.split(":");

                mLocalRequestRange = HttpParser.getRange(messageHeaer);
                System.out.println("----->localRequestRange:" + mLocalRequestRange);

                String remoteHostWithPort = arr[0];
                String uri = HttpParser.getRemoteUri(requestUri);
                String musicUrl = remoteAddr + uri;

                mRootUrl = HttpParser.getMetaAddr(requestUri);
                if (mRootUrl == null) {
                    mRootUrl = musicUrl;
                }

                final HttpCache cache = new HttpCache(getMusicCachePath(), mHttpGetProxy);
                final Properties properties = cache.readProperties();

                final long localRequestRangeStart = mLocalRequestRange.start;

                //
                if (cache.exist()) {
                    System.out.println("=====>local request has response cahce");
                    int cacheStatusCode = 200;
                    if (mLocalRequestRange.start != 0) {
                        cacheStatusCode = 206; //Http Partial Content
                    }
                    System.out.println("=====>local request has response cahce statusCode = " + cacheStatusCode);

                    final int finalCacheStatusCode = cacheStatusCode;
                    final int completeContentLength = cache.getContentLength();
                    mWriteRangeStart = (int) cache.getBodyLength();
                    mUsingCache = true;
                    System.out.println("----->cacheBodyLength = " + mWriteRangeStart + ", completeContentLength = " + completeContentLength);


                    mWriteCacheHeaderTask = new Runnable() {
                        @Override
                        public void run() {
                            cacheHeaderHadWritten = true;
                            System.out.println("=====>local writing response header by using cache");
                            try {
                                cache.writeHeaderWithModified(mLocalOut, completeContentLength, finalCacheStatusCode, (int) localRequestRangeStart);
                            } catch (IOException e) {
                                e.printStackTrace();
                                closeIO();
                            }
                        }
                    };

                    if (mWriteRangeStart >= completeContentLength) {
                        mWriteCacheHeaderTask.run();
                        mWriteCacheHeaderTask = null;
                    }

                    final long finalRangeStart = mWriteRangeStart;
                    final long finalContentLength = completeContentLength;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            synchronized (tmpWriteBodyCacheLock) {
                                try {
                                    cache.writeCacheToLocalOut(mLocalOut, mLocalRequestRange.start);
                                } catch (Exception e) {
                                    e.printStackTrace();
//                                    Log.e("LocalRemoteIOComunicator", e.getMessage());
                                    closeIO();
                                }
                                if (finalRangeStart >= finalContentLength) {
                                    System.out.println("=====>local finish writing response totally by using cache");
                                    closeIO();
                                }
                            }
                        }
                    }).start();

                    if (mWriteRangeStart >= completeContentLength) {
                        System.out.println("=====>local writing response totally by using cache");
                        return;
                    }
                }

                else {
                    System.out.println("=====>local request has no cache");
                }

                String remoteHostToConnect = arr[0];
                int remotePortToConnect = HttpGetProxy.REMOTE_DEFAULT_PORT;
                if (arr.length >= 2) {
                    remotePortToConnect = Integer.valueOf(arr[1]);
                }

                System.out.println("----->localMusicUrl: " + musicUrl);
                if (messageHeaer.contains("GET") && messageHeaer.contains(HEADER_CONTENT_DELIMITER)) {
                    String locationStr = properties.getProperty(LOCATION);
                    if (locationStr != null && !"".equals(locationStr)) {

                        URL locationURL = new URL(locationStr);
                        int port = locationURL.getPort();
                        remotePortToConnect = (port != -1 ? port : 80);
                        remoteHostToConnect = locationURL.getHost();

                        remoteHostWithPort = HttpGetProxy.getHostWithPort(locationStr).trim();
                        uri = HttpGetProxy.getUriFromUrl(locationStr).trim();

                        System.out.println("----->location:" + locationStr);
                        System.out.println("----->url:" + uri);
                    }

                    // ---把request中的本地ip改为远程ip---//
                    messageHeaer = messageHeaer.replace(requestUri, uri);
                    messageHeaer = messageHeaer.replace(HttpGetProxy.LOCAL_IP_ADDRESS + ":" + mLocalProxyPort, remoteHostWithPort);
                    System.out.println("=====>replace request host");
                    if (localRequestRangeStart != 0) {
                        messageHeaer = HttpParser.replaceOrAddHeader(messageHeaer, "Range", "bytes=" + mWriteRangeStart + "-");
                        System.out.println("=====>replace request range");
                    }
                }

                System.out.println("----->localSocket[proxy->remote]:" + messageHeaer);
                connectRemote(remoteHostToConnect, remotePortToConnect);

                // wait until reading response thread ready
                synchronized (this) {
                    // since response reading has a while statement
                    // we must read response in a new thread
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                synchronized (tmpWriteBodyCacheLock) {
                                    LocalRemoteIOComunicator.this.responseFromRemoteToLocal();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            } finally {
                                closeIO();
                            }
                        }
                    }).start();
                    this.wait();
                }
                mRemoteOut.write(messageHeaer.getBytes());
                mRemoteOut.flush();
                continue;
            } else {
                mRemoteOut.write(bufferStr.getBytes());
                mRemoteOut.flush();
            }
        }
        System.out.println("=====>local finish receive");
    }

    private int getMusicStreamLength(String messageHeader) {
        String contentRange = HttpParser.getHeader("Content-Range", messageHeader, "");
        int contentLength = HttpParser.getContentLength(messageHeader);
        if (!"".equals(contentRange)) {
            try {
                int i = contentRange.indexOf("/");
                contentRange = contentRange.subSequence(i + 1, contentRange.length()).toString().trim();
                contentLength = Integer.valueOf(contentRange);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return contentLength;
    }

    public void responseFromRemoteToLocal() throws IOException {
        System.out.println("=====>remote start to receive");
        int bytesRead;

        // notify response ready
        synchronized (this) {
            this.notify();
        }

        byte[] buffer = new byte[5120];

        // remote message header
        byte[] messageHeaderRaw = HttpParser.readMessageHeaderRaw(mRemoteIn);
        String messageHeader = new String(messageHeaderRaw);
        StatusLine statusLine = HttpParser.getStatusLine(messageHeader);
        System.out.println("statusLine = " + statusLine);


        HttpCache httpCache = new HttpCache(getMusicCachePath(), mHttpGetProxy);
        Properties properties = httpCache.readProperties();

        // handle 302 redirect 
        if (statusLine.statusCode == 302) {
            String location = HttpParser.getLocation(messageHeader);
            System.out.println("=====>remote has 302 redirect. location = " + location);
            if (location != null) {
                try {
                    String proxyUrl = mHttpGetProxy.getProxyUrl(location, mRootUrl);
                    messageHeader = messageHeader.replace(location, proxyUrl);
                    properties.setProperty(LOCATION, location);
                } catch (Exception e) {
                    System.out.println("remote start proxy exception");
                    e.printStackTrace();
                }
            }
        }

        if ((statusLine.statusCode == 200 || statusLine.statusCode == 206)) {

            if (mUsingCache) {
                if ((!cacheHeaderHadWritten) && mWriteCacheHeaderTask != null) {
                    mWriteCacheHeaderTask.run();
                }
            }

            else {
                // save header cache
                httpCache.writeCacheResponseHeader(messageHeaderRaw);

                // save MusicStreamLength
                int contentLength = getMusicStreamLength(messageHeader);
                System.out.println("contentLength = " + contentLength);
                properties.setProperty(CONTENT_LENGTH, "" + contentLength);
            }
        }
        httpCache.writeProperties(getMusicCachePath(), properties);

        String formatMessageHeader = messageHeader.replace("\r", "CR");
        formatMessageHeader = formatMessageHeader.replace("\n", "LF");
        System.out.println("----->remoteSocket messageHeader ..." + formatMessageHeader);
        System.out.println("----->remoteSocket rootUrl ..." + mRootUrl);
        if (!cacheHeaderHadWritten) {
            mLocalOut.write(messageHeader.getBytes());
        }
        System.out.println("======>remote read message content");

        //---------------hack------------>
        mRemoteIn.skip(164);
        //---------------hack end-------->


        System.out.println("----->remote needWriteRangeStart(seekPos) = " + mWriteRangeStart + ", cacheBodyLength = " + httpCache.getBodyLength());
        if (httpCache.getBodyLength() >= mWriteRangeStart) {
            RandomAccessFile ras = httpCache.openCacheResponseBody();
            ras.seek(mWriteRangeStart);
            int writeCacheLength = 0;
            while ((bytesRead = mRemoteIn.read(buffer)) != -1) {
                if (statusLine.statusCode == 200 || statusLine.statusCode == 206) {
                    ras.write(buffer, 0, bytesRead);
                    writeCacheLength += bytesRead;
                }
                if (statusLine.statusCode == 400) {
                    System.out.print(new String(buffer));
                }
                mLocalOut.write(buffer, 0, bytesRead);
                mLocalOut.flush();
            }
            close(ras);
            System.out.println("----->remote writeRangeStart(seekPos) = " + mWriteRangeStart + ", writeToCacheLength = " + writeCacheLength + ", contentLength = "
                    + properties.getProperty(CONTENT_LENGTH));
        } else {

        }
        System.out.println("=====>remote finish receive...........");
    }

    public void closeIO() {
        close(mLocalIn);
        close(mLocalOut);
        close(mRemoteIn);
        close(mRemoteOut);
        try {
            mLocalSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void close(Closeable obj) {
        try {
            if (obj != null) {
                obj.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
