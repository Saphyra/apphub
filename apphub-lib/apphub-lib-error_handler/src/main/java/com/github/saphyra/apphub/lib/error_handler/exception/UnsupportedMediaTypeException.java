package com.github.saphyra.apphub.lib.error_handler.exception;

import com.github.saphyra.apphub.lib.error_handler.domain.ErrorMessage;
import org.springframework.http.HttpStatus;

public class UnsupportedMediaTypeException extends RestException {
    private static final HttpStatus STATUS = HttpStatus.UNSUPPORTED_MEDIA_TYPE;

    public UnsupportedMediaTypeException(String logMessage) {
        super(STATUS, logMessage);
    }

    public UnsupportedMediaTypeException(ErrorMessage errorMessage, String logMessage) {
        super(STATUS, errorMessage, logMessage);
    }

    public UnsupportedMediaTypeException(String errorCode, String logMessage) {
        super(STATUS, errorCode, logMessage);
    }
}
