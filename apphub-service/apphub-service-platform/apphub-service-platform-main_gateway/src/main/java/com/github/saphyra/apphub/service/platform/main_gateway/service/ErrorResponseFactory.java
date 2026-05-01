package com.github.saphyra.apphub.service.platform.main_gateway.service;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_domain.ErrorResponse;
import com.github.saphyra.apphub.lib.common_domain.ErrorResponseWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Map;

@RequiredArgsConstructor
@Slf4j
@Component
public class ErrorResponseFactory {
    public ErrorResponseWrapper create(HttpStatus httpStatus, ErrorCode errorCode, Map<String, String> params) {
        ErrorResponse errorResponse = ErrorResponse.builder()
            .errorCode(errorCode)
            .params(params)
            .build();
        return new ErrorResponseWrapper(errorResponse, httpStatus);
    }
}