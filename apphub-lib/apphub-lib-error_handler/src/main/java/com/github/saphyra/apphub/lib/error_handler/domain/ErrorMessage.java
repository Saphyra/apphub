package com.github.saphyra.apphub.lib.error_handler.domain;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class ErrorMessage {
    private final String errorCode;

    private final Map<String, String> params;

    public ErrorMessage(String errorCode) {
        this(errorCode, new HashMap<>());
    }

    public ErrorMessage(String errorCode, Map<String, String> params) {
        this.errorCode = errorCode;
        this.params = params;
    }

    @Override
    public String toString() {
        return errorCode + " - Params: " + params.toString();
    }
}
