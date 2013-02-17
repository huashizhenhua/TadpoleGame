package com.itap.voiceemoticon.activity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpParser {

    private static final String HEADER_PARSE_REGEX = "%s.*?:(.*)[\\r\\n | \\n]";
    public static final String HACK_KEY = "hack";
    public static final String HACK_REGEX = HACK_KEY + "(.*)" + HACK_KEY + "?";

    public static final String META_KEY = "meta";
    public static final String META_REGEX = META_KEY + "(.*)" + META_KEY + "?";

    public static class Range {
        public int start = 0;
        public int end = 0;

        public boolean isEmpty() {
            return start == 0 && end == 0;
        }

        @Override
        public String toString() {
            return "{start:" + start + ", end : " + end + "}";
        }
    }

    public static class RequestLine {
        public static final String METHOD_GET = "GET";
        public String method = METHOD_GET;
        public String protocol = "HTTP/1.1";
        public String uri = "";

        @Override
        public String toString() {
            return "[protocol = " + protocol + ", uri = " + uri + ", method=" + method + "]";
        }
    }

    public static class StatusLine {
        public static final String METHOD_GET = "GET";
        public int statusCode = 400;
        public String protocol = "HTTP/1.1";
        public String msg = "OK";

        @Override
        public String toString() {
            return "[statusCode = " + statusCode + ", protocol = " + protocol + ", msg=" + msg + "]";
        }
    }

    public static String hackString(String addr) {
        return HACK_KEY + addr + HACK_KEY;
    }

    public static String getRemoteUri(String uri) {
        int index = uri.indexOf(HACK_KEY);
        if (index >= 0) {
            return (String) uri.subSequence(0, index);
        }
        return null;
    }

    public static String getRemoteAddrFromHackedUri(String requestLine) {
        Pattern p = Pattern.compile(HACK_REGEX);
        Matcher m = p.matcher(requestLine);
        if (m.find()) {
            return m.group(1);
        }
        return null;
    }

    public static String metaStr(String addr) {
        return META_KEY + addr + META_KEY;
    }

    public static String getMetaAddr(String requestLine) {
        Pattern p = Pattern.compile(META_REGEX);
        Matcher m = p.matcher(requestLine);
        if (m.find()) {
            return m.group(1);
        }
        return null;
    }

    public static RequestLine getRequestLine(InputStream is) throws IOException {
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        int byteRead;
        String statuLineRawStr = "";
        while ((byteRead = is.read()) != -1) {
            if (byteRead == '\n') {
                statuLineRawStr = bao.toString();
                break;
            }
            bao.write(byteRead);
        }
        return getRequestLine(statuLineRawStr);
    }

    public static RequestLine getRequestLine(String requestLineRawStr) {
        RequestLine statusLine = new RequestLine();
        requestLineRawStr = requestLineRawStr.replace("\n", "");
        String[] arr = requestLineRawStr.split(" ");
        if (arr.length >= 3) {
            statusLine.method = arr[0];
            statusLine.uri = arr[1];
            statusLine.protocol = arr[2];
        }
        return statusLine;
    }

    public static StatusLine getStatusLine(InputStream is) throws IOException {
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        int byteRead;
        String statuLineRawStr = "";
        while ((byteRead = is.read()) != -1) {
            if (byteRead == '\n') {
                statuLineRawStr = bao.toString();
                break;
            }
            bao.write(byteRead);
        }
        return getStatusLine(statuLineRawStr);
    }

    public static StatusLine getStatusLine(String statuLineRawStr) {
        StatusLine statusLine = new StatusLine();
        int index = statuLineRawStr.indexOf("\n");
        if (index >= 0) {
            statuLineRawStr = statuLineRawStr.substring(0, index);
            statuLineRawStr = statuLineRawStr.replace("\n", "");
        }
        String[] arr = statuLineRawStr.split(" ");
        if (arr.length >= 3) {
            statusLine.protocol = arr[0];
            statusLine.statusCode = Integer.valueOf(arr[1]);
            statusLine.msg = arr[2];
        }
        return statusLine;
    }

    public static String getUri(String messageHeaer) {
        try {
            int indexChangeLine = messageHeaer.indexOf('\n');
            String requestLine = (String) messageHeaer.subSequence(0, indexChangeLine);
            String[] arr = requestLine.split(" ");
            return arr[1].trim();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * read http message header
     * 
     * @param is
     * @return message header bytes
     * @throws IOException
     */
    public static byte[] readMessageHeaderRaw(InputStream is) throws IOException {
        /*
         * http contain two part: 1 message header 2 message content
         * 
         * they are split by "\r\n\r\n"
         */
        int bytes_read;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ArrayList<Integer> iList = new ArrayList<Integer>();
        int partIndex = 0;
        while ((bytes_read = is.read()) != -1) {
            byteArrayOutputStream.write(bytes_read);
            if (bytes_read == '\r' && iList.size() == 0) {
                iList.add(bytes_read);
            } else if (bytes_read == '\n' && iList.size() == 1) {
                iList.add(bytes_read);
            } else if (bytes_read == '\r' && iList.size() == 2) {
                iList.add(bytes_read);
            } else if (bytes_read == '\n' && iList.size() == 3) {
                iList.add(bytes_read);
            } else {
                iList.clear();
            }
            // get message header
            if (iList.size() == 4 && partIndex == 0) {
                return byteArrayOutputStream.toByteArray();
            }
        }
        return null;
    }

    public static int getContentLength(String response) {
        String headerRegex = String.format(HEADER_PARSE_REGEX, "Content-Length");
        Pattern p = Pattern.compile(headerRegex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = p.matcher(response);
        if (matcher.find() && matcher.groupCount() >= 1) {
            String value = matcher.group(1);
            return Integer.valueOf(value.trim());
        }
        return -1;
    }

    public static String getLocation(String response) {
        String headerRegex = String.format(HEADER_PARSE_REGEX, "Location");
        Pattern p = Pattern.compile(headerRegex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = p.matcher(response);
        if (matcher.find() && matcher.groupCount() >= 1) {
            String value = matcher.group(1);
            return value;
        }
        return null;
    }

    public static String getHeader(String headerName, String bodySegment, String defaultValue) {
        String headerRegex = String.format(HEADER_PARSE_REGEX, headerName);
        Pattern p = Pattern.compile(headerRegex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = p.matcher(bodySegment);
        if (matcher.find() && matcher.groupCount() >= 1) {
            String value = matcher.group(1);
            return value.trim();
        }
        return defaultValue;
    }


    public static Range getRange(String body) {
        String rangeVal = getHeader("Range", body, "bytes=0-");
        rangeVal = rangeVal.split("=")[1];
        Range range = new Range();
        String[] rangeArr = rangeVal.split("-");
        range.start = Integer.valueOf(rangeArr[0].trim());
        if (rangeArr.length >= 2) {
            range.end = Integer.valueOf(rangeArr[1].trim());
        }
        return range;
    }

    public static String replaceStatusCode(String data, int statusCode) {
        String s = new String(data);
        Pattern p = Pattern.compile("[0-9]{3}?");
        Matcher m = p.matcher(s);
        if (m.find()) {
            s = m.replaceFirst("" + statusCode + "");
        }
        return s;
    }

    public static String addHeader(String data, String headerName, String headerValue) {
        int clIndex = data.indexOf('\n');
        StringBuilder sb = new StringBuilder(data);
        sb.insert(clIndex + 1, headerName + ": " + headerValue + " \r\n");
        return sb.toString();
    }

    public static String removeHeader(String data, String headerName) {
        String headerRegex = String.format(HEADER_PARSE_REGEX, headerName);
        Pattern p = Pattern.compile(headerRegex);
        Matcher m = p.matcher(data);
        String s = data;
        if (m.find()) {
            s = m.replaceFirst("");
        }
        return s;
    }

    public static String replaceOrAddHeader(String data, String headerName, String headerValue) {
        int hStartI = data.toLowerCase().indexOf(headerName.toLowerCase());
        if (hStartI != -1) {
            hStartI += headerName.length();
            StringBuilder sb = new StringBuilder(data);
            int hEndI = sb.indexOf("\r\n", hStartI);
            sb.replace(hStartI + 2, hEndI, headerValue);
            return sb.toString();
        } else {
            return addHeader(data, headerName, headerValue);
        }
    }

    public static String addContentRange(String data, int start, int len, int total) {
        return HttpParser.addHeader(data, "Content-Range", "bytes " + start + "-" + (start + len - 1) + "/" + total);
    }

    public static void main(String[] args) {
        String bodySegment = "Location: http://www.baidu.com \r\n";
        String location = getLocation(bodySegment);
        System.out.println("Location = " + location);

        String requestLineStrRaw = "GET A/B/C/CC HTTP/1.1\n";
        System.out.println("requestLine = " + getRequestLine(requestLineStrRaw));

        String statusLineStrRaw = "HTTP/1.1 200 OK\n";
        System.out.println("statusLine = " + getStatusLine(statusLineStrRaw));

        String hackedUrl = "sdfdsfs /sddddhackwww.baidu.com:80hackmetabb.commeta sdfsdfsd";


        System.out.println("url = " + HttpParser.getRemoteAddrFromHackedUri(hackedUrl));
        System.out.println("url = " + HttpParser.getMetaAddr(hackedUrl));


        String rangeStr = "Range: bytes=0 \r\n";
        String range = getHeader("Range", rangeStr, "0");
        System.out.println("range = " + getRange(rangeStr));
        System.out.println("changeStatusCode = " + replaceStatusCode(statusLineStrRaw, 203));
        System.out.println(addHeader(statusLineStrRaw, "Content-Range", "77777"));


        String headerStr = replaceOrAddHeader(requestLineStrRaw + rangeStr, "Range", "dfddsdfsd");
        System.out.println("headerStr = " + headerStr);

        headerStr = replaceOrAddHeader(requestLineStrRaw + rangeStr, "Content-Length", "10000");
        System.out.println("headerStr = " + headerStr);

        System.out.println((byte) '\r');



        URL url;
        try {
            url = new URL("http://www.baidu.com/ttt/index.html?dd=1121#121212121");
            int port = url.getPort();
            String host = url.getHost();
            System.out.println("port = " + port);
            System.out.println("host = " + host);
            System.out.println("uri = " + url.getPath());
            System.out.println("query = " + url.getQuery());
            System.out.println("ref = " + url.getRef());
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        System.out.println("uri = " + HttpGetProxy.getUriFromUrl("http://www.baidu.com/ttt/"));
        System.out.println("uri = " + HttpGetProxy.getUriFromUrl("http://www.baidu.com"));
    }
}
