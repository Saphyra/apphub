package com.github.saphyra.apphub.service.platform.main_gateway.service.locale;

import com.github.saphyra.apphub.lib.common_util.CommonConfigProperties;
import com.github.saphyra.apphub.lib.common_util.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
class BrowserLanguageLocaleResolver {
    private final CommonConfigProperties commonConfigProperties;

    Optional<String> getLocale(HttpHeaders headers) {
        return Optional.ofNullable(headers.getFirst(Constants.BROWSER_LANGUAGE_HEADER))
            .filter(locale -> commonConfigProperties.getSupportedLocales().contains(locale));
    }
}
