package com.github.saphyra.apphub.service.platform.message_sender.session;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Component
@Slf4j
class WsSessionLocaleProvider {
    String getLocale(Map<String, String> cookies) {
        return Optional.ofNullable(cookies.get(Constants.LOCALE_COOKIE))
            .orElseThrow(() -> ExceptionFactory.loggedException(HttpStatus.BAD_REQUEST, ErrorCode.LOCALE_NOT_FOUND, "Locale cookie not found."));
    }
}
