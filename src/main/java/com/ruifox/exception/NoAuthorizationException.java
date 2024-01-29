package com.ruifox.exception;

/**
 * @author 离狐千慕
 * @version 1.0
 * @date 2024/1/17 14:14
 */
public class NoAuthorizationException extends RuntimeException{
    public NoAuthorizationException(String exception) {
        super(exception);
    }
    public NoAuthorizationException(String exception, Throwable cause) {
        super(exception, cause);
    }
    public NoAuthorizationException(Throwable cause) {
        super(cause);
    }
}
