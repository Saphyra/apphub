package com.github.saphyra.apphub.lib.error_handler.exception;

import com.github.saphyra.apphub.lib.error_handler.domain.ErrorMessage;
import org.springframework.http.HttpStatus;

public class GatewayTimeoutException extends RestException {
    private static final HttpStatus STATUS = HttpStatus.GATEWAY_TIMEOUT;

    public GatewayTimeoutException(String logMessage) {
        super(STATUS, logMessage);
    }

    public GatewayTimeoutException(ErrorMessage errorMessage, String logMessage) {
        super(STATUS, errorMessage, logMessage);
    }

    public GatewayTimeoutException(String errorCode, String logMessage) {
        super(STATUS, errorCode, logMessage);
    }
}
