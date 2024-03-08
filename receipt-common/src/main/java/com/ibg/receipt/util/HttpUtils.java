package com.ibg.receipt.util;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * http 请求工具类
 * <p>
 * 支持 两种请求方法（get/post）
 * 支持 两种提交方式（表单提交、Ajax提交）
 * 支持 是否携带请求头
 * <p>
 * 表单提交 编码方式为 application/x-www-form-urlencoded
 * Ajax提交 编码方式为 application/json
 *
 * @author 杨彭伟
 * @date 2018-12-06 18:15
 */
public class HttpUtils {

    /**
     * 带参数GET请求
     *
     * @param url   请求地址
     * @param param 请求参数
     * @return java.lang.String
     * @author 杨彭伟
     * @date 2018-9-21 9:03
     */
    public static String doGet(String url, Map<String, String> param) {
        String resultString = "";
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet httpGet = buildHttpGet(url, param);
            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                if (response.getStatusLine().getStatusCode() == 200) {
                    resultString = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultString;
    }

    /**
     * 带参数和请求头的GET请求
     *
     * @param url    请求地址
     * @param param  请求参数
     * @param header 请求头
     * @return java.lang.String
     * @date 2018-9-25 20:09
     */
    public static String doGet(String url, Map<String, String> param, Map<String, String> header) {
        String resultString = "";
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet httpGet = buildHttpGet(url, param);
            if (header != null) {
                for (String key : header.keySet()) {
                    httpGet.setHeader(key, header.get(key));
                }
            }
            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                if (response.getStatusLine().getStatusCode() == 200) {
                    resultString = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultString;
    }

    /**
     * 无参GET请求
     *
     * @param url 请求地址
     * @return java.lang.String
     */
    public static String doGet(String url) {
        return doGet(url, null);
    }

    /**
     * 无参的 POST 表单提交
     *
     * @param url 请求地址
     * @return java.lang.String
     */
    public static String doPost(String url) {
        return doPost(url, null);
    }

    /**
     * 有参数、无请求头 POST 表单提交
     *
     * @param url   请求地址
     * @param param 请求参数
     * @return java.lang.String
     */
    public static String doPost(String url, Map<String, String> param) {
        String resultString = "";
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(url);
            setEntity(httpPost, param);
            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                resultString = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultString;
    }

    /**
     * 有参数、有请求头 POST 表单提交
     *
     * @param url    请求地址
     * @param param  请求参数
     * @param header 请求头
     * @return java.lang.String
     */
    public static String doPost(String url, Map<String, String> param, Map<String, String> header) {
        String resultString = "";
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(url);
            if (header != null) {
                for (String key : header.keySet()) {
                    httpPost.setHeader(key, header.get(key));
                }
            }
            setEntity(httpPost, param);
            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                resultString = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultString;
    }

    /**
     * 有参数、无请求头 POST Ajax提交
     *
     * @param url  请求地址
     * @param json 请求参数
     * @return java.lang.String
     */
    public static String doPostJson(String url, String json) {
        String resultString = "";
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(url);
            StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
            httpPost.setEntity(entity);
            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                resultString = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultString;
    }

    /**
     * 有参数、有请求头 POST Ajax提交
     *
     * @param url    请求地址
     * @param json   请求参数
     * @param header 请求头
     * @return java.lang.String
     */
    public static String doPostJson(String url, String json, Map<String, String> header) {
        String resultString = "";
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(url);
            if (header != null) {
                for (String key : header.keySet()) {
                    httpPost.setHeader(key, header.get(key));
                }
            }
            ContentType contentType = ContentType.create(ContentType.APPLICATION_JSON.getMimeType(), StandardCharsets.UTF_8);
            StringEntity entity = new StringEntity(json, contentType);
            httpPost.setEntity(entity);
            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                resultString = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultString;
    }

    /**
     * 构建带有参数的 GET请求对象
     *
     * @param url   请求地址
     * @param param 请求参数
     * @return GET请求对象
     * @throws URISyntaxException
     */
    private static HttpGet buildHttpGet(String url, Map<String, String> param) throws URISyntaxException {
        URIBuilder builder = new URIBuilder(url);
        if (param != null) {
            for (String key : param.keySet()) {
                builder.addParameter(key, param.get(key));
            }
        }
        URI uri = builder.build();
        return new HttpGet(uri);
    }

    /**
     * 为post请求 设置请求参数
     *
     * @param httpPost POST请求对象
     * @param param    请求参数
     * @throws UnsupportedEncodingException
     */
    private static void setEntity(HttpPost httpPost, Map<String, String> param) throws UnsupportedEncodingException {
        if (param != null) {
            List<NameValuePair> paramList = new ArrayList<>();
            for (String key : param.keySet()) {
                paramList.add(new BasicNameValuePair(key, param.get(key)));
            }
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(paramList, StandardCharsets.UTF_8);
            httpPost.setEntity(entity);
        }
    }
}