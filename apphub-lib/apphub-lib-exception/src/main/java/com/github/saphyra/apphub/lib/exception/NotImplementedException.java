package com.github.saphyra.apphub.lib.exception;

import com.github.saphyra.apphub.lib.common_domain.ErrorMessage;
import org.springframework.http.HttpStatus;

public class NotImplementedException extends RestException {
    private static final HttpStatus STATUS = HttpStatus.NOT_IMPLEMENTED;

    public NotImplementedException() {
        this("");
    }

    public NotImplementedException(String logMessage) {
        super(STATUS, logMessage);
    }

    public NotImplementedException(ErrorMessage errorMessage, String logMessage) {
        super(STATUS, errorMessage, logMessage);
    }

    public NotImplementedException(String errorCode, String logMessage) {
        super(STATUS, errorCode, logMessage);
    }
}
