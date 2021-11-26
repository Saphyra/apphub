package com.github.saphyra.apphub.lib.exception;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import lombok.experimental.UtilityClass;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

@UtilityClass
public class ExceptionFactory {
    public RestException loggedException(HttpStatus status, ErrorCode errorCode, String message) {
        return loggedException(status, errorCode, new HashMap<>(), message);
    }

    public RestException loggedException(HttpStatus status, ErrorCode errorCode, Map<String, String> params, String message) {
        return new LoggedException(status, errorCode, params, message);
    }

    public RestException reportedException(String message) {
        return new ReportedException(message);
    }

    public RestException reportedException(HttpStatus status, String message) {
        return new ReportedException(status, message);
    }

    public RestException reportedException(HttpStatus status, ErrorCode errorCode, String message) {
        return reportedException(status, errorCode, message, null);
    }

    public RestException reportedException(HttpStatus status, ErrorCode errorCode, String message, Throwable cause) {
        return new ReportedException(status, errorCode, message, cause);
    }

    public RestException notLoggedException(HttpStatus status, String message) {
        return notLoggedException(status, ErrorCode.GENERAL_ERROR, new HashMap<>(), message);
    }


    public RestException notLoggedException(HttpStatus status, ErrorCode errorCode, String message) {
        return notLoggedException(status, errorCode, new HashMap<>(), message);
    }

    public RestException notLoggedException(HttpStatus status, ErrorCode errorCode, Map<String, String> params, String message) {
        return new NotLoggedException(status, errorCode, params, message);
    }

    private RestException notLoggedException(HttpStatus status, ErrorCode errorCode, Map<String, String> params, String message, Exception cause) {
        throw new NotLoggedException(status, errorCode, params, message, cause);
    }

    public RestException invalidParam(String field, String value) {
        return invalidParam(field, value, null);
    }

    public RestException invalidParam(String field, String value, Exception cause) {
        Map<String, String> params = new HashMap<>();
        params.put(field, value);
        return notLoggedException(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_PARAM, params, String.join(" ", field, value), cause);
    }

    public RestException invalidType(String message) {
        return notLoggedException(HttpStatus.UNPROCESSABLE_ENTITY, ErrorCode.INVALID_TYPE, message);
    }

    public RestException forbiddenOperation(String message) {
        return notLoggedException(HttpStatus.FORBIDDEN, ErrorCode.FORBIDDEN_OPERATION, message);
    }
}
