package com.github.saphyra.apphub.lib.exception;

import com.github.saphyra.apphub.lib.common_domain.ErrorMessage;
import org.springframework.http.HttpStatus;

public class HttpVersionNotSupportedException extends RestException {
    private static final HttpStatus STATUS = HttpStatus.HTTP_VERSION_NOT_SUPPORTED;

    public HttpVersionNotSupportedException(String logMessage) {
        super(STATUS, logMessage);
    }

    public HttpVersionNotSupportedException(ErrorMessage errorMessage, String logMessage) {
        super(STATUS, errorMessage, logMessage);
    }

    public HttpVersionNotSupportedException(String errorCode, String logMessage) {
        super(STATUS, errorCode, logMessage);
    }
}
