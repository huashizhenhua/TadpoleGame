package network;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;

import org.omg.CORBA.Environment;

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

	private static final String MUSIC_CACHE_DIR = "/tadpole/music";

	private ServerSocket localServer = null;
	private int mRemotePort;
	private int mLocalPort;
	private boolean mGoOnListening = false;
	private String mRemoteHost;

	/**
	 * music cache directory
	 */
	private String mCacheDir;

	public HttpGetProxy(int localPort) {
		mLocalPort = localPort;
		mRemotePort = REMOTE_DEFAULT_PORT;
	}

	public void setCacheDir(String cacheDir) {
		mCacheDir = cacheDir;
	}

	public String defaultCacheDir() {
		String path = "D:/test/";
		File file = new File(path);
		if (!file.exists()) {
			file.mkdirs();
		}
		return path;
		// if
		// (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()))
		// {
		// File sdFile = Environment.getExternalStorageDirectory();
		// String sdDirPath = sdFile.getAbsolutePath() + File.separator +
		// MUSIC_CACHE_DIR;
		// File file = new File(sdDirPath);
		// if (file.exists()) {
		// file.mkdirs();
		// }
		// return sdDirPath;
		// }
		// return null;
	}

	public ByteArrayOutputStream readCache(String musicUrl) {
		if (mCacheDir == null) {
			mCacheDir = defaultCacheDir();
		}
		String md5MusicUrl = mCacheDir + File.separator
				+ MD5Util.getMD5(musicUrl.getBytes());
		File md5MusicFile = new File(md5MusicUrl);
		FileInputStream fileOS;
		ByteArrayOutputStream baos = null;
		try {
			if (!md5MusicFile.exists()) {
				return null;
			}
			fileOS = new FileInputStream(md5MusicFile);
			baos = new ByteArrayOutputStream();
			byte[] buffer = new byte[5120];
			int byteRead;
			while ((byteRead = fileOS.read(buffer)) != -1) {
				baos.write(buffer, 0, byteRead);
			}
		} catch (FileNotFoundException e) {
			System.err.println(e.getMessage());
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
		return baos;
	}

	public void saveCache(String musicUrl, ByteArrayOutputStream cacheOutput) {
		if (mCacheDir == null) {
			mCacheDir = defaultCacheDir();
		}
		String md5MusicUrl = mCacheDir + File.separator
				+ MD5Util.getMD5(musicUrl.getBytes());
		File md5MusicFile = new File(md5MusicUrl);
		FileOutputStream fileOS = null;
		try {
			if (md5MusicFile.exists() && md5MusicFile.isDirectory()) {
				md5MusicFile.delete();
				md5MusicFile.createNewFile();
			} else {
				md5MusicFile.createNewFile();
			}
			fileOS = new FileOutputStream(md5MusicFile);
			fileOS.write(cacheOutput.toByteArray());
			fileOS.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(fileOS);
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

	/**
	 * return host with port .e.g www.baidu.com:80
	 * 
	 * @param fromUrl
	 * @return
	 */
	public String getHostWithPort(String fromUrl) {
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
			} else {
				addr = addr;
			}
			proxyUrl = fromUrl.replace(addr, LOCAL_IP_ADDRESS + ":"
					+ mLocalPort);
			proxyUrl = proxyUrl + HttpParser.hackString(addr)
					+ (rootUrl != null ? HttpParser.metaStr(rootUrl) : "");
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
			localServer = new ServerSocket(mLocalPort, 1,
					InetAddress.getByName(LOCAL_IP_ADDRESS));
			printLog("..........localServer start finish...........");
			mGoOnListening = true;

			// start a local request listener on a new thread
			new Thread() {
				public void run() {
					try {
						while (true) {
							// recept a new local http request socket
							Socket localSocket = localServer.accept();
							printLog("..........localSocket connected..........");

							// start a proxy communicator on a new thread
							// to handle request/response
							LocalRemoteIOComunicator communicator = new LocalRemoteIOComunicator(
									HttpGetProxy.this, localSocket, mLocalPort);
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

	public void printLog(String msg) {
		System.err.println("HttpGetProxy [port:" + mLocalPort + "] content = "
				+ msg);
	}
}
