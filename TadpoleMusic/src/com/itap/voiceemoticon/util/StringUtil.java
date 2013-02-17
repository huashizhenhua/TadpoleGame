package com.itap.voiceemoticon.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.util.Log;

/**
 * 
 * <br>==========================
 * <br> author：Zenip
 * <br> email：lxyczh@gmail.com
 * <br> create：2013-1-26下午3:48:59
 * <br>==========================
 */

public class StringUtil {

    /** 空字符串。 */
    public static final String EMPTY_STRING = "";

    /**
     * 判断配置字符串中是否包含对应的配置项.
     * <br> StringUtil.defStrContains("1|2,3"|"1","|") =true
     * <br> StringUtil.defStrContains("1|2|3","3","|") =true
     * <br> StringUtil.defStrContains("1|2|3","2","|") =true
     * <br> StringUtil.defStrContains("1|2|3","4","|") =false
     * <br> StringUtil.defStrContains("1|2|3","","|") =false
     * <br> StringUtil.defStrContains("1|2|3",null,"|") =false
     * <br> <b>StringUtil.defStrContains("1|2|3","1|2","|") =true</b>
     * <br> StringUtil.defStrContains("1|2|3","|2|","|") =true
     * <br> StringUtil.defStrContains("1|2|3","|2|",",") =true
     * <br> StringUtil.defStrContains("1|2|3","2",",") =false
     * 
     * @param defStr
     *            配置内容
     * @param defValue
     *            配置项的关键字
     * @param splitSyb
     *            分隔符号
     * @return
     */
    public static boolean defStrContains(String defStr, String defValue, String splitSyb) {
        return (splitSyb + defStr + splitSyb).contains(splitSyb + defValue + splitSyb);
    }

    /**
     * 将邮箱中间部分替换成星号
     * 
     * <pre>
     * StringUtil.asteriskReplace("upayTest@ucweb.com") = "upa*****@ucweb.com"
     * </pre>
     * 
     * @param str
     *            被替换字符串
     * 
     * @return 前三位或@后明文，中间为密文，若@前少于等于三位则将@前全部替换为*(fyg@ucweb.com--->***@ucweb.com)
     *         不符合邮箱规则返回asteriskReplace(str) 若传入参数为null，直接返回null
     */
    public static String asteriskEmailReplace(String str) {
        if (StringUtil.isBlank(str))
            return null;
        StringBuffer replace = new StringBuffer();
        if (str.matches("^.+@.+\\..+$")) {
            if (str.indexOf("@") > 3)
                replace.append(str.substring(0, 3));
            replace.append("*****");
            replace.append(str.substring(str.indexOf("@"), str.length()));
        } else {
            return asteriskReplace(str);
        }
        return replace.toString();
    }

    /**
     * 将字符串中间部分替换成星号.
     * <br />若长度大于6位则将中间部分替换成星号，保留前三位后三位,其他替换为4个星号
     * <br />若长度大于2位且小于等于6位，则保留第一位,其他替换为3个星号
     * <br />若长度小于等于2位则替换为2个星号
     * <pre>
     * str.length()>6:
     * StringUtil.asteriskReplace("12345678") = "123****678"
     * 2<str.length()<=6:
     * StringUtil.asteriskReplace("1234") = "1***"
     * str.length()<=2:
     * StringUtil.asteriskReplace("12") = "**"
     * </pre>
     * 
     * @param str
     *            被替换字符串
     * 
     * @return
     */
    public static String asteriskReplace(String str) {
        if (StringUtil.isBlank(str))
            return null;
        StringBuffer replace = new StringBuffer();
        if (str.length() > 6) {
            replace.append(str.substring(0, 3));
            replace.append("****");
            replace.append(StringUtil.rightTrunc(str, 3));
        } else if (str.length() > 2) {
            replace.append(str.substring(0, 1)).append("***");
        } else {
            replace.append("**");

        }
        return replace.toString();
    }

