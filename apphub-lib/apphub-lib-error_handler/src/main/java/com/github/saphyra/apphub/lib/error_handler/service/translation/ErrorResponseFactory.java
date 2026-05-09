package com.github.saphyra.apphub.lib.error_handler.service.translation;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_domain.ErrorResponse;
import com.github.saphyra.apphub.lib.common_domain.ErrorResponseWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Slf4j
@Component
public class ErrorResponseFactory {
    public ErrorResponseWrapper create(HttpStatus status, ErrorCode errorCode) {
        return create(status, errorCode, new HashMap<>());
    }

    public ErrorResponseWrapper create(HttpStatus httpStatus, ErrorCode errorCode, Map<String, String> params) {
        ErrorResponse errorResponse = ErrorResponse.builder()
            .errorCode(errorCode)
            .params(params)
            .build();
        return new ErrorResponseWrapper(errorResponse, httpStatus);
    }
}