package com.github.datnguyenzzz.Exceptions;

public class SystemException extends RuntimeException {
    public SystemException(String msg) {
        super(msg);
    }

    public SystemException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public SystemException(Throwable cause) {
        super(cause);
    }
}
