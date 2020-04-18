package com.github.saphyra.apphub.lib.error_handler.exception;

import com.github.saphyra.apphub.lib.error_handler.domain.ErrorMessage;
import org.springframework.http.HttpStatus;

public class RequestTimeoutException extends RestException {
    private static final HttpStatus STATUS = HttpStatus.REQUEST_TIMEOUT;

    public RequestTimeoutException(String logMessage) {
        super(STATUS, logMessage);
    }

    public RequestTimeoutException(ErrorMessage errorMessage, String logMessage) {
        super(STATUS, errorMessage, logMessage);
    }

    public RequestTimeoutException(String errorCode, String logMessage) {
        super(STATUS, errorCode, logMessage);
    }
}
