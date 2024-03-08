package com.ibg.receipt.util;

import java.util.List;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.ibg.receipt.base.exception.ServiceException;
import com.ibg.receipt.base.exception.code.CodeConstants;
import com.ibg.receipt.base.vo.PageInfo;

/**
 * @author yushijun
 * @date 2018/9/8
 */
public class ResultCheckUtil {
    public static final String SEPARATOR = ".";

    /**
     * 校验返回值和状态 checkKey使用.分隔，校验内层 如data.payInfo,会校验data和payInfo是否存在
     *
     * @param jsonObject
     * @param checkKey
     * @return
     */
    public static boolean checkResultPraram(JSONObject jsonObject, String statusKey, String statusSuccessValue,
            boolean throwException, String... checkKey) {
        if (jsonObject == null || StringUtils.isBlank(statusSuccessValue)) {
            return false;
        }

        boolean success = true;
        String message = "";

        List<String> status = Lists.newArrayList(StringUtils.splitToList(statusKey,SEPARATOR));

        //校验statusKey之前字段是否存在
        if (CollectionUtils.isNotEmpty(status) && status.size()>1) {
            JSONObject root = jsonObject;
            for (int i = 0; i < status.size()-1; i++) {
                JSONObject child = root.getJSONObject(status.get(i));
                if (child == null || child.isEmpty()) {
                    success = false;
                    message = "不存在Key" + status.get(i);
                    break;
                }
                root = child;
            }
            //key之前字段都存在，校验key-value 不能为空
            String value = root.getString(status.get(status.size()-1));
            // 空字符串也算
            if(value == null){
                success = false;
                message = "不存在Key" + status.get(status.size()-1);
            }
        }

        if(!success ){
            if(throwException){
                throw new ServiceException(CodeConstants.C_10101006.getCode(),message);
            }
            return success;
        }

        if (checkKey != null && checkKey.length > 0) {
            List<String> list = Lists.newArrayList(checkKey);
            // 分隔每个字段
            List<List<String>> splitList = list.stream().map(s -> {
                if (s.contains(SEPARATOR)) {
                    return Lists.newArrayList(StringUtils.splitToList(s,SEPARATOR));
                }
                return Lists.newArrayList(s);
            }).collect(Collectors.toList());

            // 对每个key 做校验
            for (List<String> r : splitList) {

                JSONObject root = jsonObject;
                //校验最后key之前字段是否存在
                for (int i = 0; i < r.size() - 1; i++) {
                    String s = r.get(i);
                    JSONObject child = root.getJSONObject(s);
                    if (child == null || child.isEmpty()) {
                        success = false;
                        message = "不存在Key" + status.get(i);
                        break;
                    }
                    root = child;
                }
                //key之前字段都存在，校验key-value 不能为空
                String value = root.getString(r.get(r.size()-1));
                // 空字符串也算
                if(value == null){
                    success = false;
                    message = "不存在Key" + status.get(status.size()-1);
                }
            }
        }
        // 返回处理结果
        if(!success){
            if(throwException){
                throw new ServiceException(CodeConstants.C_10101006.getCode(),message);
            }
        }
        return success;
    }

    public static boolean checkResultPraram(JSONObject jsonObject, String statusKey, String statusSuccessValue,
            String... checkKey) {
        return checkResultPraram(jsonObject, statusKey, statusSuccessValue, false, checkKey);
    }

    public static void main(String[] args) {
        String x = "{\"body\":{\"balanceUsedInfo\":{\"CE4E438F4B4A41F18C736BA536736EE5\":0},"
                + "\"canPayOff\":false,\"canRepay\":true,\"frozenAmount\":0" + ",\"message\":\"允许借款还款\","
                + "\"paySubjectAmt\":[{\"amount\":121.00,\"paySubject\":\"ZLCT\"}],\"splitAccount\":false,\"status\":\"OVERDUE\"},"
                + "\"header\":{\"attach\":\"\",\"channel\":\"PAY_DAY_LOAN\",\"message\":\"\",\"outOrderId\":\"180908053FAB2C008DA74048BAA6E4B00524CEB3\","
                + "\"status\":\"0\",\"timestamp\":1536399133485,\"version\":\"1.0\"}}";
        String status ="{\"attach\":\"\",\"channel\":\"PAY_DAY_LOAN\",\"message\":\"\",\"outOrderId\":\"180908053FAB2C008DA74048BAA6E4B00524CEB3\","
            + "\"status\":\"0\",\"timestamp\":1536399133485,\"version\":\"1.0\"}";
        JSONObject jsonObject = JSONObject.parseObject(x);
        JSONObject jsonObjectStatus = JSONObject.parseObject(status);
        boolean result = checkResultPraram(jsonObject,"header.status","0",false,"body","body.canPayOff");
        boolean result111 = checkResultPraram(jsonObjectStatus,"header.status","0",false,"body","body.canPayOff");

        System.out.printf("结果为"+result);
    }

}
