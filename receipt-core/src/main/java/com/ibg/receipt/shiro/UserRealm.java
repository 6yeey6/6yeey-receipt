package com.ibg.receipt.shiro;

import com.ibg.receipt.base.exception.ServiceException;
import com.ibg.receipt.enums.business.UserSource;
import com.ibg.receipt.model.receipt.ReceiptUser;
import com.ibg.receipt.service.receipt.ReceiptUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author wanghongbo01
 * @date 2022/8/22 19:58
 */
@Slf4j
public class UserRealm extends AuthorizingRealm {

    @Autowired
    private ReceiptUserService receiptUserService;

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        return null;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {

        String userName = (String) token.getPrincipal();
        ReceiptUser receiptUser = receiptUserService.getReceiptUserByUserName(userName);
        if(receiptUser != null) {
            ShiroUser shiroUser = new ShiroUser();
            shiroUser.setUserName(receiptUser.getUserName());
            shiroUser.setUserSource(UserSource.MANAGEMENT_PLATFORM);
            return new SimpleAuthenticationInfo(shiroUser, //用户
                    receiptUser.getPassword(), //密码
                    this.getName()//realm name
            );
        } else {
            log.warn("用户信息为空! username : {}", userName);
            throw new ServiceException(
                    "查询不到用户信息! userName = " + userName);
        }

    }
}