    /**
     * 比较两个字符串（大小写敏感）。
     * 
     * <pre>
     * StringUtil.equals(null, null) = true
     * StringUtil.equals(null, "abc") = false
     * StringUtil.equals("abc", null) = false
     * StringUtil.equals("abc", "abc") = true
     * StringUtil.equals("abc", "ABC") = false
     * </pre>
     * 
     * @param str1
     *            要比较的字符串1
     * @param str2
     *            要比较的字符串2
     * 
     * @return 如果两个字符串相同，或者都是<code>null</code>，则返回<code>true</code>
     */
    public static boolean equals(String str1, String str2) {
        if (str1 == null) {
            return str2 == null;
        }

        return str1.equals(str2);
    }

    /**
     * 比较两个字符串（大小写不敏感）。
     * 
     * <pre>
     * StringUtil.equalsIgnoreCase(null, null) = true
     * StringUtil.equalsIgnoreCase(null, "abc") = false
     * StringUtil.equalsIgnoreCase("abc", null) = false
     * StringUtil.equalsIgnoreCase("abc", "abc") = true
     * StringUtil.equalsIgnoreCase("abc", "ABC") = true
     * </pre>
     * 
     * @param str1
     *            要比较的字符串1
     * @param str2
     *            要比较的字符串2
     * 
     * @return 如果两个字符串相同，或者都是<code>null</code>，则返回<code>true</code>
     */
    public static boolean equalsIgnoreCase(String str1, String str2) {
        if (str1 == null) {
            return str2 == null;
        }

        return str1.equalsIgnoreCase(str2);
    }

    /**
     * 检查字符串是否是空白：<code>null</code>、空字符串<code>""</code>或只有空白字符。
     * 
     * <pre>
     * StringUtil.isBlank(null) = true
     * StringUtil.isBlank("") = true
     * StringUtil.isBlank(" ") = true
     * StringUtil.isBlank("bob") = false
     * StringUtil.isBlank("  bob  ") = false
     * </pre>
     * 
     * @param str
     *            要检查的字符串
     * 
     * @return 如果为空白, 则返回<code>true</code>
     */
    public static boolean isBlank(String str) {
        int length;

        if ((str == null) || ((length = str.length()) == 0)) {
            return true;
        }

        for (int i = 0; i < length; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return false;
            }
        }

