package com.github.saphyra.apphub.lib.exception;

import com.github.saphyra.apphub.lib.common_domain.ErrorMessage;
import org.springframework.http.HttpStatus;

public class InternalServerErrorException extends RestException {
    private static final HttpStatus STATUS = HttpStatus.INTERNAL_SERVER_ERROR;

    public InternalServerErrorException(String logMessage) {
        super(STATUS, logMessage);
    }

    public InternalServerErrorException(ErrorMessage errorMessage, String logMessage) {
        super(STATUS, errorMessage, logMessage);
    }

    public InternalServerErrorException(String errorCode, String logMessage) {
        super(STATUS, errorCode, logMessage);
    }
}
