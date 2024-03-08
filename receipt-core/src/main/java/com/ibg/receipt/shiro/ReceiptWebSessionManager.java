package com.ibg.receipt.shiro;

import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;

/**
 *
 * @author wanghongbo01
 * @date 2022/8/22 19:47
 */
public class ReceiptWebSessionManager extends DefaultWebSessionManager {

    @Override
    protected Serializable getSessionId(ServletRequest request, ServletResponse response) {
        HttpServletRequest req = (HttpServletRequest) request;
        Serializable sessionId = req.getHeader(ShiroConstant.RECEIPT_TOKEN);

        if(sessionId != null){
            return sessionId;
        }

        // 如果消息头获取为空，则使用shiro原来的方式获取
        return super.getSessionId(request, response);
    }
}
