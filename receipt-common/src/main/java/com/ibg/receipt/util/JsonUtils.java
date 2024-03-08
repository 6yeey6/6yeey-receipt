package com.ibg.receipt.util;

import static com.alibaba.fastjson.serializer.SerializerFeature.WriteNullStringAsEmpty;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ibg.receipt.sensitive.FastJsonSensitizeForUtilFilter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Request;
import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibg.receipt.base.exception.ExceptionUtils;
import retrofit2.Invocation;
import org.hibernate.validator.constraints.NotBlank;

@Slf4j
public class JsonUtils {

    public static final ObjectMapper mapper = new ObjectMapper();


    public static final List<String> NEED_DECODED_KEY_LIST = Stream
            .of("fundName","account","userName","address","bankName","trustName","userPid")
            .collect(Collectors.toList());
    /**
     *常见四要素日志剔除
     */
    private static final List<String> NO_FILTER_KEYS = Stream
        .of("firstContactName", "idCardNo", "bankReservedMobile", "bankCardNo", "accountName", "secondContactName","base64FileContent","userName","userMobile")
        .collect(Collectors.toList());

    /**
     * 对象转Json
     *
     * @param obj
     * @return
     */
    public static String toJson(Object obj) {
        if (obj == null) {
            return null;
        }
        return JSON.toJSONString(obj);
    }

    /**
     *  脱敏日志
     * @param obj
     * @return
     */
    public static String toJsonWithSensitive(Object obj) {
        if (obj == null) {
            return null;
        }
        FastJsonSensitizeForUtilFilter filter = new FastJsonSensitizeForUtilFilter();
        return JSON.toJSONString(obj, filter);
    }

    public static String  toJsonFromChannel(Request request){
        String requestJson ="";
        try{
            requestJson = com.ibg.receipt.util.OkHttpUtils.readRequestBody(request.body());
            //先判断是否是json
            if(cn.hutool.json.JSONUtil.isJson(requestJson)){
                Class<?>[] classes = request.tag(Invocation.class).method().getParameterTypes();
                for(Class clz : classes){
                    if(!clz.equals(Object.class)){
                        Object obj=JSON.parseObject(requestJson,clz);
                        return toJsonWithSensitive(obj);
                    }
                }
            }
        }catch (Exception e){
        }
        return requestJson;
    }

    /**
     *  增加字段过滤
     * @param obj
     * @param filterKey
     * @return
     */
    public static String toJson(Object obj, String filterKey) {
        if (obj == null) {
            return null;
        }

        if (com.ibg.receipt.util.StringUtils.isEmpty(filterKey)) {
            return toJson(obj);
        }
        SimplePropertyPreFilter filter = new SimplePropertyPreFilter();
        filter.getExcludes().add(filterKey);
        return JSON.toJSONString(obj, filter);
    }

    public static String toJsonOutKeys(Object obj) {
        return toJson(obj,NO_FILTER_KEYS);
    }

    /**
     * 过滤多个字段
     * @param obj
     * @param filterKeys
     * @return
     */
    public static String toJson(Object obj, List<String> filterKeys) {
        if (obj == null) {
            return null;
        }
        if (CollectionUtils.isEmpty(filterKeys)) {
            return toJson(obj);
        }
        SimplePropertyPreFilter filter = new SimplePropertyPreFilter();
        for (String key : filterKeys) {
            filter.getExcludes().add(key);
        }
        return JSON.toJSONString(obj, filter);
    }

    public static String toJsonContainsNull(Object obj) {
        if (obj == null) {
            return null;
        }
        return JSON.toJSONString(obj, WriteNullStringAsEmpty);
    }

    /**
     * Json转对象
     *
     * @param json
     * @param clazz
     * @return
     */
    public static <T> T toObject(String json, Class<T> clazz) {
        if (StringUtils.isBlank(json)) {
            return null;
        }
        if (clazz == null) {
            return null;
        }
        return JSON.parseObject(json, clazz);
    }

    /**
     * Json转列表
     *
     * @param json
     * @param clazz
     * @return
     */
    public static <T> List<T> toList(String json, Class<T> clazz) {
        if (StringUtils.isBlank(json)) {
            return null;
        }
        if (clazz == null) {
            return null;
        }
        return new ArrayList<>(JSON.parseArray(json, clazz));
    }

    /**
     * 将json字符串反序列号成对象
     *
     * @param <T>
     * @param json
     * @param clazz
     * @return
     */
    public static <T> T readValue(String json, Class<T> clazz) {
        try {
            return mapper.readValue(json, clazz);
        } catch (Exception e) {
            throw ExceptionUtils.commonError("解析参数异常");
        }
    }