        return true;
    }

    /**
     * 检查字符串是否不是空白：<code>null</code>、空字符串<code>""</code>或只有空白字符。
     * 
     * <pre>
     * StringUtil.isNotBlank(null) = false
     * StringUtil.isNotBlank("") = false
     * StringUtil.isNotBlank(" ") = false
     * StringUtil.isNotBlank("bob") = true
     * StringUtil.isNotBlank("  bob  ") = true
     * </pre>
     * 
     * @param str
     *            要检查的字符串
     * 
     * @return 如果为空白, 则返回<code>true</code>
     */
    public static boolean isNotBlank(String str) {
        int length;

        if ((str == null) || ((length = str.length()) == 0)) {
            return false;
        }

        for (int i = 0; i < length; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return true;
            }
        }

        return false;
    }

    /**
     * 检查字符串是否为<code>null</code>或空字符串<code>""</code>。
     * 
     * <pre>
     * StringUtil.isEmpty(null) = true
     * StringUtil.isEmpty("") = true
     * StringUtil.isEmpty(" ") = false
     * StringUtil.isEmpty("bob") = false
     * StringUtil.isEmpty("  bob  ") = false
     * </pre>
     * 
     * @param str
     *            要检查的字符串
     * 
     * @return 如果为空, 则返回<code>true</code>
     */
    public static boolean isEmpty(String str) {
        return ((str == null) || (str.length() == 0));
    }

    /**
     * 检查字符串是否不是<code>null</code>和空字符串<code>""</code>。
     * 
     * <pre>
     * StringUtil.isNotEmpty(null) = false
     * StringUtil.isNotEmpty("") = false
     * StringUtil.isNotEmpty(" ") = true
     * StringUtil.isNotEmpty("bob") = true
     * StringUtil.isNotEmpty("  bob  ") = true
     * </pre>
     * 
     * @param str
     *            要检查的字符串
     * 
     * @return 如果不为空, 则返回<code>true</code>
     */
    public static boolean isNotEmpty(String str) {
        return ((str != null) && (str.length() > 0));
    }

    /**
     * 在字符串中查找指定字符串，并返回第一个匹配的索引值。如果字符串为<code>null</code>或未找到，则返回<code>-1</code>。
     * 
     * <pre>
     * StringUtil.indexOf(null, *) = -1
     * StringUtil.indexOf(*, null) = -1
     * StringUtil.indexOf("", "") = 0
     * StringUtil.indexOf("aabaabaa", "a") = 0
     * StringUtil.indexOf("aabaabaa", "b") = 2
     * StringUtil.indexOf("aabaabaa", "ab") = 1
     * StringUtil.indexOf("aabaabaa", "") = 0
     * </pre>
     * 
     * @param str
     *            要扫描的字符串
     * @param searchStr
     *            要查找的字符串
     * 
     * @return 第一个匹配的索引值。如果字符串为<code>null</code>或未找到，则返回<code>-1</code>
     */
    public static int indexOf(String str, String searchStr) {
        if ((str == null) || (searchStr == null)) {
            return -1;
        }

        return str.indexOf(searchStr);
    }

    /**
     * 在字符串中查找指定字符串，并返回第一个匹配的索引值。如果字符串为<code>null</code>或未找到，则返回<code>-1</code>。
     * 
     * <pre>
     * StringUtil.indexOf(null, *, *) = -1
     * StringUtil.indexOf(*, null, *) = -1
     * StringUtil.indexOf("", "", 0) = 0
     * StringUtil.indexOf("aabaabaa", "a", 0) = 0
     * StringUtil.indexOf("aabaabaa", "b", 0) = 2
     * StringUtil.indexOf("aabaabaa", "ab", 0) = 1
     * StringUtil.indexOf("aabaabaa", "b", 3) = 5
     * StringUtil.indexOf("aabaabaa", "b", 9) = -1
     * StringUtil.indexOf("aabaabaa", "b", -1) = 2
     * StringUtil.indexOf("aabaabaa", "", 2) = 2
     * StringUtil.indexOf("abc", "", 9) = 3
     * </pre>
     * 
     * @param str
     *            要扫描的字符串
     * @param searchStr
     *            要查找的字符串
     * @param startPos
     *            开始搜索的索引值，如果小于0，则看作0
     * 
     * @return 第一个匹配的索引值。如果字符串为<code>null</code>或未找到，则返回<code>-1</code>
     */
    public static int indexOf(String str, String searchStr, int startPos) {
        if ((str == null) || (searchStr == null)) {
            return -1;
        }

        // JDK1.3及以下版本的bug：不能正确处理下面的情况
        if ((searchStr.length() == 0) && (startPos >= str.length())) {
            return str.length();
        }

        return str.indexOf(searchStr, startPos);
    }

    /**
     * 取指定字符串的子串。
     * 
     * <p>
     * 负的索引代表从尾部开始计算。如果字符串为<code>null</code>，则返回<code>null</code>。
     * 
     * <pre>
     * StringUtil.substring(null, *, *) = null
     * StringUtil.substring("", * , *) = "";
     * StringUtil.substring("abc", 0, 2) = "ab"
     * StringUtil.substring("abc", 2, 0) = ""
     * StringUtil.substring("abc", 2, 4) = "c"
     * StringUtil.substring("abc", 4, 6) = ""
     * StringUtil.substring("abc", 2, 2) = ""
     * StringUtil.substring("abc", -2, -1) = "b"
     * StringUtil.substring("abc", -4, 2) = "ab"
     * </pre>
     * 
     * </p>
     * 
     * @param str
     *            字符串
     * @param start
     *            起始索引，如果为负数，表示从尾部计算
     * @param end
     *            结束索引（不含），如果为负数，表示从尾部计算
     * 
     * @return 子串，如果原始串为<code>null</code>，则返回<code>null</code>
     */
    public static String substring(String str, int start, int end) {
        if (str == null) {
            return null;
        }

        if (end < 0) {
            end = str.length() + end;
        }

        if (start < 0) {
            start = str.length() + start;
        }

        if (end > str.length()) {
            end = str.length();
        }

        if (start > end) {
            return EMPTY_STRING;
        }

        if (start < 0) {
            start = 0;
        }

        if (end < 0) {
            end = 0;
        }

        return str.substring(start, end);
    }

    /**
     * 检查字符串中是否包含指定的字符串。如果字符串为<code>null</code>，将返回<code>false</code>。
     * 
     * <pre>
     * StringUtil.contains(null, *) = false
     * StringUtil.contains(*, null) = false
     * StringUtil.contains("", "") = true
     * StringUtil.contains("abc", "") = true
     * StringUtil.contains("abc", "a") = true
     * StringUtil.contains("abc", "z") = false
     * </pre>
     * 
     * @param str
     *            要扫描的字符串
     * @param searchStr
     *            要查找的字符串
     * 
     * @return 如果找到，则返回<code>true</code>
     */
    public static boolean contains(String str, String searchStr) {
        if ((str == null) || (searchStr == null)) {
            return false;
        }

        return str.indexOf(searchStr) >= 0;
    }

    /**
     * <p>
     * Checks if the String contains only unicode digits. A decimal point is not
     * a unicode digit and returns false.
     * </p>
     * 
     * <p>
     * <code>null</code> will return <code>false</code>. An empty String ("")
     * will return <code>true</code>.
     * </p>
     * 
     * <pre>
     * StringUtils.isNumeric(null) = false
     * StringUtils.isNumeric("") = true
     * StringUtils.isNumeric("  ") = false
     * StringUtils.isNumeric("123") = true
     * StringUtils.isNumeric("12 3") = false
     * StringUtils.isNumeric("ab2c") = false
     * StringUtils.isNumeric("12-3") = false
     * StringUtils.isNumeric("12.3") = false
     * </pre>
     * 
     * @param str
     *            the String to check, may be null
     * @return <code>true</code> if only contains digits, and is non-null
     */
    public static boolean isNumeric(String str) {
        if (str == null) {
            return false;
        }
        int sz = str.length();
        for (int i = 0; i < sz; i++) {
            if (Character.isDigit(str.charAt(i)) == false) {
                return false;
            }
        }
        return true;
    }

    /**
     * 将url参数形式的字符串转换为map,
     * <br /> 如果某个key重复出现,则只留最后一次出现的值
     * <br /> 若某个value URLDecode失败,则返回null
     * 
     * @param queryString
     * @param enc
     *            key-value中的value的URLDecode编码
     * @param keepEmptyValue
     *            如果某个'key-value对'没有=号,是否包括此对,但将value设置为null
     * @return
     */
    public static Map<String, String> urlToSingleMap(String queryString, String enc, boolean keepEmptyValue) {
        Map<String, String> pMap = new HashMap<String, String>();
        if (isBlank(queryString)) {
            return null;
        }
        String[] secs = queryString.split("&");
        for (String sec : secs) {
            if (sec.indexOf("=") < 0) {
                if (keepEmptyValue) {
                    pMap.put(sec, null);
                }
            } else {
                String[] kv = sec.split("=", 2);
                String value = kv[1];
                try {
                    value = URLDecoder.decode(value, enc);
                } catch (UnsupportedEncodingException e) {
                    return null;
                }
                pMap.put(kv[0], value);
            }
        }

        if (pMap.isEmpty()) {
            return null;
        } else {
            return pMap;
        }
    }

    /**
     * 将url参数形式的字符串转换为map
     * 
     * @param queryString
     * @param enc
     * @return
     */
    public static Map<String, String[]> urlToMap(String queryString, String enc) {
        Map<String, String[]> paramsMap = new HashMap<String, String[]>();
        if (queryString != null && queryString.length() > 0) {
            int ampersandIndex, lastAmpersandIndex = 0;
            String subStr, param, value;
            String[] paramPair, values, newValues;
            do {
                ampersandIndex = queryString.indexOf('&', lastAmpersandIndex) + 1;
                if (ampersandIndex > 0) {
                    subStr = queryString.substring(lastAmpersandIndex, ampersandIndex - 1);
                    lastAmpersandIndex = ampersandIndex;
                } else {
                    subStr = queryString.substring(lastAmpersandIndex);
                }
                paramPair = subStr.split("=");
                param = paramPair[0];
                value = paramPair.length == 1 ? "" : paramPair[1];
                try {
                    value = URLDecoder.decode(value, enc);
                } catch (UnsupportedEncodingException ignored) {
                }
                if (paramsMap.containsKey(param)) {
                    values = paramsMap.get(param);
                    int len = values.length;
                    newValues = new String[len + 1];
                    System.arraycopy(values, 0, newValues, 0, len);
                    newValues[len] = value;
                } else {
                    newValues = new String[] { value };
                }
                paramsMap.put(param, newValues);
            } while (ampersandIndex > 0);
        }
        if (paramsMap.isEmpty()) {
            return null;
        } else {
            return paramsMap;
        }
    }

    /**
     * 将普通字符串转为16进制大写字符串
     * 
     * @param str
     * @return
     */
    public static String str2HexStr(String str) {
        byte[] bytes = str.getBytes();
        int bLen = bytes.length;
        StringBuffer buf = new StringBuffer(bLen * 2);
        int i;
        for (i = 0; i < bLen; i++) {
            if (((int) bytes[i] & 0xff) < 0x10) {
                buf.append("0");
            }
            buf.append(Long.toString((int) bytes[i] & 0xff, 16));
        }
        return buf.toString().toUpperCase();
    }

    /**
     * 将16进制字符串其对应的普通字符串
     * 
     * @param str
     * @return
     */
    public static String hexStr2Str(String str) {
        int bLen = str.length() / 2;
        byte[] bytes = new byte[bLen];
        for (int i = 0; i < bLen; i++) {
            int index = i * 2;
            int v = Integer.parseInt(str.substring(index, index + 2), 16);
            bytes[i] = (byte) v;
        }
        return new String(bytes);
    }

    /**
     * 从url中提取domain部分,如果没有`/`符号,则返回传入内容
     * <pre>
     * StringUtil.getDomain("http://www.uc.cn/abc")="www.uc.cn"
     * StringUtil.getDomain("www.uc.cn:8080/abc")="www.uc.cn:8080"
     * StringUtil.getDomain("abc")="abc"
     * </pre>
     * 
     * @param url
     * @return
     */
    public static String getDomain(String url) {
        String[] ary = url.split("/");
        if (ary.length > 3 && (ary[0].equalsIgnoreCase("http:") || ary[0].equalsIgnoreCase("https:")) && ary[1].equals("")) {
            return ary[2];
        } else if (ary.length > 0) {
            return ary[1];
        } else {
            return url;
        }
    }

    /**
     * 从url中提取domain部分,如果没有`/`符号,则返回传入内容,并去除端口号
     * <pre>
     * StringUtil.getDomainWithoutPort("http://www.uc.cn:8080/abc")="www.uc.cn"
     * </pre>
     * 
     * @param url
     * @return
     */
    public static String getDomainWithoutPort(String url) {
        if (url == null)
            return null;
        String domain = getDomain(url);
        String[] arrs = domain.split(":");
        if (arrs.length == 2)
            return arrs[0];
        else
            return domain;
    }

    /**
     * 从末尾倒数截取str中指定长度的内容<br />
     * 若str 为 null,返回null<br />
     * 若str.length<=len,返回str<br />
     * 若len<0 返回str
     * 
     * @param str
     *            被截取的字符
     * @param len
     *            截取长度
     * @return
     */
    public static String rightTrunc(String str, int len) {
        if (str == null) {
            return null;
        }
        int strLen = str.length();
        if (strLen <= len || len < 0) {
            return str;
        }

        return str.substring(strLen - len);

    }


    public static String map2String(Map<String, String> map, String[] notIn) {
        StringBuffer sbuf = new StringBuffer("");
        List<String> list = Arrays.asList(notIn);
        String and = "";
        for (String key : map.keySet()) {
            if (!list.contains(key)) {
                sbuf.append(and);
                sbuf.append(key).append("=").append(map.get(key));
                and = "&";
            }
        }
        return sbuf.toString();
    }

    /**
     * 统计c在str出现的次数
     * <br /> src为null 返回0
     * <br /> c为null 返回0
     * 
     * @param src
     *            字符串
     * @param c的单个字符
     * @return 出现的次数
     */
    public static int countRepeat(String src, char c) {
        if (src == null) {
            return 0;
        }
        int count = 0;
        for (int i = 0; i < src.length(); i++) {
            Character cr = src.charAt(i);
            if (c == cr) {
                count++;
            }
        }
        return count;
    }

    /**
     * 取得指定子串在字符串中出现的次数。
     * <p/>
     * <p>
     * 如果字符串为<code>null</code>或空，则返回<code>0</code>。
     * <pre>
     * StringUtil.countRepeat(null, *) = 0
     * StringUtil.countRepeat("", *) = 0
     * StringUtil.countRepeat("abba", null) = 0
     * StringUtil.countRepeat("abba", "") = 0
     * StringUtil.countRepeat("abba", "a") = 2
     * StringUtil.countRepeat("abba", "ab") = 1
     * StringUtil.countRepeat("abba", "xxx") = 0
     * StringUtil.countRepeat("aaaa", "aa") = 3
     * </pre>
     * </p>
     * 
     * @param str
     *            要扫描的字符串
     * @param subStr
     *            子字符串
     * @return 子串在字符串中出现的次数，如果字符串为<code>null</code>或空，则返回<code>0</code>
     */
    public static int countRepeat(String str, String subStr) {
        if ((str == null) || (str.length() == 0) || (subStr == null) || (subStr.length() == 0)) {
            return 0;
        }

        int count = 0;
        int index = 0;

        while ((index = str.indexOf(subStr, index)) != -1) {
            count++;
            index++;
        }

        return count;
    }


    public static String getCurrentDateTimeStringDefault() {
        Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
        String str = (String) android.text.format.DateFormat.format("yyyy年MM月dd日   hh:mm:ss", curDate);
        return str;
    }

    public static String formatFloatAmount(float amount) {
        DecimalFormat df = new DecimalFormat("#0.##");
        return df.format(amount);
    }

    public static Date parseStringToDate(String dateString, String dateFormat) {
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        Date dt = null;
        try {
            dt = formatter.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dt;
    }

    public static String join(ArrayList<Long> list, String separator) {
        String retStr = "";
        for (int i = 0, len = list.size(); i < len; i++) {
            if (i == 0) {
                retStr += list.get(i).toString();
            } else {
                retStr += separator + list.get(i).toString();
            }
        }
        return retStr;
    }

    /**
     * 将对象转化成json字符串
     * 
     * @param src
     * @return
     */
    public static String toJsonStr(Object src) {
        return "";
    }


    /**
     * 判断字符串是否为空或空白串
     * 
     * @author wangcj@ucweb.com
     *         <br> Create: 2012-07-19
     * 
     * @param s
     *            待判断字符串
     * @return true - 是null、空串或空白串； false - 是具有实际内容的字符串
     */
    public static boolean isNullOrEmpty(String s) {
        return (s == null || s.length() == 0 || s.trim().length() == 0);
    }



    /**
     * 判断输入的邮箱字符串是否合法
     * 
     * @author chenbl@ucweb.com
     *         <br> Create: 2012-12-03
     * 
     * @param emailString
     *            带判断的字符串
     * @return
     *         true 输入的字符串是合法邮箱地址；false 输入的字符串不是合法邮箱地址
     */
    public static boolean isEmailString(String emailString) {
        Pattern pattern = Pattern.compile("^([\\w]+([\\w-\\.+]*[\\w-]+)?)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([\\w-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$");
        Matcher matcher = pattern.matcher(emailString);
        return matcher.matches();
    }
}
