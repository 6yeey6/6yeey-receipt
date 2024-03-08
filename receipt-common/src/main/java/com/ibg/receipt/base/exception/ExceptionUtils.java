package com.ibg.receipt.base.exception;

import com.ibg.receipt.base.exception.code.CodeConstants;

/**
 * Created by ning on 16-12-7.
 */
public class ExceptionUtils {

    public static ServiceException commonError(String errorMsg) {
        return ServiceException.exception(CodeConstants.C_10101002, errorMsg);
    }

    public static ServiceException notImplement(String errorMsg) {
        return ServiceException.exception(CodeConstants.C_40101001, errorMsg);
    }

    public static IllegalArgumentException invalidArg(String errorMsg) {
        return new IllegalArgumentException(errorMsg);
    }

    public static RequestException requestException(String message) {
        return new RequestException(message);
    }

}
