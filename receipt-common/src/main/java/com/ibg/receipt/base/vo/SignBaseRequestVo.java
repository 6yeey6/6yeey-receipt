package com.ibg.receipt.base.vo;

import org.hibernate.validator.constraints.NotBlank;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class SignBaseRequestVo extends ApiBaseRequestVo {

    private static final long serialVersionUID = 4187912295926547199L;

    /** 签名 */
    @NotBlank
    protected String sign;


}
