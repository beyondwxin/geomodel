package com.king.geomodel.utils.uploadutils;


import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Http上传
 */
public class HttpRequester {

    /**
     * 通过Android 客户端上传数据到服务器：可以上传简单的表单，也可以方便的 传带有附件的文件，此类远远比Android
     * 自身的HttpClient 更高效、更易于使用 直接通过HTTP 协议提交数据到服务器,实现如下面表单提交功能: <FORM
     * METHOD=POST ACTION="http://192.168.0.200:8080/ssi/fileload/test.do"
     * enctype="multipart/form-data"> <INPUT TYPE="text" NAME="name"> <INPUT
     * TYPE="text" NAME="id"> <input type="file" name="imagefile"/> <input
     * type="file" name="zip"/> </FORM>
     *
     * @param actionUrl 上传路径(注：避免使用localhost 或127.0.0.1这样的路径 测试， 因为它会指向手机模拟器，
     *                  你可以使用http://www.itcast.cn 或 http://192.168.1.10:8080这样的路径测试)
     * @param params    请求参数key 为参数名,value 为参数值
     * @param files     上传的文件集合
     */
    public static String post(String actionUrl, Map<String, String> params, ArrayList<FormFile> files) {
        try {
            String BOUNDARY = "---------7d4a6d158c9"; // 数据分隔线
            String MULTIPART_FORM_DATA = "multipart/form-data";
            URL url = new URL(actionUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5 * 1000);
            conn.setDoInput(true);// 允许输入
            conn.setDoOutput(true);// 允许输出
            conn.setUseCaches(false);// 不使用Cache

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Charset", "UTF-8");
            conn.setRequestProperty("Content-Type", MULTIPART_FORM_DATA + "; boundary=" + BOUNDARY);

            StringBuilder sb = new StringBuilder();
            for (Map.Entry<String, String> entry : params.entrySet()) {// 构建表单字段内容
                sb.append("--");
                sb.append(BOUNDARY);
                sb.append("\r\n");
                sb.append("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"\r\n\r\n");
                sb.append(entry.getValue());
                sb.append("\r\n");
            }

            DataOutputStream outStream = new DataOutputStream(conn.getOutputStream());

            outStream.write(sb.toString().getBytes());// 发送表单字段数据
            for (FormFile file : files) {// 发送文件数据
                StringBuilder split = new StringBuilder();
                split.append("--");
                split.append(BOUNDARY);
                split.append("\r\n");
                split.append("Content-Disposition: form-data;name=\"" + "file" + "\";filename=\""
                        + file.getFilname() + "\"\r\n");
                split.append("Content-Type: " + file.getContentType() + "\r\n\r\n");
                outStream.write(split.toString().getBytes());
                if (file.getInStream() != null) {
                    byte[] buffer = new byte[1024];

                    int len = 0;
                    while ((len = file.getInStream().read(buffer)) != -1) {
                        outStream.write(buffer, 0, len);
                    }
                    file.getInStream().close();
                } else {
                    outStream.write(file.getData(), 0, file.getData().length);
                }
                outStream.write("\r\n".getBytes());
            }

            byte[] end_data = ("--" + BOUNDARY + "--\r\n").getBytes();// 数据结束标志
            outStream.write(end_data);
            outStream.flush();
            int cah = conn.getResponseCode();

            if (cah != 200) {

                throw new RuntimeException("请求url 失败");
            }
            InputStream is = conn.getInputStream();

            int ch;
            StringBuilder b = new StringBuilder();
            while ((ch = is.read()) != -1) {
                b.append((char) ch);
            }

            outStream.close();
            conn.disconnect();

            return b.toString(); // 返回POST后的数据
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 提交数据到服务器
     *
     * @param actionUrl 上传路径(注：避免使用localhost 或127.0.0.1这样的路径 测试， 因为它会指向手机模拟器，
     *                  你可以使用http://www.itcast.cn 或 http://192.168.1.10:8080这样的路径测试)
     * @param params    请求参数key 为参数名,value 为参数值
     * @param file      上传文件
     */
    public static String post(String actionUrl, Map<String, String> params, FormFile file) {
//		return post(actionUrl, params, new FormFile[] { file });
        ArrayList<FormFile> files = new ArrayList<FormFile>();
        files.add(file);
        return post(actionUrl, params, files);
    }

    /**
     * 提交返回
     *
     * @param path
     * @param params
     * @param encode
     * @return
     * @throws Exception
     */
    public static String postFromHttpClient(String path, Map<String, String> params, String encode) throws Exception {
        List<NameValuePair> formparams = new ArrayList<NameValuePair>();// 用于存放请求参数
        for (Map.Entry<String, String> entry : params.entrySet()) {
            formparams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, encode);
        HttpPost httppost = new HttpPost(path);
        httppost.setEntity(entity);
        HttpClient httpclient = new DefaultHttpClient();// 看作是浏览器
        HttpResponse response = httpclient.execute(httppost);// 发送post 请求

        if (response.getStatusLine().getStatusCode() == 200) {
            return EntityUtils.toString(response.getEntity());
        }
        return null;// StreamTool.readInputStream(response.getEntity().getContent());
    }

    /**
     * 发送请求
     *
     * @param path   请求路径
     * @param params 请求参数key 为参数名称value 为参数值
     * @param encode 请求参数的编码
     */
    public static byte[] post(String path, Map<String, String> params, String encode) throws Exception {
        // String params = "method=save&name="+
        // URLEncoder.encode(" 老毕","UTF-8")+ "&age=28&";//需要发送的参数
        StringBuilder parambuilder = new StringBuilder("");
        if (params != null && !params.isEmpty()) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                parambuilder.append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue(), encode))
                        .append("&");
            }
            parambuilder.deleteCharAt(parambuilder.length() - 1);
        }
        byte[] data = parambuilder.toString().getBytes();

        URL url = new URL(path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput(true);// 允许对外发送请求参数
        conn.setUseCaches(false);// 不进行缓存
        conn.setConnectTimeout(5 * 1000);
        conn.setRequestMethod("POST");
        // 下面设置http 请求头
        conn.setRequestProperty(
                "Accept",
                "image/gif, image/jpeg, image/pjpeg, image/pjpeg, application/x-shockwave-flash, application/xaml+xml, application/vnd.ms-xpsdocument, application/x-ms-xbap, application/x-ms-application, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, */*");
        conn.setRequestProperty("Accept-Language", "zh-CN");
        conn.setRequestProperty(
                "User-Agent",
                "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR3.0.04506.30; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("Content-Length", String.valueOf(data.length));
        conn.setRequestProperty("Connection", "Keep-Alive");

        // 发送参数
        DataOutputStream outStream = new DataOutputStream(conn.getOutputStream());
        outStream.write(data);// 把参数发送出去
        outStream.flush();
        outStream.close();
        if (conn.getResponseCode() == 200) {

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), encode));
            String lines;
            while ((lines = reader.readLine()) != null) {
                System.out.println(lines);
            }
            // 如果只有一行的话，最后一行算返回结果
            return StreamTool.readInputStream(conn.getInputStream());
        }
        return null;
    }
}
