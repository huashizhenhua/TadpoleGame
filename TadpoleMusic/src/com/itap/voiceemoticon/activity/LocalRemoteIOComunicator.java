
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
    /**
     * Delimiter between Http Header and Http Content
     */
    private static final String HEADER_CONTENT_DELIMITER = "\r\n\r\n";

    private static final String LOCATION = "Location";

    private static final String CONTENT_LENGTH = "Content-Length";

    private InputStream mLocalIn = null;

    private OutputStream mLocalOut = null;

    private InputStream mRemoteIn;

    private OutputStream mRemoteOut;

    private int mLocalProxyPort = -1;

    private HttpGetProxy mHttpGetProxy;

    private Socket mRemoteSocket;

    private Socket mLocalSocket;

    /**
     * a url that is requested firstly. useded to handle 302 redirect
     */
    private String mRootUrl;

    private boolean mCacheHeaderUsing = false;

    private boolean mCacheBodyNeedSave = true;

    private boolean mCacheHeaderHadWritten = false;

    private int mCacheBodyLength = 0;

    private long mCacheSaveRangeStart = 0;

    private Runnable mWriteCacheHeaderTask;

    private Runnable mWriteCacheContentStreamTask;

    private Object mTmpWriteBodyLock = new Object();

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
            connectLocal();
            LocalRemoteIOComunicator.this.requestFromLocalToRemote();
        } catch (Exception e) {
            // Log.e("LocalRemoteIOComunicator", e.getMessage());
            e.printStackTrace();
            closeIO();
        }
    }

    private void connectLocal() throws IOException {
        // printlog("=====>init local Socket I/O");
        // mLocalSocket.setSoTimeout(20000);
        mLocalIn = mLocalSocket.getInputStream();
        mLocalOut = mLocalSocket.getOutputStream();
    }

    private void connectRemote(String remoteHost, int remotePort) throws IOException {
        SocketAddress address = new InetSocketAddress(remoteHost, remotePort);
        // --------连接目标服务器---------//
        mRemoteSocket = new Socket();
        mRemoteSocket.setSoTimeout(20000);
        mRemoteSocket.connect(address);
        // printlog("=====>remote Server connected");
        mRemoteOut = mRemoteSocket.getOutputStream();
        mRemoteIn = mRemoteSocket.getInputStream();
        // printlog("=====>init remote Server I/O");
    }

    public void requestFromLocalToRemote() throws IOException, InterruptedException {
        // printlog("=====>local start to receive");
        long byteRead = 0;
        byte[] buffer = new byte[5120];
        String bufferStr = "";
        while ((byteRead = mLocalIn.read(buffer)) != -1) {

            String reqStr = new String(buffer);
            printlog("----->localSocket[local-proxy]:" + reqStr);
            bufferStr = bufferStr + reqStr;
            if (bufferStr.contains("GET") && bufferStr.contains(HEADER_CONTENT_DELIMITER)) {

                // ----- parse proxy request
                String messageHeaer = bufferStr;
                String requestUri = HttpParser.getRequestLine(messageHeaer).uri;
                String remoteAddr = HttpParser.getRemoteAddrFromHackedUri(requestUri);
                String[] arr = remoteAddr.split(":");
                String remoteHostWithPort = arr[0];
                String uri = HttpParser.getRemoteUri(requestUri); // uri without
                                                                  // proxy
                String musicUrl = remoteAddr + uri; // music url
                mRootUrl = HttpParser.getMetaAddr(requestUri); // music root url
                if (mRootUrl == null) {
                    mRootUrl = musicUrl;
                }
                // ----- end

                // get http rrange
                mLocalRequestRange = HttpParser.getRange(messageHeaer);
                printlog("----->localRequestRange:" + mLocalRequestRange);

                final HttpCache cache = new HttpCache(getMusicCachePath(), mHttpGetProxy);
                final Properties properties = cache.readProperties();

                final long localRequestRangeStart = mLocalRequestRange.start;

                //
                if (cache.exist()) {
                    printlog("=====>local request has response cahce");
                    int cacheStatusCode = 200;
                    if (localRequestRangeStart != 0) {
                        cacheStatusCode = 206; // Http Partial Content
                    }
                    printlog("=====>local request has response cahce statusCode = "
                            + cacheStatusCode);

                    final int finalCacheStatusCode = cacheStatusCode;
                    final int completeContentLength = cache.getContentLength();
                    mCacheBodyLength = (int)cache.getBodyLength(); // music
                                                                   // stream
                                                                   // seek pos
                                                                   // to write
                    mCacheHeaderUsing = true;
                    printlog("----->cacheBodyLength = " + mCacheBodyLength
                            + ", completeContentLength = " + completeContentLength);

                    // write header from cache
                    mWriteCacheHeaderTask = new Runnable() {
                        @Override
                        public void run() {
                            mCacheHeaderHadWritten = true;
                            try {
                                cache.writeHeaderWithModified(mLocalOut, completeContentLength,
                                        finalCacheStatusCode, (int)localRequestRangeStart);
                            } catch (IOException e) {
                                e.printStackTrace();
                                closeIO();
                            }
                        }
                    };

                    if (mCacheBodyLength >= localRequestRangeStart) {
                        mCacheSaveRangeStart = mCacheBodyLength;
                        printlog("----->local body cache used length = "
                                + (mCacheSaveRangeStart - localRequestRangeStart));
                        // write music stream from cache
                        final long finalRangeStart = mCacheSaveRangeStart;
                        final long finalContentLength = completeContentLength;
                        mWriteCacheContentStreamTask = new Runnable() {
                            @Override
                            public void run() {
                                synchronized (mTmpWriteBodyLock) {
                                    try {
                                        cache.writeBodyCacheToLocalOut(mLocalOut,
                                                localRequestRangeStart, mCacheBodyLength);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        // Log.e("LocalRemoteIOComunicator",
                                        // e.getMessage());
                                        closeIO();
                                    }
                                    if (finalRangeStart >= finalContentLength) {
                                        System.out
                                                .println("=====>local finish writing response totally by using cache");
                                        closeIO();
                                    }
                                }
                            }
                        };
                    } else {
                        mCacheBodyNeedSave = false;
                        printlog("----->local no cache for requestRange =  "
                                + localRequestRangeStart);
                        mCacheSaveRangeStart = localRequestRangeStart;
                    }

                    if (mCacheSaveRangeStart >= completeContentLength) {
                        mWriteCacheHeaderTask.run();
                        mWriteCacheHeaderTask = null;
                        new Thread(mWriteCacheContentStreamTask).start();
                        mWriteCacheContentStreamTask = null;
                        printlog("=====>local writing response totally by using cache");
                        return;
                    }
                }

                else {
                    printlog("=====>local request has no cache");
                }

                String remoteHostToConnect = arr[0];
                int remotePortToConnect = HttpGetProxy.REMOTE_DEFAULT_PORT;
                if (arr.length >= 2) {
                    remotePortToConnect = Integer.valueOf(arr[1]);
                }

                printlog("----->localMusicUrl: " + musicUrl);
                if (messageHeaer.contains("GET") && messageHeaer.contains(HEADER_CONTENT_DELIMITER)) {
                    String locationStr = properties.getProperty(LOCATION);
                    if (locationStr != null && !"".equals(locationStr)) {

                        URL locationURL = new URL(locationStr);
                        int port = locationURL.getPort();
                        remotePortToConnect = (port != -1 ? port : 80);
                        remoteHostToConnect = locationURL.getHost();

                        remoteHostWithPort = HttpGetProxy.getHostWithPort(locationStr).trim();
                        uri = HttpGetProxy.getUriFromUrl(locationStr).trim();

                        printlog("----->location:" + locationStr);
                        printlog("----->url:" + uri);
                    }

                    // ---把request中的本地ip改为远程ip---//
                    messageHeaer = messageHeaer.replace(requestUri, uri);
                    messageHeaer = messageHeaer.replace(HttpGetProxy.LOCAL_IP_ADDRESS + ":"
                            + mLocalProxyPort, remoteHostWithPort);
                    printlog("=====>replace request host");
                    if (mCacheSaveRangeStart != 0) {
                        messageHeaer = HttpParser.replaceOrAddHeader(messageHeaer, "Range",
                                "bytes=" + mCacheSaveRangeStart + "-");
                        printlog("=====>replace request range");
                    }
                }

                printlog("----->connteRemote: remoteHostToConnect = " + remoteHostToConnect
                        + ", remotePortToConnect = " + remotePortToConnect);
                connectRemote(remoteHostToConnect, remotePortToConnect);
                printlog("----->localSocket[proxy->remote]:" + messageHeaer);
                // wait until reading response thread ready
                synchronized (this) {
                    // since response reading has a while statement
                    // we must read response in a new thread
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                LocalRemoteIOComunicator.this.responseFromRemoteToLocal();
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
                break;
            } else {
                mRemoteOut.write(bufferStr.getBytes());
                mRemoteOut.flush();
            }
        }
        printlog("=====>local finish receive");
    }

    private int getMusicStreamLength(String messageHeader) {
        String contentRange = HttpParser.getHeader("Content-Range", messageHeader, "");
        int contentLength = HttpParser.getContentLength(messageHeader);
        if (!"".equals(contentRange)) {
            try {
                int i = contentRange.indexOf("/");
                contentRange = contentRange.subSequence(i + 1, contentRange.length()).toString()
                        .trim();
                contentLength = Integer.valueOf(contentRange);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return contentLength;
    }

    public void responseFromRemoteToLocal() throws IOException {
        printlog("=====>remote start to receive");
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
        printlog("statusLine = " + statusLine);

        HttpCache httpCache = new HttpCache(getMusicCachePath(), mHttpGetProxy);
        Properties properties = httpCache.readProperties();

        // handle 302 redirect
        if (statusLine.statusCode == 302) {
            String location = HttpParser.getLocation(messageHeader);
            printlog("=====>remote has 302 redirect. location = " + location);
            if (location != null) {
                try {
                    String proxyUrl = mHttpGetProxy.getProxyUrl(location, mRootUrl);
                    messageHeader = messageHeader.replace(location, proxyUrl);
                    properties.setProperty(LOCATION, location);
                } catch (Exception e) {
                    printlog("remote start proxy exception");
                    e.printStackTrace();
                }
            }
        }

        if ((statusLine.statusCode == 200 || statusLine.statusCode == 206)) {

            // skip 164 byte
            byte[] bb = new byte[164];
            mRemoteIn.read(bb);
            printlog("-----> bb  =  " + new String(bb));

            if (mCacheHeaderUsing) {
                if ((!mCacheHeaderHadWritten) && mWriteCacheHeaderTask != null) {
                    mWriteCacheHeaderTask.run();
                }
            }

            else {
                // save header cache
                httpCache.writeCacheResponseHeader(messageHeaderRaw);

                // save MusicStreamLength
                int contentLength = getMusicStreamLength(messageHeader);
                printlog("contentLength = " + contentLength);
                properties.setProperty(CONTENT_LENGTH, "" + contentLength);
            }
        }
        httpCache.writeProperties(getMusicCachePath(), properties);

        printlog("----->remoteSocket messageHeader ..." + messageHeader);
        printlog("----->remoteSocket rootUrl ..." + mRootUrl);
        if (!mCacheHeaderHadWritten) {
            mLocalOut.write(messageHeader.getBytes());
        }
        printlog("======>remote read message content");

        RandomAccessFile ras = httpCache.openCacheResponseBody();
        if (mCacheBodyNeedSave && null != ras) {
            ras.seek(mCacheSaveRangeStart);
            int saveBodyCacheLength = 0;
            while ((bytesRead = mRemoteIn.read(buffer)) != -1) {
                // write cache
                if (mWriteCacheContentStreamTask != null) {
                    mWriteCacheContentStreamTask.run();
                    mWriteCacheContentStreamTask = null;
                }

                if (statusLine.statusCode == 200 || statusLine.statusCode == 206) {
                    ras.write(buffer, 0, bytesRead);
                    saveBodyCacheLength += bytesRead;
                }
                if (statusLine.statusCode == 400) {
                    System.out.print(new String(buffer));
                }
                mLocalOut.write(buffer, 0, bytesRead);
                mLocalOut.flush();
            }
            close(ras);
            printlog("----->remote writeRangeStart(seekPos) = " + mCacheSaveRangeStart
                    + ", saveBodyCacheLength = " + saveBodyCacheLength + ", contentLength = "
                    + properties.getProperty(CONTENT_LENGTH));
        } else {
            while ((bytesRead = mRemoteIn.read(buffer)) != -1) {
                mLocalOut.write(buffer, 0, bytesRead);
                mLocalOut.flush();
            }
        }
        printlog("=====>remote finish receive...........");
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

    public static final void printlog(String msg) {
        if (HttpGetProxy.DEBUG) {
            System.out.println(msg);
        }
    }
}
