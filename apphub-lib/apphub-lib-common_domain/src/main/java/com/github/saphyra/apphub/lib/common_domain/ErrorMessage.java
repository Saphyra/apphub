package com.github.saphyra.apphub.lib.common_domain;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class ErrorMessage {
    public static ErrorMessage DEFAULT_ERROR_MESSAGE = new ErrorMessage(ErrorCode.GENERAL_ERROR);

    private final ErrorCode errorCode;

    private final Map<String, String> params;

    public ErrorMessage(ErrorCode errorCode) {
        this(errorCode, new HashMap<>());
    }

    public ErrorMessage(ErrorCode errorCode, Map<String, String> params) {
        this.errorCode = errorCode;
        this.params = params;
    }

    public ErrorMessage(ErrorCode errorCode, String paramKey, String paramValue) {
        this.errorCode = errorCode;
        Map<String, String> params = new HashMap<>();
        params.put(paramKey, paramValue);
        this.params = params;
    }

    @Override
    public String toString() {
        return errorCode + " - Params: " + params.toString();
    }
}
