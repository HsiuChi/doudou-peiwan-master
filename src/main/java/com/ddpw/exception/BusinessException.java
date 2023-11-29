package com.ddpw.exception;

/**
 * @author Wuxy
 * @version 1.0
 * @ClassName BusinessException
 * @since 2023/1/14 15:28
 */
public class BusinessException extends RuntimeException {

    private Throwable cause;

    private String message;

    public BusinessException(String message) {
        this.message = message;
    }

    public BusinessException(Throwable cause) {
        this.cause = cause;
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public Throwable getCause() {
        return cause;
    }

    public void setCause(Throwable cause) {
        this.cause = cause;
    }

    /**
     * 不写入堆栈信息，提高性能
     */
    @Override
    public Throwable fillInStackTrace() {
        return this;
    }
}
