package com.ibg.receipt.vo.api.user;

import com.ibg.receipt.base.exception.Assert;
import com.ibg.receipt.base.exception.ServiceException;
import com.ibg.receipt.base.exception.code.CodeConstants;
import lombok.Data;

/**
 *
 * @author wanghongbo01
 * @date 2022/8/23 16:51
 */
@Data
public class UserRegisterRequestVo {

    private String userName;

    private String confirmPassword;

    private String password;

    private String email;

    public void check() {
        Assert.notBlank(userName, "用户名");
        Assert.notBlank(password, "密码");
        Assert.notBlank(confirmPassword, "确认密码");
        if(!password.equals(confirmPassword)) {
            throw ServiceException.exception(CodeConstants.C_10101002, "密码和确认密码不相等");
        }
    }
}
