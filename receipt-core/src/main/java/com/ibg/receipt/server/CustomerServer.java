package com.ibg.receipt.server;

import com.alibaba.fastjson.JSONObject;
import com.ibg.receipt.base.exception.ServiceException;
import com.ibg.receipt.base.exception.code.CodeConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 *
 * @author wanghongbo01
 * @date 2022/8/30 14:57
 */
@Slf4j
@Component
public class CustomerServer {

    @Value("${server.customer.url}")
    private String serverUrl;

    @Value("${server.customer.userName}")
    private String userName;

    @Value("${server.customer.password}")
    private String password;

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public String getCustomerTokenCheck() {
        return serverUrl + "/api/bill/auth";
    }

    public JSONObject checkCustomerResult(String result) {

        if (result == null) {
            throw new ServiceException(CodeConstants.C_10101000.getCode(), "请求客服系统失败result:null");
        }
        JSONObject jsonObject = JSONObject.parseObject(result);
        String status = jsonObject.getString("code");

        if (StringUtils.isBlank(status) || !status.equals(CustomerServer.CustomerResponseStatus.SUCCESS.toString())) {
            throw new ServiceException(status, jsonObject.getString("msg"));
        }
        return jsonObject;
    }

    public enum CustomerResponseStatus {

        SUCCESS("1000");

        private final String status;

        CustomerResponseStatus(String status) {
            this.status = status;
        }

        @Override
        public String toString() {
            return this.status;
        }

    }
}
