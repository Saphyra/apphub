package com.github.saphyra.apphub.lib.exception;

import com.github.saphyra.apphub.lib.common_domain.ErrorMessage;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

public class RestException extends RuntimeException {
    private static final ErrorMessage DEFAULT_ERROR_CODE = new ErrorMessage("", new HashMap<>());
    static final String NON_TRANSLATED_ERROR_CODE = "NON_TRANSLATED_ERROR";
    static final String KEY = "key";

    @Getter
    private final HttpStatus responseStatus;

    @Getter
    private final ErrorMessage errorMessage;

    public RestException(HttpStatus responseStatus, String logMessage) {
        super(logMessage);
        this.responseStatus = responseStatus;
        this.errorMessage = DEFAULT_ERROR_CODE;
    }

    public RestException(HttpStatus responseStatus, ErrorMessage errorMessage, String logMessage) {
        super(logMessage);
        this.responseStatus = responseStatus;
        this.errorMessage = errorMessage;
    }

    public RestException(HttpStatus responseStatus, String errorCode, String logMessage) {
        super(logMessage);
        this.responseStatus = responseStatus;
        this.errorMessage = new ErrorMessage(errorCode);
    }

    public RestException(HttpStatus responseStatus, String logMessage, Throwable cause) {
        super(logMessage, cause);
        this.responseStatus = responseStatus;
        this.errorMessage = DEFAULT_ERROR_CODE;
    }

    public RestException(HttpStatus status, ErrorMessage errorMessage, String logMessage, Throwable cause) {
        super(logMessage, cause);
        this.responseStatus = status;
        this.errorMessage = errorMessage;
    }

    public static RestException createNonTranslated(HttpStatus status, String message) {
        return createNonTranslated(status, message, null);
    }

    public static RestException createNonTranslated(HttpStatus status, String message, Throwable cause) {
        Map<String, String> params = new HashMap<>();
        params.put(KEY, message);
        ErrorMessage errorMessage = new ErrorMessage(NON_TRANSLATED_ERROR_CODE, params);
        return new RestException(status, errorMessage, message, cause);
    }
}
