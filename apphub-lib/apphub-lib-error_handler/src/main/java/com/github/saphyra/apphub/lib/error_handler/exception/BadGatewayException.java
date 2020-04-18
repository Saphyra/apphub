package com.github.saphyra.apphub.lib.error_handler.exception;

import com.github.saphyra.apphub.lib.error_handler.domain.ErrorMessage;
import org.springframework.http.HttpStatus;

public class BadGatewayException extends RestException {
    private static final HttpStatus STATUS = HttpStatus.BAD_GATEWAY;

    public BadGatewayException(String logMessage) {
        super(STATUS, logMessage);
    }

    public BadGatewayException(ErrorMessage errorMessage, String logMessage) {
        super(STATUS, errorMessage, logMessage);
    }

    public BadGatewayException(String errorCode, String logMessage) {
        super(STATUS, errorCode, logMessage);
    }
}
