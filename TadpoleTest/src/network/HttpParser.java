package network;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class HttpParser {

    private static final String locationRegex = "%s.*?:(.*)[\\r\\n\\r\\n | \\n]";
    public static final String HACK_KEY = "hack";
    public static final String HACK_REGEX = HACK_KEY + "(.*)" + HACK_KEY + "?";

    public static final String META_KEY = "meta";
    public static final String META_REGEX = META_KEY + "(.*)" + META_KEY + "?";


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
         * http contain two part:
         * 1 message header
         * 2 message content
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


    public static String getLocation(String bodySegment) {
        String headerRegex = String.format(locationRegex, "Location");
        Pattern p = Pattern.compile(headerRegex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = p.matcher(bodySegment);
        if (matcher.find() && matcher.groupCount() >= 1) {
            String locationUrl = matcher.group(1);
            return locationUrl;
        }
        return null;
    }

    public static String getHeader(String headerName, String bodySegment, String defaultValue) {
        String headerRegex = String.format(locationRegex, headerName);
        Pattern p = Pattern.compile(headerRegex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = p.matcher(bodySegment);
        if (matcher.find() && matcher.groupCount() >= 1) {
            String locationUrl = matcher.group(1);
            return locationUrl.trim();
        }
        return defaultValue;
    }

    public static void main(String[] args) {
        String bodySegment = "Location: http://www.baidu.com \r\n\r\n";
        String location = getLocation(bodySegment);
        System.out.println("Location = " + location);

        String requestLineStrRaw = "GET A/B/C/CC HTTP/1.1\n";
        System.out.println("requestLine = " + getRequestLine(requestLineStrRaw));


        String statusLineStrRaw = "HTTP/1.1 200 OK\n";
        System.out.println("statusLine = " + getStatusLine(statusLineStrRaw));

        String hackedUrl = "sdfdsfs /sddddhackwww.baidu.com:80hackmetabb.commeta sdfsdfsd";

        System.out.println("url = " + HttpParser.getRemoteAddrFromHackedUri(hackedUrl));
        System.out.println("url = " + HttpParser.getMetaAddr(hackedUrl));

    }
}
