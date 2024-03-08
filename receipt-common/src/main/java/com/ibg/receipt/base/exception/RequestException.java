package com.ibg.receipt.base.exception;

/**
 * 请求相关的异常，比如未返回与预期的报文或者响应报文为空等。 一般这种异常是预期外的异常，需要抛出人工介入处理。
 * 
 * @author: guojianchang
 * @date: 2019年9月27日 下午5:05:29
 */
public class RequestException extends RuntimeException {

    private static final long serialVersionUID = -3033004962083009726L;

    public RequestException() {
        super();
    }

    public RequestException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public RequestException(String message, Throwable cause) {
        super(message, cause);
    }

    public RequestException(String message) {
        super(message);
    }

    public RequestException(Throwable cause) {
        super(cause);
    }

}