    /****
     * @Description :根据json字段名称查找对应值
     * @param json
     * @param param
     * @return java.lang.String
     */
    public static String getJsonParam(String json, String param) {
        String regex = param + "\":(.*?)(,|})";//
        Matcher matcher = Pattern.compile(regex).matcher(json);
        String returnStr = null;
        while (matcher.find()) {
            String ret = matcher.group(1);
            returnStr = ret;
        }
        if (returnStr == null) {
            return null;
        } else {
            return returnStr.replaceAll("\"", "").replaceAll("}", "").replaceAll("]", "");
        }

    }

    /***
     * @Description :更新JSON数据
     * @param objJson
     * @param nodeKey
     * @param nodeValue
     * @return java.lang.Object
     */
    public static Object updateJson(Object objJson, String nodeKey, String nodeValue) {
        //如果obj为json数组
        if (objJson instanceof JSONArray) {
            JSONArray objArray = (JSONArray) objJson;
            for (int i = 0; i < objArray.size(); i++) {
                updateJson(objArray.get(i), nodeKey, nodeValue);
            }
        } else if (objJson instanceof JSONObject) {
            JSONObject jsonObject = (JSONObject) objJson;
            Iterator it = jsonObject.keySet().iterator();
            while (it.hasNext()) {
                String key = it.next().toString();
                Object object = jsonObject.get(key);
                if (object instanceof JSONArray) {
                    JSONArray objArray = (JSONArray) object;
                    updateJson(objArray, nodeKey, nodeValue);
                } else if (object instanceof JSONObject) {
                    updateJson(object, nodeKey, nodeValue);
                } else {
                    if (key.equals(nodeKey)) {
                        //替换数据
                        jsonObject.put(key, nodeValue);
                    }
                }
            }
        }
        return objJson;
    }

    /**
     * 解密
     * @param result
     * @return
     */
    public static String getDecodedResult(Object result){
        String tempResultStr = null;
        for (String key : JsonUtils.NEED_DECODED_KEY_LIST) {
            if (result != null && JsonUtils.getJsonParam(String.valueOf(result), key) != null) {
                String value = JsonUtils.getJsonParam(String.valueOf(result), key).replace(" ", "");
                JSONObject val = tempResultStr == null ? JSONObject.parseObject(String.valueOf(result)) : JSONObject.parseObject(tempResultStr);
                log.info("当前key:{}", key);
                log.info("发票系统加密前字段:{}", value);
                Object tempResult = JsonUtils.updateJson(val, key, EncryptUtil.getDecoded(value));
                log.info("发票系统加密前字段:{}", tempResult.toString());
                tempResultStr = tempResult.toString();
            }
        }
        return tempResultStr;
    }

    public static void main(String[] args) {

        String str = "{\n" +
                "    \"data\": {\n" +
                "        \"list\": [\n" +
                "            {\n" +
                "                \"fundName\": \"Ft1Mr/WtVv524mK5T7vZcA==\",\n" +
                "                \"userName\": \"Ft1Mr/WtVv524mK5T7vZcA==\",\n" +
                "                \"userPid\": \"Ft1Mr/WtVv524mK5T7vZcA==\",\n" +
                "                \"address\": \"Ft1Mr/WtVv524mK5T7vZcA==\",\n" +
                "                \"bankName\": \"Ft1Mr/WtVv524mK5T7vZcA==\",\n" +
                "                \"trustName\": \"Ft1Mr/WtVv524mK5T7vZcA==\"\n" +
                //"                \"account\": \"Ft1Mr/WtVv524mK5T7vZcA==\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"fundName\": \"xfNoaFLqGC55y0nDk4ZM3A==\",\n" +
                "                \"userName\": \"xfNoaFLqGC55y0nDk4ZM3A==\",\n" +
                "                \"userPid\": \"xfNoaFLqGC55y0nDk4ZM3A==\",\n" +
                "                \"address\": \"xfNoaFLqGC55y0nDk4ZM3A==\",\n" +
                "                \"bankName\": \"xfNoaFLqGC55y0nDk4ZM3A==\",\n" +
                "                \"trustName\": \"xfNoaFLqGC55y0nDk4ZM3A==\"\n" +
                //"                \"account\": \"xfNoaFLqGC55y0nDk4ZM3A==\"\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    \"message\": \"\",\n" +
                "    \"status\": \"0000\",\n" +
                "    \"success\": true\n" +
                "}";
        System.out.println(getDecodedResult(str));
        //JSONObject val = JSONObject.parseObject(str);
        //Object tempResult =JsonUtils.updateJson(val,"userName","测试");
        //System.out.println(tempResult.toString());
    }
}
