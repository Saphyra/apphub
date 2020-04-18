package com.github.saphyra.apphub.lib.error_handler.exception;

import com.github.saphyra.apphub.lib.error_handler.domain.ErrorMessage;
import org.springframework.http.HttpStatus;

public class NetworkAuthenticationRequiredException extends RestException {
    private static final HttpStatus STATUS = HttpStatus.NETWORK_AUTHENTICATION_REQUIRED;

    public NetworkAuthenticationRequiredException(String logMessage) {
        super(STATUS, logMessage);
    }

    public NetworkAuthenticationRequiredException(ErrorMessage errorMessage, String logMessage) {
        super(STATUS, errorMessage, logMessage);
    }

    public NetworkAuthenticationRequiredException(String errorCode, String logMessage) {
        super(STATUS, errorCode, logMessage);
    }
}
