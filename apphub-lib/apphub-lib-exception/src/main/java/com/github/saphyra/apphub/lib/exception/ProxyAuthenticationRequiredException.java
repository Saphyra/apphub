package com.github.saphyra.apphub.lib.exception;

import com.github.saphyra.apphub.lib.common_domain.ErrorMessage;
import org.springframework.http.HttpStatus;

public class ProxyAuthenticationRequiredException extends RestException {
    private static final HttpStatus STATUS = HttpStatus.PROXY_AUTHENTICATION_REQUIRED;

    public ProxyAuthenticationRequiredException(String logMessage) {
        super(STATUS, logMessage);
    }

    public ProxyAuthenticationRequiredException(ErrorMessage errorMessage, String logMessage) {
        super(STATUS, errorMessage, logMessage);
    }

    public ProxyAuthenticationRequiredException(String errorCode, String logMessage) {
        super(STATUS, errorCode, logMessage);
    }
}
