package com.ibg.receipt.base.vo;

import java.io.Serializable;
import java.util.Date;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

@Data
public class ApiBaseRequestVo implements Serializable {

    private static final long serialVersionUID = -9020961037787133574L;

    /** 版本 */
    @NotBlank
    protected String serviceVersion;

    /** 调用方 */
    @NotBlank
    protected String sourceCode;

    /** 请求时间 */
    @NotNull
    protected Date requestTime;

    /** 请求流水号 */
    @NotBlank
    protected String requestSerialNo;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
