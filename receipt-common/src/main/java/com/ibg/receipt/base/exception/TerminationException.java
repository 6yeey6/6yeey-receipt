package com.ibg.receipt.base.exception;

/**
 * @desc: 流程引擎运行时，需要终止流程运行，进行人工干预时抛出该异常
 * @author: lvzhonglin
 * @date: 2021/4/16 20:15
 */
public class TerminationException extends RuntimeException{

    private static final long serialVersionUID = 1L;

    public TerminationException(String message) {
        super(message);
    }

    public TerminationException(Throwable cause) {
        super(cause);
    }

    public TerminationException(String message, Throwable cause) {
        super(message, cause);
    }
}
