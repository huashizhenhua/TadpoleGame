package com.itap.voiceemoticon.activity;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.Properties;

import android.os.Environment;

import com.itap.voiceemoticon.api.util.MD5Util;

/**
 * <br>=
 * ========================= <br>
 * author：Zenip <br>
 * email：lxyczh@gmail.com <br>
 * create：2013-2-11 <br>=
 * =========================
 */
public class HttpCache {
	private static final String CONTENT_RANGE = "Content-Range";

	private static final String CONTENT_LENGTH = "Content-Length";

	private static final String MUSIC_CACHE_DIR = "/tadpole/music";

	private String mCachePath;
	private HttpGetProxy mHttpGetProxy;

	public HttpCache(String cachePath, HttpGetProxy httpGetProxy) {
		mCachePath = cachePath;
		mHttpGetProxy = httpGetProxy;
	}

	public boolean exist() {
		final ByteCache cacheHeader = readCacheResponseHeader(mCachePath);
		final ByteCache cacheBody = readCacheBody(mCachePath);
		return cacheHeader.isNotEmpty() && cacheBody.isNotEmpty();
	}

	public ByteCache readCacheResponseHeader(String musicUrl) {
		return readCache(musicUrl, ".header");
	}

	public ByteCache readCacheBody(String musicUrl) {
		return readCache(musicUrl, ".body");
	}

	public String getCacheHeader() {
		final ByteCache cacheHeader = readCacheResponseHeader(mCachePath);
		byte[] cacheHeaderBytes = cacheHeader.getBytes();
		return new String(cacheHeaderBytes);
	}

	public int getContentLength() {
		Properties properties = readProperties();
		String contentLength = properties.getProperty(CONTENT_LENGTH);
		if ("".equals(contentLength) || contentLength == null) {
			contentLength = "0";
		}
		return Integer.valueOf(contentLength);
	}

	public boolean writeHeaderWithModified(final OutputStream dstOutput,
			int contentLength, int statusCode, int rangeStart)
			throws IOException {
		System.out
				.println("=====>local writing response header by using cache");
		final ByteCache cacheHeader = readCacheResponseHeader(mCachePath);
		String header = cacheHeader.getString();

		header = HttpParser.replaceStatusCode(header, statusCode);
		if (rangeStart == 0) {
			header = HttpParser.removeHeader(header, CONTENT_RANGE);
			header = HttpParser.replaceOrAddHeader(header, CONTENT_LENGTH, ""
					+ contentLength);
		} else {
			int tmpContentLength = contentLength - rangeStart;
			header = HttpParser.addContentRange(header, rangeStart,
					tmpContentLength, contentLength);
			header = HttpParser.replaceOrAddHeader(header, CONTENT_LENGTH, ""
					+ tmpContentLength);
		}

		System.out.println("----->localCacheHeader:" + header);
		final byte[] cacheHeaderBytes = header.getBytes();
		dstOutput.write(cacheHeaderBytes);
		return true;
	}

	public long getBodyLength() {
		final ByteCache cacheBody = readCacheBody(mCachePath);
		return cacheBody.getLength();
	}

	/**
	 * @param dstOutput
	 * @param rangeStart
	 * @return
	 * @throws IOException
	 */
	public int writeBodyCacheToLocalOut(final OutputStream dstOutput,
			long rangeStart, int byteCount) throws IOException {
		final ByteCache cacheHeader = readCacheResponseHeader(mCachePath);
		final ByteCache cacheBody = readCacheBody(mCachePath);
		System.out.println("=====>local write cache body range = ("
				+ rangeStart + ", " + cacheBody.getLength() + ")");
		int byteRead = 0;
		final byte[] cacheHeaderBytes = cacheHeader.getBytes();
		int contentLength = HttpParser.getContentLength(new String(
				cacheHeaderBytes));
		int byteCountToRead = byteCount;
		int lastByteRead = 0;
		System.out.println("----->local cache response content-length:"
				+ contentLength + " cacheBody length:" + cacheBody.getLength());
		if (contentLength != -1) {
			byte[] buffer = new byte[5120];
			RandomAccessFile ras = cacheBody.openRAS();
			ras.seek(rangeStart);
			int tmpByteRead = 0;
			while ((tmpByteRead = ras.read(buffer)) != -1) {
				lastByteRead = byteRead;
				byteRead += tmpByteRead;
				if (byteRead > byteCountToRead) {
					int writeByteCount = byteCountToRead - lastByteRead;
					dstOutput.write(buffer, 0, writeByteCount);
				} else {
					dstOutput.write(buffer, 0, tmpByteRead);
				}
			}
			try {
				ras.close();
			} catch (Exception e) {
				e.printStackTrace();
            }
        }
		System.out.println("=====>write cache body to local finish . Totally byteRead = "
						+ byteRead);
		return byteRead;
	}

