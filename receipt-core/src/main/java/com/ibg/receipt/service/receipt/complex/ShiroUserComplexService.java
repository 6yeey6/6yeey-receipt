package com.ibg.receipt.service.receipt.complex;

import com.ibg.receipt.base.exception.ServiceException;
import com.ibg.receipt.base.exception.code.CodeConstants;
import com.ibg.receipt.enums.business.UserSource;
import com.ibg.receipt.redis.service.RedisService;
import com.ibg.receipt.service.receipt.remote.CustomerRemoteService;
import com.ibg.receipt.shiro.ShiroConstant;
import com.ibg.receipt.shiro.ShiroUser;
import com.ibg.receipt.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author wanghongbo01
 * @date 2022/9/2 17:02
 */
@Service
public class ShiroUserComplexService {

    @Autowired
    private CustomerRemoteService customerRemoteService;

    @Autowired
    private RedisService redisService;

    public ShiroUser getShiroUser(HttpServletRequest request) {
        String token = request.getHeader(ShiroConstant.RECEIPT_TOKEN);
        String partnerUserId = request.getHeader(ShiroConstant.PARTNER_USER_ID);
        if (StringUtils.isBlank(token)) {
            throw new ServiceException(CodeConstants.C_10101002.getCode(), "token不存在");
        }

        if (StringUtils.isBlank(partnerUserId)) {
            throw new ServiceException(CodeConstants.C_10101002.getCode(), "partnerUserId不存在");
        }
        String tokeKey = ShiroConstant.CUSTOMER_KEY_PREFIX + token;

        ShiroUser shiroUser;
        if (redisService.exists(tokeKey)) {
            shiroUser = (ShiroUser) redisService.getObject(tokeKey);
        } else {
            shiroUser = new ShiroUser();
            shiroUser.setUserSource(UserSource.CUSTOMER_SYSTEM);
            shiroUser.setUserName(customerRemoteService.tokenCheck(token, partnerUserId));
        }
        redisService.setObject(tokeKey, 24 * 60 * 60, shiroUser);
        return shiroUser;
    }
}
