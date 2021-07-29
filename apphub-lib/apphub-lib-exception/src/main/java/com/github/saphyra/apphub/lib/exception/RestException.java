package com.github.saphyra.apphub.lib.exception;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_domain.ErrorMessage;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
public abstract class RestException extends RuntimeException {
    private final HttpStatus responseStatus;
    private final ErrorMessage errorMessage;

    RestException(String message) {
        super(message);
        responseStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        errorMessage = ErrorMessage.DEFAULT_ERROR_MESSAGE;
    }

    RestException(HttpStatus status, ErrorCode errorCode, String message) {
        this(status, errorCode, new HashMap<>(), message);
    }

    RestException(HttpStatus status, ErrorCode errorCode, Map<String, String> params, String message) {
        super(message);
        this.responseStatus = status;
        this.errorMessage = new ErrorMessage(errorCode, params);
    }

    RestException(HttpStatus status, String message) {
        this(status, ErrorMessage.DEFAULT_ERROR_MESSAGE, message);
    }

    RestException(HttpStatus status, ErrorMessage errorMessage, String message) {
        super(message);
        this.responseStatus = status;
        this.errorMessage = errorMessage;
    }

    public RestException(HttpStatus status, ErrorCode errorCode, Map<String, String> params, String message, Exception cause) {
        super(message, cause);
        this.responseStatus = status;
        this.errorMessage = new ErrorMessage(errorCode, params);
    }

    public RestException(HttpStatus status, ErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.responseStatus = status;
        this.errorMessage = new ErrorMessage(errorCode);
    }
}
