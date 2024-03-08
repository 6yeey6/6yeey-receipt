package com.ibg.receipt.util;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Handler;

import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;

import com.ibg.commons.wx.work.notice.starter.service.WxWorkNoticeService;
import com.ibg.receipt.context.ContextContainer;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HttpUtil {
    public static final int SOCKET_TIME_OUT = 1000 * 120; // 读取超时时间两分钟
    public static final int CONNECT_TIME_OUT = 1000 * 30; // 连接超时时间30秒

    public static HttpUtil create() {
        return new HttpUtil();
    }

    /**
     * 发送 get请求
     */
    public String get(String url) {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            // 创建httpget.
            HttpGet httpGet = new HttpGet(url);
            httpGet.setHeader("YX_SOURCE", "IBG_ACCOUNT");
            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(SOCKET_TIME_OUT)
                    .setConnectTimeout(CONNECT_TIME_OUT).build();// 设置请求和传输超时时间
            httpGet.setConfig(requestConfig);
            // 执行get请求.
            CloseableHttpResponse response = httpclient.execute(httpGet);
            wxWorkHttpResultMonitor(response, url, "");
            try {
                // 获取响应实体
                HttpEntity entity = response.getEntity();
                // 打印响应状态
                if (entity != null) {
                    return EntityUtils.toString(entity);
                }
            } finally {
                response.close();
            }
        } catch (ClientProtocolException e) {
            log.error(String.format("请求url：%s 时异常", url), e);
        } catch (ParseException e) {
            log.error(String.format("请求url：%s 时异常", url), e);
        } catch (IOException e) {
            log.error(String.format("请求url：%s 时异常", url), e);
        } catch (Exception e) {
            log.error(String.format("请求url：%s 时异常", url), e);
        } finally {
            // 关闭连接,释放资源
            try {
                httpclient.close();
            } catch (IOException e) {
                log.error(String.format("请求url：%s 关闭连接时异常", url), e);
            }
        }
        return null;
    }

    public String get(String url, Map<String, String> headersMap) {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            // 创建httpget.
            HttpGet httpGet = new HttpGet(url);
            httpGet.setHeader("YX_SOURCE", "IBG_ACCOUNT");
            for (Entry<String, String> entry : headersMap.entrySet()) {
                httpGet.setHeader(entry.getKey(), entry.getValue());
            }
            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(SOCKET_TIME_OUT)
                    .setConnectTimeout(CONNECT_TIME_OUT).build();// 设置请求和传输超时时间
            httpGet.setConfig(requestConfig);
            // 执行get请求.
            CloseableHttpResponse response = httpclient.execute(httpGet);
            // 增加返回值校验
            wxWorkHttpResultMonitor(response, url, "");
            try {
                // 获取响应实体
                HttpEntity entity = response.getEntity();
                // 打印响应状态
                if (entity != null) {
                    return EntityUtils.toString(entity);
                }
            } finally {
                response.close();
            }
        } catch (ClientProtocolException e) {
            log.error(String.format("请求url：%s 时异常", url), e);
        } catch (ParseException e) {
            log.error(String.format("请求url：%s 时异常", url), e);
        } catch (IOException e) {
            log.error(String.format("请求url：%s 时异常", url), e);
        } catch (Exception e) {
            log.error(String.format("请求url：%s 时异常", url), e);
        } finally {
            // 关闭连接,释放资源
            try {
                httpclient.close();
            } catch (IOException e) {
                log.error(String.format("请求url：%s 关闭连接时异常", url), e);
            }
        }
        return null;
    }

    public byte[] getBytes(String url, Map<String, String> headersMap) {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            // 创建httpget.
            HttpGet httpGet = new HttpGet(url);
            httpGet.setHeader("YX_SOURCE", "IBG_ACCOUNT");
            if (headersMap != null) {
                for (Entry<String, String> entry : headersMap.entrySet()) {
                    httpGet.setHeader(entry.getKey(), entry.getValue());
                }
            }
            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(SOCKET_TIME_OUT)
                    .setConnectTimeout(CONNECT_TIME_OUT).build();// 设置请求和传输超时时间
            httpGet.setConfig(requestConfig);
            // 执行get请求.
            CloseableHttpResponse response = httpclient.execute(httpGet);
            try {
                // 获取响应实体
                HttpEntity entity = response.getEntity();
                // 打印响应状态
                if (entity != null) {
                    return EntityUtils.toByteArray(entity);
                }
            } finally {
                response.close();
            }
        } catch (ClientProtocolException e) {
            log.error(String.format("请求url：%s 时异常", url), e);
        } catch (ParseException e) {
            log.error(String.format("请求url：%s 时异常", url), e);
        } catch (IOException e) {
            log.error(String.format("请求url：%s 时异常", url), e);
        } catch (Exception e) {
            log.error(String.format("请求url：%s 时异常", url), e);
        } finally {
            // 关闭连接,释放资源
            try {
                httpclient.close();
            } catch (IOException e) {
                log.error(String.format("请求url：%s 关闭连接时异常", url), e);
            }
        }
        return null;
    }

    public byte[] getBytes(String url, Map<String, String> headersMap, boolean isHttps) {
        if(!isHttps) {
            return getBytes(url, headersMap);
        }
        CloseableHttpClient httpclient = null;
        try {
            httpclient = HttpClients.custom().setSSLHostnameVerifier(new NoopHostnameVerifier())
                .setSSLContext(
                    new SSLContextBuilder().loadTrustMaterial(null, (chain, authType) -> true).build())
                .build();
            // 创建httpget.
            HttpGet httpGet = new HttpGet(url);
            httpGet.setHeader("YX_SOURCE", "IBG_ACCOUNT");
            if (headersMap != null) {
                for (Entry<String, String> entry : headersMap.entrySet()) {
                    httpGet.setHeader(entry.getKey(), entry.getValue());
                }
            }
            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(SOCKET_TIME_OUT)
                .setConnectTimeout(CONNECT_TIME_OUT).build();// 设置请求和传输超时时间
            httpGet.setConfig(requestConfig);
            // 执行get请求.
            CloseableHttpResponse response = httpclient.execute(httpGet);
            try {
                // 获取响应实体
                HttpEntity entity = response.getEntity();
                // 打印响应状态
                if (entity != null) {
                    return EntityUtils.toByteArray(entity);
                }
            } finally {
                response.close();
            }
        } catch (ConnectTimeoutException e) {
            log.warn(String.format("请求url：%s 时异常", url), e);
        } catch (ClientProtocolException e) {
            log.error(String.format("请求url：%s 时异常", url), e);
        } catch (ParseException e) {
            log.error(String.format("请求url：%s 时异常", url), e);
        } catch (IOException e) {
            log.error(String.format("请求url：%s 时异常", url), e);
        } catch (Exception e) {
            log.error(String.format("请求url：%s 时异常", url), e);
        } finally {
            // 关闭连接,释放资源
            try {
                httpclient.close();
            } catch (IOException e) {
                log.error(String.format("请求url：%s 关闭连接时异常", url), e);
            }
        }
        return null;
    }


    public String post(String url, Map<String, String> params) {
        List<BasicNameValuePair> formparams = new ArrayList<BasicNameValuePair>();
        for (Entry<String, String> entry : params.entrySet()) {
            BasicNameValuePair pair = new BasicNameValuePair(entry.getKey(), entry.getValue());
            formparams.add(pair);
        }
        return post(url, formparams);
    }

    /**
     * 发送 get请求
     */
    public String post(String url, List<BasicNameValuePair> formparams) {
        CloseableHttpResponse response = null;
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(url);
            httpPost.setHeader("YX_SOURCE", "IBG_ACCOUNT");
            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(SOCKET_TIME_OUT)
                    .setConnectTimeout(CONNECT_TIME_OUT).build();// 设置请求和传输超时时间
            httpPost.setConfig(requestConfig);
            httpPost.setEntity(new UrlEncodedFormEntity(formparams, "UTF-8"));
            response = httpclient.execute(httpPost);
            HttpEntity entity = response.getEntity();

            wxWorkHttpResultMonitor(response, url, formparams);

            String content = EntityUtils.toString(entity);
            EntityUtils.consume(entity);
            return content;
        } catch (ClientProtocolException e) {
            log.error(String.format("请求url：%s 时异常", url), e);
        } catch (ParseException e) {
            log.error(String.format("请求url：%s 时异常", url), e);
        } catch (IOException e) {
            log.error(String.format("请求url：%s 时异常", url), e);
        } catch (Exception e) {
            log.error(String.format("请求url：%s 时异常", url), e);
        } finally {
            // 关闭连接,释放资源
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                log.error(String.format("请求url：%s 关闭连接时异常", url), e);
            }
        }
        return null;
    }

    private String post(String url, String json, boolean ignoreSsl, int sockectTimeOutMillis) {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        if (ignoreSsl) {
            try {
                httpclient = HttpClients.custom().setSSLHostnameVerifier(new NoopHostnameVerifier())
                        .setSSLContext(
                                new SSLContextBuilder().loadTrustMaterial(null, (chain, authType) -> true).build())
                        .build();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        CloseableHttpResponse response = null;
        try {
            HttpPost httpPost = new HttpPost(url);
            httpPost.setHeader("YX_SOURCE", "IBG_ACCOUNT");
            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setHeader("Accept", "application/json");
            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(sockectTimeOutMillis)
                    .setConnectTimeout(CONNECT_TIME_OUT).build();// 设置请求和传输超时时间
            httpPost.setConfig(requestConfig);
            httpPost.setEntity(new StringEntity(json, Charset.forName("UTF-8")));
            response = httpclient.execute(httpPost);
            // 返回状态码校验
            wxWorkHttpResultMonitor(response, url, json);

            HttpEntity entity = response.getEntity();
            String content = EntityUtils.toString(entity);
            EntityUtils.consume(entity);
            if ("".equals(content)) {
                content = null;
            }
            return content;
        } catch (ClientProtocolException e) {
            log.error(String.format("请求url：%s 时异常", url), e);
        } catch (ParseException e) {
            log.error(String.format("请求url：%s 时异常", url), e);
        } catch (IOException e) {
            log.error(String.format("请求url：%s 时异常", url), e);
        } catch (Exception e) {
            log.error(String.format("请求url：%s 时异常", url), e);
        } finally {
            // 关闭连接,释放资源
            try {
                if (response != null) {
                    response.close();
                }
                httpclient.close();
            } catch (IOException e) {
                log.error(String.format("请求url：%s 关闭连接时异常", url), e);
            }
        }
        return null;
    }

    /**
     * 提交post请求
     *
     * @param url
     *            请求地址
     * @param json
     *            请求json对象
     * @param ignoreSsl
     *            是否忽略证书
     */
    public String post(String url, String json, boolean ignoreSsl) {
        return this.post(url, json, ignoreSsl, SOCKET_TIME_OUT);
    }

    public String post(String url, String json) {
        return this.post(url, json, false);
    }

    public String post(String url, String json, int sockectTimeOutMillis) {
        return this.post(url, json, false, sockectTimeOutMillis);
    }

    public String post(String url, String json, Map<String, String> headersMap) {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        try {
            HttpPost httpPost = new HttpPost(url);
            httpPost.setHeader("YX_SOURCE", "IBG_ACCOUNT");
            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setHeader("Accept", "application/json");
            for (Entry<String, String> entry : headersMap.entrySet()) {
                httpPost.setHeader(entry.getKey(), entry.getValue());
            }
            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(SOCKET_TIME_OUT)
                    .setConnectTimeout(CONNECT_TIME_OUT).build();// 设置请求和传输超时时间
            httpPost.setConfig(requestConfig);
            httpPost.setEntity(new StringEntity(json, Charset.forName("UTF-8")));
            response = httpclient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            String content = EntityUtils.toString(entity);
            EntityUtils.consume(entity);
            if ("".equals(content)) {
                content = null;
            }
            return content;
        } catch (ClientProtocolException e) {
            log.error(String.format("请求url：%s 时异常", url), e);
        } catch (ParseException e) {
            log.error(String.format("请求url：%s 时异常", url), e);
        } catch (IOException e) {
            log.error(String.format("请求url：%s 时异常", url), e);
        } catch (Exception e) {
            log.error(String.format("请求url：%s 时异常", url), e);
        } finally {
            // 关闭连接,释放资源
            try {
                if (response != null) {
                    response.close();
                }
                httpclient.close();
            } catch (IOException e) {
                log.error(String.format("请求url：%s 关闭连接时异常", url), e);
            }
        }
        return null;
    }

    public String post(String url, byte[] bytes, Map<String, String> headersMap) {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        int timeOut = Integer.valueOf(30000);
        CloseableHttpResponse response = null;
        try {
            HttpPost httpPost = new HttpPost(url);
            httpPost.setHeader("YX_SOURCE", "IBG_ACCOUNT");
            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setHeader("Accept", "application/json");
            for (Entry<String, String> entry : headersMap.entrySet()) {
                httpPost.setHeader(entry.getKey(), entry.getValue());
            }
            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(timeOut).setConnectTimeout(timeOut)
                    .build();// 设置请求和传输超时时间
            httpPost.setConfig(requestConfig);
            httpPost.setEntity(new ByteArrayEntity(bytes));
            response = httpclient.execute(httpPost);
            // status异常校验
            wxWorkHttpResultMonitor(response, url, new String(bytes));

            HttpEntity entity = response.getEntity();
            String content = EntityUtils.toString(entity);
            EntityUtils.consume(entity);
            if ("".equals(content)) {
                content = null;
            }
            return content;
        } catch (ClientProtocolException e) {
            log.error(String.format("请求url：%s 时异常", url), e);
        } catch (ParseException e) {
            log.error(String.format("请求url：%s 时异常", url), e);
        } catch (IOException e) {
            log.error(String.format("请求url：%s 时异常", url), e);
        } catch (Exception e) {
            log.error(String.format("请求url：%s 时异常", url), e);
        } finally {
            // 关闭连接,释放资源
            try {
                if (response != null) {
                    response.close();
                }
                httpclient.close();
            } catch (IOException e) {
                log.error(String.format("请求url：%s 关闭连接时异常", url), e);
            }
        }
        return null;
    }

    /**
     * http 请求响应结果监控
     *
     * @param response
     * @param url
     * @param param
     */
    private void wxWorkHttpResultMonitor(CloseableHttpResponse response, String url, Object param) {
        try {
            WxWorkNoticeService wxWorkNoticeService = (WxWorkNoticeService) ContextContainer
                    .getBean("wxWorkNoticeService");
            wxWorkNoticeService.wxWorkHttpResultMonitor(response, url, param, null);
        } catch (Exception e) {
            log.error("企业微信发送异常Exception", e);
        }
    }

//    public static void main(String[] args) {
//        HttpUtil httpUtil = new HttpUtil();
//        byte[] b = httpUtil.getHttpsBytes("https://test60129h5.msyidai.com/contract/view?type=bill&billNo=YKD2021062314202003911&tempCde=MB00000306", null);
//        System.out.println(b);
//    }
}
