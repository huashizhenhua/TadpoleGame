package network;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URL;

import network.HttpParser.StatusLine;

/**
 * <br>=
 * ========================= <br>
 * author：Zenip <br>
 * email：lxyczh@gmail.com <br>
 * create：2013-2-1 <br>=
 * =========================
 */
public class LocalRemoteIOComunicator implements Runnable {
	private InputStream mInputStreamLocal = null;
	private OutputStream mOutputStreamlocal = null;
	private int mLocalPort = -1;
	private InputStream mInputStreamRemote;
	private OutputStream mOutputStreamRemote;
	private Socket mRemoteSocket;
	private HttpGetProxy mHttpProxy;
	private Socket mLocalSocket;

	private String mRootUrl;

	public String mCacheBelongUrl = "";

	/**
	 * http range header
	 */
	private String mRange = "";

	public LocalRemoteIOComunicator(HttpGetProxy httpProxy, Socket localSocket,
			int localPort) {
		mHttpProxy = httpProxy;
		mLocalPort = localPort;
		mLocalSocket = localSocket;
	}

	@Override
	public void run() {
		try {
			System.out.println("=====>init local Socket I/O");
			// mLocalSocket.setSoTimeout(20000);
			mInputStreamLocal = mLocalSocket.getInputStream();
			mOutputStreamlocal = mLocalSocket.getOutputStream();
			LocalRemoteIOComunicator.this.requestFromLocalToRemote();
		} catch (Exception e) {
			e.printStackTrace();
			closeIO();
		}
	}

	public void connectRemote(String remoteHost, int remotePort)
			throws IOException {
		SocketAddress address = new InetSocketAddress(remoteHost, remotePort);
		// --------连接目标服务器---------//
		mRemoteSocket = new Socket();
		mRemoteSocket.setSoTimeout(20000);
		mRemoteSocket.connect(address);
		System.out.println("=====>remote Server connected");
		mOutputStreamRemote = mRemoteSocket.getOutputStream();
		mInputStreamRemote = mRemoteSocket.getInputStream();
		System.out.println("=====>init remote Server I/O");
	}

	public static int count = 0;

	public void requestFromLocalToRemote() throws IOException,
			InterruptedException {
		count++;
		System.out.println("=====>local start to receive");
		int bytes_read;
		byte[] local_request = new byte[5120];
		String buffer = "";
		System.out.println("=====>local write message header");
		while ((bytes_read = mInputStreamLocal.read(local_request)) != -1) {
			String str = new String(local_request);
			System.out.println("----->localSocket:\n" + str);
			// Log.e("localSocket---->",str);
			buffer = buffer + str;
			if (buffer.contains("GET") && buffer.contains("\r\n\r\n")) {
				// ---把request中的本地ip改为远程ip---//
				String messageHeaer = buffer;
				String hackedUri = HttpParser.getRequestLine(messageHeaer).uri;
				String remoteAddr = HttpParser
						.getRemoteAddrFromHackedUri(hackedUri);
				String[] arr = remoteAddr.split(":");

				String headerRange = HttpParser.getHeader("Range",
						messageHeaer, "");
				System.out.println("----->localRange:\n" + headerRange);
				mRange = headerRange;

				String remoteHost = arr[0];
				String uri = HttpParser.getRemoteUri(hackedUri);
				String musicUrl = remoteAddr + uri;

				mRootUrl = HttpParser.getMetaAddr(hackedUri);
				if (mRootUrl == null) {
					mRootUrl = musicUrl;
				}
				final ByteArrayOutputStream cache = mHttpProxy
						.readCache(mRootUrl + mRange);
				if (cache != null && cache.size() != 0) {
					System.out.println("=====>local request has cache rootUrl");
					new Thread(new Runnable() {
						@Override
						public void run() {
							byte[] cacheBytes = cache.toByteArray();
							try {
								mOutputStreamlocal.write(cacheBytes);
							} catch (IOException e) {
								e.printStackTrace();
							} finally {
								closeIO();
							}
						}
					}).start();
					return;
				} else {
					System.out
							.println("=====>local request has no cache rootUrl");
				}

				System.out.println("----->localMusicUrl:\n " + musicUrl);

				if (messageHeaer.contains("GET")
						&& messageHeaer.contains("\r\n\r\n")) {
					// ---把request中的本地ip改为远程ip---//
					messageHeaer = messageHeaer.replace(hackedUri, uri);
					messageHeaer = messageHeaer.replace(
							HttpGetProxy.LOCAL_IP_ADDRESS + ":" + mLocalPort,
							remoteHost);
					System.out.println("已经替换IP");
				}

				System.out.println("----->localSocket:\n" + messageHeaer);
				if (arr.length >= 2) {
					connectRemote(arr[0], Integer.valueOf(arr[1]));
				} else {
					connectRemote(arr[0], HttpGetProxy.REMOTE_DEFAULT_PORT);
				}

				// wait until reading response thread ready
				synchronized (this) {
					// since response reading has a while statement
					// we must read response in a new thread
					new Thread(new Runnable() {
						@Override
						public void run() {
							try {
								LocalRemoteIOComunicator.this
										.responseFromRemoteToLocal();
							} catch (IOException e) {
								e.printStackTrace();
							} finally {
								closeIO();
							}
						}
					}).start();
					this.wait();
				}
				mOutputStreamRemote.write(messageHeaer.getBytes());
				mOutputStreamRemote.flush();
				continue;
			} else {
				mOutputStreamRemote.write(buffer.getBytes());
				mOutputStreamRemote.flush();
			}
		}
		System.out.println("=====>local finish receive");
	}

