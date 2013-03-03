package com.tadpolemusic.media.http;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



/**
 * http get proxy
 * 
 * instruction: generate a proxy url with hacked source url. then in every
 * request. we will know the source url from the hacked source uri.
 * 
 * <br>=
 * ========================= <br>
 * author：Zenip <br>
 * email：lxyczh@gmail.com <br>
 * create：2013-2-1 <br>=
 * =========================
 */
public class HttpGetProxy {
    public static final String LOCAL_IP_ADDRESS = "127.0.0.1";
    public static final int REMOTE_DEFAULT_PORT = 80;
    private ArrayList<LocalRemoteIOComunicator> comunicatorList = new ArrayList<LocalRemoteIOComunicator>();
    private ServerSocket localServer = null;
    private int mLocalPort;
    private boolean mGoOnListening = false;

    public HttpGetProxy(int localPort) {
        mLocalPort = localPort;
    }

    /**
     * return host .e.g www.baidu.com:80
     * 
     * @param fromUrl
     * @return
     */
    public static String getHost(String fromUrl) {
        String addr = null;
        try {
            URL fromURL = new URL(fromUrl);
            addr = fromURL.getHost();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return addr;
    }

    /**
     * return host with port .e.g www.baidu.com:80
     * 
     * @param fromUrl
     * @return
     */
    public static String getHostWithPort(String fromUrl) {
        String addr = null;
        try {
            URL fromURL = new URL(fromUrl);
            addr = fromURL.getHost();
            if (fromURL.getPort() != -1) {
                addr = addr + ":" + fromURL.getPort();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return addr;
    }

    /**
     * return uri
     * .e.g
     * in: http://www.baidu.com/hello/index.html?gg=true
     * out: /hello/index.html?gg=true
     * 
     * @param fromUrl
     * @return
     */
    public static String getUriFromUrl(String fromUrl) {
        Pattern p = Pattern.compile(".*?://.*?(/.*)");
        Matcher m = p.matcher(fromUrl);
        if (m.find()) {
            return m.group(1);
        } else {
            return "";
        }
    }

    /**
     * get proxy url.
     * 
     * .e.g
     * 
     * ---input--- http://www.baidu.com:8080/{query}
     * 
     * ---ouput---
     * http://127.0.0.1:{mLocalPort}/{query}hackwww.baidu.com:8080hack
     * 
     * @param fromUrl
     *            the url need be proxy
     * @param rootUrl
     *            the root request source url
     * 
     * @return
     */
    public String getProxyUrl(String fromUrl, String rootUrl) {
        String proxyUrl = "";
        try {
            URL fromURL = new URL(fromUrl);
            String addr = fromURL.getHost();
            if (fromURL.getPort() != -1) {
                addr = addr + ":" + fromURL.getPort();
            }
            proxyUrl = fromUrl.replace(addr, LOCAL_IP_ADDRESS + ":" + mLocalPort);
            proxyUrl = proxyUrl + HttpParser.hackString(addr) + (rootUrl != null ? HttpParser.metaStr(rootUrl) : "");
            printLog("proxyUrl = " + proxyUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return proxyUrl;
    }

    public String getProxyUrl(String fromUrl) {
        return getProxyUrl(fromUrl, null);
    }

    /**
     * start proxy
     * 
     * 
     * @return true when localServer is running. false otherwise ;
     */
    public boolean start() {
        try {
            if (localServer != null && (!localServer.isClosed())) {
                return true;
            }
            printLog("..........localServer start prepare...........");
            localServer = new ServerSocket(mLocalPort, 1, InetAddress.getByName(LOCAL_IP_ADDRESS));
            printLog("..........localServer start finish...........");
            mGoOnListening = true;

            // start a local request listener on a new thread
            new Thread() {
                public void run() {
                    try {
                        while (true) {
                            if (mGoOnListening == false) {
                                break;
                            }

                            // recept a new local http request socket
                            Socket localSocket = localServer.accept();
                            printLog("..........localSocket connected..........");

                            // start a proxy communicator on a new thread
                            // to handle request/response
                            LocalRemoteIOComunicator communicator = new LocalRemoteIOComunicator(HttpGetProxy.this, localSocket, mLocalPort);
                            comunicatorList.add(communicator);
                            new Thread(communicator).start();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void printStateInfo() {
        printLog("localServer isClosed = " + localServer.isClosed());
    }

    /**
     * close the local server socket
     */
    public void stop() {
        printLog("..........localServer stop..........");
        mGoOnListening = false;
        if (localServer != null) {
            try {
                localServer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void closeOpenedStreams() {
        for (int i = 0, len = comunicatorList.size(); i < len; i++) {
            comunicatorList.get(i).closeIO();
        }
        comunicatorList.clear();
    }

    public void printLog(String msg) {
        System.err.println("HttpGetProxy [port:" + mLocalPort + "] content = " + msg);
    }
}
