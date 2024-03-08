package com.ibg.receipt.service.receipt.remote;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.ibg.receipt.base.exception.ServiceException;
import com.ibg.receipt.base.exception.code.CodeConstants;
import com.ibg.receipt.server.CustomerServer;
import com.ibg.receipt.util.HttpUtil;
import com.ibg.receipt.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sun.misc.BASE64Encoder;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author wanghongbo01
 * @date 2022/8/30 15:06
 */
@Service
@Slf4j
public class CustomerRemoteService {

    @Autowired
    private CustomerServer customerServer;

    public String tokenCheck(String token, String partnerUserId) {

        String url = customerServer.getCustomerTokenCheck();

        Map<String, String> headersMap = new HashMap<>();
        headersMap.put("Authorization", getAuthorization());
        url = url +"?token=" + token + "&partnerUserId=" + partnerUserId;
        log.info("请求客服token验证,url:{}", url);
        HttpUtil http = new HttpUtil();
        String result = http.post(url, "", headersMap);
        log.info("请求客服token验证,url:{},response:{}", url, result);

        JSONObject jsonObject = customerServer.checkCustomerResult(result);
        JSONObject data = jsonObject.getJSONObject("data");
        if(data != null && data.containsKey("userName")) {
            return data.getString("userName");
        } else {
            log.error("请求客服token验证返回数据异常:{}, result");
            throw new ServiceException(CodeConstants.C_10101002.getCode(), "请求客服token验证返回数据异常");
        }
    }

    private String getAuthorization() {
        String data = customerServer.getUserName() + ":" + customerServer.getPassword();
        BASE64Encoder encoder = new BASE64Encoder();
        return "Basic " + encoder.encode(data.getBytes());
    }

    public static void main(String[] args) {
        String data = "bill_app" + ":bill_app";
        BASE64Encoder encoder = new BASE64Encoder();
        System.out.println(encoder.encode(data.getBytes()));
    }

    private String buildRequest(String token, String partnerUserId) {
        Map<String, String> request = Maps.newHashMap();

        request.put("token", token);
        request.put("partnerUserId", partnerUserId);

        return JsonUtils.toJson(request);
    }
}
