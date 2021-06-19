package com.github.saphyra.apphub.lib.exception;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import org.springframework.http.HttpStatus;

import java.util.Map;

//General exception with logged stack trace
public class LoggedException extends RestException {
    LoggedException(HttpStatus status, ErrorCode errorCode, String message) {
        super(status, errorCode, message);
    }

    LoggedException(HttpStatus status, ErrorCode errorCode, Map<String, String> params, String message) {
        super(status, errorCode, params, message);
    }
}
