package com.github.saphyra.apphub.lib.error_handler.exception;

import com.github.saphyra.apphub.lib.error_handler.domain.ErrorMessage;
import org.springframework.http.HttpStatus;

public class PayloadTooLargeException extends RestException {
    private static final HttpStatus STATUS = HttpStatus.PAYLOAD_TOO_LARGE;

    public PayloadTooLargeException(String logMessage) {
        super(STATUS, logMessage);
    }

    public PayloadTooLargeException(ErrorMessage errorMessage, String logMessage) {
        super(STATUS, errorMessage, logMessage);
    }

    public PayloadTooLargeException(String errorCode, String logMessage) {
        super(STATUS, errorCode, logMessage);
    }
}
