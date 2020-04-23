package com.github.saphyra.apphub.service.platform.main_gateway.service.locale;

import com.github.saphyra.apphub.lib.common_util.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

import static org.apache.commons.lang.StringUtils.isBlank;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class BrowserLanguageLocaleResolver {
    private final SupportedLocalesConfiguration supportedLocalesConfiguration;

    Optional<String> getLocale(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(Constants.BROWSER_LANGUAGE_HEADER))
            .filter(headerValue -> !isBlank(headerValue))
            .filter(supportedLocalesConfiguration::isSupported);
    }
}
