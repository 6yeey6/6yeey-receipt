package com.ibg.receipt.base.exception;

/**
 * 
 * 可重试异常   
 * @author: guojianchang
 * @date:   2020年2月7日 下午4:13:54   
 *
 */
public class CommonRetryableException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;

    public CommonRetryableException(String message) {
        super(message);
    }

    public CommonRetryableException(Throwable cause) {
        super(cause);
    }

    public CommonRetryableException(String message, Throwable cause) {
        super(message, cause);
    }
}
