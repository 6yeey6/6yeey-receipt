package com.ibg.receipt.util;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.CharsetUtils;

import java.io.File;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class MetaFsUtil {
    public static void main(String[] args) {
        try {
            /*String fileId = TestMetaFs.uploadToMetaFs(new File("d:/logs/测试upload.txt"),
                null, null, null, "TEST", "FUND");
            /*Map<String, byte[]> map = MetaFsUtil.downloadFromMetaFs("e8ed7222-be82-4693-952c-5e6bfcc26898-f-1658391379680");
            for (Map.Entry<String, byte[]> entry : map.entrySet()) {
                FileUtils.writeByteArrayToFile(new File("d:/logs/down/" + entry.getKey()), entry.getValue());
            }*/
            System.out.println("hehe");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    //start
    public static String uploadToMetaFs(String fileSystemUrl,String secret,String uniqueKey,File file, InputStream fileStream, String fileName, Long expireTime,
                                        String customType, String businessType) throws Exception {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost request = new HttpPost(fileSystemUrl);
            MultipartEntityBuilder reqEntity = MultipartEntityBuilder.create();

            if (file != null) {
                reqEntity.addPart("uFile", new FileBody(file));
                reqEntity.addPart("filename",
                    new StringBody(file.getName(), ContentType.create("text/plain", Consts.UTF_8)));
            } else if (fileStream != null) {
                reqEntity.addPart("uFile", new InputStreamBody(fileStream, fileName));
                reqEntity.addPart("filename",
                    new StringBody(fileName, ContentType.create("text/plain", Consts.UTF_8)));
            } else {
                throw new IllegalArgumentException("非法请求，没有有效的文件内容！");
            }

            Map<String, String> paramMap = Maps.newHashMap();
            paramMap.put("customType", customType);
            paramMap.put("businessType", businessType);
            reqEntity.addTextBody("params", JSON.toJSONString(paramMap), ContentType.APPLICATION_JSON);

            request.setHeader("secret", secret);
            request.setHeader("accessor_unique_key", uniqueKey);
            request.setEntity(reqEntity
                .setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
                .setCharset(CharsetUtils.get("UTF-8"))
                .build());

            try (CloseableHttpResponse response = client.execute(request)) {
                log.debug(JsonUtils.toJson(response));
                if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                    throw new Exception(response.getStatusLine().getReasonPhrase());
                }

                String result = IOUtils.toString(response.getEntity().getContent(), StandardCharsets.UTF_8);
                JSONObject vo = JSONObject.parseObject(result);
                if (ObjectUtil.isNull(vo) || !"0".equals(vo.get("code").toString()) || ObjectUtil.isNull(vo.get("data"))) {
                    throw new Exception("文件META上传失败");
                }
                JSONObject data = JSONObject.parseObject(vo.get("data").toString());
                return data.get("fileId").toString();
            }
        }
//        throw new Exception("上传文件出现问题，无法获取文件ID");
    }

    public static Map<String, Object> downloadFromMetaFs(String downloadUrl,String secret,final String fileId) throws Exception {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(downloadUrl + fileId);
            request.setHeader("secret", secret);

            try (CloseableHttpResponse response = client.execute(request)) {
                if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                    throw new Exception(IOUtils.toString(response.getEntity().getContent(), StandardCharsets.UTF_8));
                }
                Header[] contentDisposition = response.getHeaders("Content-Disposition");
                String fileName = "file";
                if (!ArrayUtils.isEmpty(contentDisposition)) {
                    HeaderElement[] e = contentDisposition[0].getElements();
                    fileName = e[0].getParameterByName("fileName").getValue();
                    fileName = URLDecoder.decode(fileName, "UTF-8");
                    //fileName = new String(fileName.getBytes("ISO-8859-1"), "UTF-8");
                }

                Map<String, Object> result = new HashMap<>(16);
                result.put("fileName",fileName);
                result.put("fileBytes",IOUtils.toByteArray(response.getEntity().getContent()));
                return result;
            }
        }
    }
    //end
}
