package com.github.saphyra.apphub.lib.error_handler.exception;

import com.github.saphyra.apphub.lib.error_handler.domain.ErrorMessage;
import org.springframework.http.HttpStatus;

public class TooManyRequestsException extends RestException {
    private static final HttpStatus STATUS = HttpStatus.TOO_MANY_REQUESTS;

    public TooManyRequestsException(String logMessage) {
        super(STATUS, logMessage);
    }

    public TooManyRequestsException(ErrorMessage errorMessage, String logMessage) {
        super(STATUS, errorMessage, logMessage);
    }

    public TooManyRequestsException(String errorCode, String logMessage) {
        super(STATUS, errorCode, logMessage);
    }
}
