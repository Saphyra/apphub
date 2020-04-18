package com.github.saphyra.apphub.lib.error_handler.exception;

import com.github.saphyra.apphub.lib.error_handler.domain.ErrorMessage;
import org.springframework.http.HttpStatus;

public class LoopDetectedException extends RestException {
    private static final HttpStatus STATUS = HttpStatus.LOOP_DETECTED;

    public LoopDetectedException(String logMessage) {
        super(STATUS, logMessage);
    }

    public LoopDetectedException(ErrorMessage errorMessage, String logMessage) {
        super(STATUS, errorMessage, logMessage);
    }

    public LoopDetectedException(String errorCode, String logMessage) {
        super(STATUS, errorCode, logMessage);
    }
}
