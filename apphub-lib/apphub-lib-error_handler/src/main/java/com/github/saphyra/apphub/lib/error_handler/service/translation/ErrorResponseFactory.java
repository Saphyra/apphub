package com.github.saphyra.apphub.lib.error_handler.service.translation;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_domain.ErrorResponse;
import com.github.saphyra.apphub.lib.common_domain.ErrorResponseWrapper;
import com.github.saphyra.apphub.lib.web_utils.LocaleProvider;
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
    private final LocaleProvider localeProvider;
    private final LocalizedMessageProvider localizedMessageProvider;

    public ErrorResponseWrapper create(HttpStatus httpStatus, ErrorCode errorCode) {
        return create(httpStatus, errorCode, new HashMap<>());
    }

    public ErrorResponseWrapper create(HttpStatus httpStatus, ErrorCode errorCode, Map<String, String> params) {
        return create(localeProvider.getOrDefault(), httpStatus, errorCode, params);
    }

    public ErrorResponseWrapper create(String locale, HttpStatus status, ErrorCode errorCode) {
        return create(locale, status, errorCode, new HashMap<>());
    }

    public ErrorResponseWrapper create(String locale, HttpStatus httpStatus, ErrorCode errorCode, Map<String, String> params) {
        String localizedMessage = localizedMessageProvider.getLocalizedMessage(locale, errorCode, params);

        ErrorResponse errorResponse = ErrorResponse.builder()
            .errorCode(errorCode)
            .localizedMessage(localizedMessage)
            .params(params)
            .build();
        return new ErrorResponseWrapper(errorResponse, httpStatus);
    }
}