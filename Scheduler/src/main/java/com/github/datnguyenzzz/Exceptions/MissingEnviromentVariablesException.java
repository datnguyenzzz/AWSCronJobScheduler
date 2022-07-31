package com.github.datnguyenzzz.Exceptions;

public class MissingEnviromentVariablesException extends RuntimeException {
    
    public MissingEnviromentVariablesException(String msg) {
        super(msg);
    }

    public MissingEnviromentVariablesException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public MissingEnviromentVariablesException(Throwable cause) {
        super(cause);
    }
}
