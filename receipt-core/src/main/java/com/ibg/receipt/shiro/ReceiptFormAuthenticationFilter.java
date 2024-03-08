package com.ibg.receipt.shiro;

import com.ibg.receipt.base.vo.JsonResultVo;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.PrintWriter;

/**
 *
 * @author wanghongbo01
 * @date 2022/8/26 19:24
 */
public class ReceiptFormAuthenticationFilter extends FormAuthenticationFilter {


    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        if (this.isLoginRequest(request, response)) {
            if (this.isLoginSubmission(request, response)) {
                return this.executeLogin(request, response);
            } else {
                return true;

            }
        } else {
            response.setCharacterEncoding("UTF-8");
            PrintWriter out = response.getWriter();
            out.print(JsonResultVo.error("", ""));
            out.flush();
            out.close();
            return false;
        }
    }
}
