package com.github.saphyra.apphub.service.platform.message_sender.session;

import com.github.saphyra.apphub.lib.common_util.Constants;
import com.github.saphyra.apphub.lib.exception.RestException;
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
            .orElseThrow(() -> RestException.createNonTranslated(HttpStatus.BAD_REQUEST, "Locale cookie not found."));
    }
}
