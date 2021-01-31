package com.github.saphyra.apphub.lib.exception;

import com.github.saphyra.apphub.lib.common_domain.ErrorMessage;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

public class InternalServerErrorException extends RestException {
    private static final HttpStatus STATUS = HttpStatus.INTERNAL_SERVER_ERROR;

    public InternalServerErrorException(String logMessage) {
        this(logMessage, null);
    }

    public InternalServerErrorException(String logMessage, Throwable cause) {
        super(STATUS, createErrorMessage(logMessage), logMessage, cause);
    }

    private static ErrorMessage createErrorMessage(String logMessage) {
        Map<String, String> params = new HashMap<>();
        params.put(KEY, logMessage);
        return new ErrorMessage(NON_TRANSLATED_ERROR_CODE, params);
    }
}
