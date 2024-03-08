package com.ibg.receipt.base.exception.code;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 错误码
 */
@Data
@AllArgsConstructor
public class Code {

    /** 错误码 */
    private String code;

    /** 错误信息 */
    private String message;

}
