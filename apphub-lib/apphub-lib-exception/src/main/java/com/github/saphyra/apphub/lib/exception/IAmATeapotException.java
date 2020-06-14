package com.github.saphyra.apphub.lib.exception;

import com.github.saphyra.apphub.lib.common_domain.ErrorMessage;
import org.springframework.http.HttpStatus;

public class IAmATeapotException extends RestException {
    private static final HttpStatus STATUS = HttpStatus.I_AM_A_TEAPOT;

    public IAmATeapotException(String logMessage) {
        super(STATUS, logMessage);
    }

    public IAmATeapotException(ErrorMessage errorMessage, String logMessage) {
        super(STATUS, errorMessage, logMessage);
    }

    public IAmATeapotException(String errorCode, String logMessage) {
        super(STATUS, errorCode, logMessage);
    }
}
