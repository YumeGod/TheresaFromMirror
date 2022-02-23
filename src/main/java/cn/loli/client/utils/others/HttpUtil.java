package cn.loli.client.utils.others;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.Validate;
import sun.misc.Unsafe;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import java.util.Set;

public class HttpUtil {

    private static HttpURLConnection createUrlConnection(URL url) throws IOException {
        Validate.notNull(url);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setConnectTimeout(15000);
        connection.setReadTimeout(15000);
        connection.setUseCaches(false);
        return connection;
    }

    public static String performGetRequest(URL url) throws IOException {
        Validate.notNull(url);

        HttpURLConnection connection = createUrlConnection(url);
        InputStream inputStream = null;
        connection.setRequestProperty("User-agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:6.0) Gecko/20100101 Firefox/6.0");

        String var6;
        try {
            String result;
            try {
                inputStream = connection.getInputStream();
                return IOUtils.toString(inputStream, Charsets.UTF_8);
            } catch (IOException var10) {
                IOUtils.closeQuietly(inputStream);
                inputStream = connection.getErrorStream();
                if (inputStream == null) {
                    throw var10;
                }
            }

            result = IOUtils.toString(inputStream, Charsets.UTF_8);
            var6 = result;
        } finally {
            IOUtils.closeQuietly(inputStream);
        }

        return var6;
    }

    public static String performPostRequest(String Surl, String data) throws IOException {
        URL url = new URL(Surl);
        URLConnection uc = url.openConnection();
        uc.setDoOutput(true);// POST可能にする

        OutputStream os = uc.getOutputStream();// POST用のOutputStreamを取得

        PrintStream ps = new PrintStream(os);
        ps.print(data);// データをPOSTする
        ps.close();

        InputStream is = uc.getInputStream();// POSTした結果を取得
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String s;
        StringBuilder result = new StringBuilder();
        while ((s = reader.readLine()) != null) {
            result.append(s);
        }
        reader.close();

        return result.toString();
    }


}
