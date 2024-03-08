package com.ibg.receipt.vo.api.user;

import com.ibg.receipt.base.exception.Assert;
import lombok.Data;

/**
 *
 * @author wanghongbo01
 * @date 2022/8/22 17:26
 */
@Data
public class UserLoginRequestVo {

    private String userName;

    private String password;

    public void check() {
        Assert.notBlank(userName, "用户名");
        Assert.notBlank(password, "密码");
    }
}
