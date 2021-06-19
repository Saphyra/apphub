package com.github.saphyra.apphub.lib.exception;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import org.springframework.http.HttpStatus;

import java.util.Map;

//Exception to be used for exceptional cases, without stack trace logged
public class NotLoggedException extends RestException {
    NotLoggedException(HttpStatus status, ErrorCode errorCode, String message) {
        super(status, errorCode, message);
    }

    NotLoggedException(HttpStatus status, ErrorCode errorCode, Map<String, String> params, String message) {
        super(status, errorCode, params, message);
    }
}
