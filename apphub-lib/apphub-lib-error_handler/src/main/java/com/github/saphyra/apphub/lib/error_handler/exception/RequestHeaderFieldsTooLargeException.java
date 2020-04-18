package com.github.saphyra.apphub.lib.error_handler.exception;

import com.github.saphyra.apphub.lib.error_handler.domain.ErrorMessage;
import org.springframework.http.HttpStatus;

public class RequestHeaderFieldsTooLargeException extends RestException {
    private static final HttpStatus STATUS = HttpStatus.REQUEST_HEADER_FIELDS_TOO_LARGE;

    public RequestHeaderFieldsTooLargeException(String logMessage) {
        super(STATUS, logMessage);
    }

    public RequestHeaderFieldsTooLargeException(ErrorMessage errorMessage, String logMessage) {
        super(STATUS, errorMessage, logMessage);
    }

    public RequestHeaderFieldsTooLargeException(String errorCode, String logMessage) {
        super(STATUS, errorCode, logMessage);
    }
}