	public void responseFromRemoteToLocal() throws IOException {
		int bytes_read;
		System.out.println("=====>remote start to receive");

		// notify response ready
		synchronized (this) {
			this.notify();
		}

		byte[] remote_content_buffer = new byte[5120];

		// remote message header
		byte[] messageHeaderRaw = HttpParser
				.readMessageHeaderRaw(mInputStreamRemote);
		String messageHeader = new String(messageHeaderRaw);
		StatusLine statusLine = HttpParser.getStatusLine(messageHeader);
		System.out.println("statusLine = " + statusLine);
		if (statusLine.statusCode == 302) {
			String location = HttpParser.getLocation(messageHeader);
			URL locationUrl = null;
			System.out.println("=====>remote has 302 redirect. location = "
					+ location);
			if (location != null) {
				try {
					String remoteAddr = mHttpProxy.getHostWithPort(location);
					String proxyUrl = mHttpProxy
							.getProxyUrl(location, mRootUrl);
					messageHeader = messageHeader.replace(location, proxyUrl);
				} catch (Exception e) {
					System.out.println("remote start proxy exception");
					e.printStackTrace();
				}
			}
		}

		ByteArrayOutputStream baosCache = null;
		if (statusLine.statusCode == 200 || statusLine.statusCode == 206) {
			baosCache = new ByteArrayOutputStream();
			baosCache.write(messageHeaderRaw);
		}

		System.out.println("----->remoteSocket messageHeader ..." + messageHeader);
		System.out.println("----->remoteSocket rootUrl ..." + mRootUrl);
		mOutputStreamlocal.write(messageHeader.getBytes());
		System.out.println("======>remote read message content");
		while ((bytes_read = mInputStreamRemote.read(remote_content_buffer)) != -1) {
			if (statusLine.statusCode == 200 || statusLine.statusCode == 206) {
				System.out.print(new String(remote_content_buffer));
				baosCache.write(remote_content_buffer, 0, bytes_read);
			}
			mOutputStreamlocal.write(remote_content_buffer, 0, bytes_read);
		}

		if (statusLine.statusCode == 200 || statusLine.statusCode == 206) {
			System.out.println("=====>remote save cache mRootUrl = " + mRootUrl
					+ ", range = " + mRange);
			mHttpProxy.saveCache(mRootUrl + mRange, baosCache);
		}
		mOutputStreamlocal.flush();

		System.out.println("=====>remote finish receive...........");
	}

	public void closeIO() {
		close(mInputStreamLocal);
		close(mOutputStreamlocal);
		close(mInputStreamRemote);
		close(mOutputStreamRemote);
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
