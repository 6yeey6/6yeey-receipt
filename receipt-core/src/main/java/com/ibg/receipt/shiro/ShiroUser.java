package com.ibg.receipt.shiro;

import com.ibg.receipt.enums.business.UserSource;
import lombok.Data;

import java.io.Serializable;

/**
 *
 * @author wanghongbo01
 * @date 2022/8/26 11:39
 */
@Data
public class ShiroUser implements Serializable {

    private String userName;

    private UserSource userSource;

}
