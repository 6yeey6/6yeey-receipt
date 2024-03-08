package com.ibg.receipt.vo.api.user;

import com.ibg.receipt.base.exception.Assert;
import lombok.Data;

/**
 *
 * @author wanghongbo01
 * @date 2022/11/1 19:48
 */
@Data
public class UserCheckRequestVo {

    private String userName;


    public void check() {
        Assert.notBlank(userName, "用户名");
    }
}
