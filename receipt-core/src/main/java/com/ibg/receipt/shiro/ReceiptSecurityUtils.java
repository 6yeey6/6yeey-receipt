package com.ibg.receipt.shiro;

import org.apache.shiro.SecurityUtils;

/**
 *
 * @author wanghongbo01
 * @date 2022/8/26 14:08
 */
public class ReceiptSecurityUtils {

    public static ShiroUser getShiroUser() {
        Object principal = SecurityUtils.getSubject().getPrincipal();
        if (principal != null) {
            if (principal instanceof ShiroUser) {
                return (ShiroUser) principal;
            } else {
                return null;
            }
        }
        return null;
    }
}