	public Properties readProperties() {
		String cacheFilePath = getCacheFilePath(mCachePath, ".ini");

		System.out.println("cacheFilePath = " + cacheFilePath);

		Properties properties = new Properties();
		FileInputStream in = null;
		try {

			File f = new File(cacheFilePath);
			if (!f.exists()) {
				f.createNewFile();
			}
			in = new FileInputStream(new File(cacheFilePath));
			properties.load(in);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (in != null) {
					in.close();
					in = null;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return properties;
	}

	public void writeProperties(String musicUrl, Properties properties) {
		String cacheFilePath = getCacheFilePath(musicUrl, ".ini");
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(new File(cacheFilePath));
			properties.store(out, "");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (out != null) {
					out.close();
					out = null;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	public void writeCacheResponseHeader(byte[] data) {
		writeCache(mCachePath, ".header", 0, data);
	}

	public void writeCacheResponseBody(int seekPos, byte[] data) {
		writeCache(mCachePath, ".body", seekPos, data);
	}

	public RandomAccessFile openCacheResponseBody() {
		String cacheFilePath = getCacheFilePath(mCachePath, ".body");
		System.out.println("cacheFilePath = " + cacheFilePath);

		File cacheFile = new File(cacheFilePath);
		RandomAccessFile ras = null;
		try {
			if (cacheFile.exists() && cacheFile.isDirectory()) {
				cacheFile.delete();
				cacheFile.createNewFile();
			} else {
				cacheFile.createNewFile();
			}
			ras = new RandomAccessFile(cacheFile, "rws");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ras;
	}

	// ----------------------------------------------
	// 读写基础方法
	// ----------------------------------------------

	private String getCacheFilePath(String key, String suffix) {
		String cacheFilePath = defaultCacheDir() + File.separator
				+ MD5Util.getMD5(key.getBytes()) + suffix;
		return cacheFilePath;
	}

	private void writeCache(String key, String suffix, int seekPos, byte data[]) {
		String cacheFilePath = getCacheFilePath(key, suffix);
		File cacheFile = new File(cacheFilePath);
		cacheFile.mkdirs();
		RandomAccessFile ras = null;
		try {
			if (cacheFile.exists() && cacheFile.isDirectory()) {
				cacheFile.delete();
				cacheFile.createNewFile();
			} else {
				cacheFile.createNewFile();
			}
			ras = new RandomAccessFile(cacheFile, "rws");
			ras.seek(seekPos);
			ras.write(data);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(ras);
		}
	}

	private ByteCache readCache(String key, String suffix) {
		String cacheFilePath = getCacheFilePath(key, suffix);
		File cacheFile = new File(cacheFilePath);
		return new ByteCache(cacheFile);
	}

	private String defaultCacheDir() {
		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {
			File sdFile = Environment.getExternalStorageDirectory();
			String sdDirPath = sdFile.getAbsolutePath() + MUSIC_CACHE_DIR;
			File file = new File(sdDirPath);
			if (false == file.exists()) {
				file.mkdirs();
			}
			return sdDirPath;
		}
		return null;
	}

	private void close(Closeable obj) {
		try {
			if (obj != null) {
				obj.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
