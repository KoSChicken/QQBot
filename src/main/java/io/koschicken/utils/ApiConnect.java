package io.koschicken.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ApiConnect {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiConnect.class);

    private ApiConnect() {
    }

    /**
     * 向url的api发送访问请求，参数为第二个map变量，为get请求
     *
     * @param requestUrl 请求地址
     * @param params     请求参数
     */
    @SuppressWarnings("unused")
    private static String httpRequest(String requestUrl, Map<String, Object> params) {
        //buffer用于接受返回的字符
        StringBuilder buffer = new StringBuilder();
        try {
            //建立URL，把请求地址给补全，其中urlEncode（）方法用于把params里的参数给取出来
            URL url = new URL(requestUrl + urlEncode(params));
            //打开http连接
            HttpURLConnection httpUrlConn = (HttpURLConnection) url.openConnection();
            httpUrlConn.setDoInput(true);
            httpUrlConn.setRequestMethod("GET");
            httpUrlConn.connect();
            //获得输入
            InputStream inputStream = httpUrlConn.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            //将bufferReader的值给放到buffer里
            String str;
            while ((str = bufferedReader.readLine()) != null) {
                buffer.append(str);
            }
            //关闭bufferReader和输入流
            bufferedReader.close();
            inputStreamReader.close();
            inputStream.close();
            //断开连接
            httpUrlConn.disconnect();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            LOGGER.debug(Arrays.toString(e.getStackTrace()));
        }
        //返回字符串
        return buffer.toString();
    }

    /**
     * 上个函数的无参版本
     *
     * @param requestUrl 请求地址
     */
    public static String httpRequest(String requestUrl) {
        //buffer用于接受返回的字符
        StringBuilder buffer = new StringBuilder();
        try {
            //建立URL，把请求地址给补全，其中urlEncode（）方法用于把params里的参数给取出来
            URL url = new URL(requestUrl);
            //打开http连接
            HttpURLConnection httpUrlConn = (HttpURLConnection) url.openConnection();
            httpUrlConn.setDoInput(true);
            httpUrlConn.setRequestMethod("GET");
            httpUrlConn.connect();
            //获得输入
            InputStream inputStream = httpUrlConn.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            //将bufferReader的值给放到buffer里
            String str;
            while ((str = bufferedReader.readLine()) != null) {
                buffer.append(str);
            }
            //关闭bufferReader和输入流
            bufferedReader.close();
            inputStreamReader.close();
            inputStream.close();
            //断开连接
            httpUrlConn.disconnect();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        //返回字符串
        return buffer.toString();
    }

    public static String urlEncode(Map<String, Object> data) {
        //将map里的参数变成像 showapi_appid=###&showapi_sign=###&的样子
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Object> i : data.entrySet()) {
            try {
                sb.append(i.getKey()).append("=").append(URLEncoder.encode(i.getValue() + "", "UTF-8")).append("&");
            } catch (UnsupportedEncodingException e) {
                LOGGER.error(e.getMessage());
                LOGGER.debug(Arrays.toString(e.getStackTrace()));
            }
        }
        return sb.toString();
    }

    /**
     * 获取本机ip
     */
    public static String getLocalIp4AddressFromNetworkInterface() {
        String ip = "";
        String chinaz = "http://ip.chinaz.com";

        StringBuilder inputLine = new StringBuilder();
        String read;
        URL url;
        HttpURLConnection urlConnection;
        try {
            url = new URL(chinaz);
            urlConnection = (HttpURLConnection) url.openConnection();
            try (BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), StandardCharsets.UTF_8))) {
                while ((read = in.readLine()) != null) {
                    inputLine.append(read).append("\r\n");
                }
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
            LOGGER.debug(Arrays.toString(e.getStackTrace()));
        }

        Pattern p = Pattern.compile("<dd class=\"fz24\">(.*?)</dd>");
        Matcher m = p.matcher(inputLine.toString());
        if (m.find()) {
            ip = m.group(1);
        }
        return ip;
    }
}
