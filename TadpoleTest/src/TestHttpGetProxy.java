import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import network.HttpGetProxy;

public class TestHttpGetProxy {

    private static String videoUrl = "http://d1.2mp4.net/%E6%89%8B%E6%9C%BA%E7%94%B5%E5%BD%B1/201212/%E5%BF%97%E6%98%8E%E4%B8%8E%E6%98%A5%E5%A8%87-2mp4-800x448.mp4";

    public static void main(String[] args) {
        //        HttpGetProxy proxy = new HttpGetProxy(9999);
        //        proxy.start();
        //        String testUrl = "";
        //        InputStream is = null;
        //        URL url;
        //        try {
        //            url = new URL(proxy.getProxyUrl(videoUrl));
        //            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        //            conn.setFollowRedirects(true);
        //            is = conn.getInputStream();
        //            int byte_read = 0;
        //            byte[] buffer = new byte[1024 * 1024];
        //            while ((byte_read = is.read(buffer)) != -1) {
        //                Thread.sleep(3000);
        //            }
        //        } catch (MalformedURLException e) {
        //            // TODO Auto-generated catch block
        //            e.printStackTrace();
        //        } catch (IOException e) {
        //            // TODO Auto-generated catch block
        //            e.printStackTrace();
        //        } catch (InterruptedException e) {
        //            // TODO Auto-generated catch block
        //            e.printStackTrace();
        //        }
        //
        //        URL u;
        //        try {
        //            u = new URL("http://5.66825.com/download/ring/000/080/123b748ba28cbbc3ed9834d1cf3a038d.mp3");
        //            HttpURLConnection conn = (HttpURLConnection) u.openConnection();
        //            conn.setFollowRedirects(true);
        //            conn.connect();
        //            System.out.println("content-length = " + conn.getContentLength());
        //            byte[] buffer = new byte[5120];
        //            InputStream in = conn.getInputStream();
        //            int total = 0;
        //            int tmpByteRead = 0;
        //            while ((tmpByteRead = in.read(buffer)) != -1) {
        //                total += tmpByteRead;
        //            }
        //
        //            System.out.println("total = " + total);
        //
        //            conn.getContentLength();
        //        } catch (MalformedURLException e) {
        //            // TODO Auto-generated catch block
        //            e.printStackTrace();
        //        } catch (IOException e) {
        //            // TODO Auto-generated catch block
        //            e.printStackTrace();
        //        }
        //


        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        System.out.println(Integer.MAX_VALUE);
        try {
            // MAX_VALUE 小于 4GB
            baos.write(new byte[Integer.MAX_VALUE]);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


    }
}
