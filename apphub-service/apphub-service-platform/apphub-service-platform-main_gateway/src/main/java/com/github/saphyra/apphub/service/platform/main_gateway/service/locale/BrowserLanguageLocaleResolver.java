package com.github.saphyra.apphub.service.platform.main_gateway.service.locale;

import com.github.saphyra.apphub.lib.common_util.Constants;
import com.github.saphyra.apphub.lib.config.common.CommonConfigProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
class BrowserLanguageLocaleResolver {
    private final CommonConfigProperties commonConfigProperties;

    Optional<String> getLocale(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(Constants.BROWSER_LANGUAGE_HEADER))
            .filter(locale -> commonConfigProperties.getSupportedLocales().contains(locale));
    }
}
