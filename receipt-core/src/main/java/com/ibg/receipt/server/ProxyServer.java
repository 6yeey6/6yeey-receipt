package com.ibg.receipt.server;

import com.ibg.receipt.base.exception.ServiceException;
import com.ibg.receipt.base.exception.code.CodeConstants;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;

import lombok.extern.slf4j.Slf4j;

/**
 * 代理系统
 */
@Slf4j
@Component
public class ProxyServer {

    /**
     * 代理系统server
     */
    @Value("${server.proxy.url}")
    private String serverUrl;

    /**
     * 用户借款信息查询
     * 
     * @return
     */
    public String getUserLoanOrders() {
        return serverUrl + "/account/loan/payoff";
    }

    /**
     * 用户借款信息明细查询
     *
     * @return
     */
    public String getUserLoanOrderDetails() {
        return serverUrl + "/account/loan/detail";
    }

    /**
     * 解析结果
     * 
     * @param result
     * @return
     */
    public JSONObject checkProxyResult(String result) {

        if (result == null) {
            throw new ServiceException(CodeConstants.C_10101000.getCode(), "请求账务系统失败result:null");
        }
        JSONObject jsonObject = JSONObject.parseObject(result);
        String status = jsonObject.getString("status");

        if (StringUtils.isBlank(status) || !status.equals(ProxyResponseStatus.SUCCESS.toString())) {
            throw new ServiceException(status, jsonObject.getString("message"));
        }
        return jsonObject;
    }

    /**
     * 是否成功
     *
     * @param status
     * @return
     */
    public static Boolean isSuccess(String status) {
        return ProxyResponseStatus.SUCCESS.toString().equals(status);
    }

    public enum ProxyResponseStatus {
        SUCCESS("000000"), ERROR("000001");

        private final String status;

        ProxyResponseStatus(String status) {
            this.status = status;
        }

        @Override
        public String toString() {
            return this.status;
        }

    }

}
