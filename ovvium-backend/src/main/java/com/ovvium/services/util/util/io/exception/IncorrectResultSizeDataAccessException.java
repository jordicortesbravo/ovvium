package com.ovvium.services.util.util.io.exception;

public class IncorrectResultSizeDataAccessException extends RuntimeException {

    private static final long serialVersionUID = -1792961733061564599L;

    public IncorrectResultSizeDataAccessException() {
        super();
    }

    public IncorrectResultSizeDataAccessException(String msg) {
        super(msg);
    }

    public IncorrectResultSizeDataAccessException(Throwable cause) {
        super(cause);
    }

    public IncorrectResultSizeDataAccessException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
