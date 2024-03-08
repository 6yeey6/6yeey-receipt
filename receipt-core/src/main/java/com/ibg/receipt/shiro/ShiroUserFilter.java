package com.ibg.receipt.shiro;

import com.ibg.receipt.base.vo.JsonResultVo;
import com.ibg.receipt.util.JsonUtils;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

/**
 * @ClassName ShiroUserFilter
 * @Description TODO
 * @Author zhangjilong
 * @Date 2022/8/31 17:19
 */
public class ShiroUserFilter extends FormAuthenticationFilter {

    @Override
    protected void redirectToLogin(ServletRequest request, ServletResponse response) throws IOException {
        response.setContentType("application/json; charset=utf-8");
        response.getWriter().write(JsonUtils.toJson(JsonResultVo.tokenInvalid(null)));
    }

}
