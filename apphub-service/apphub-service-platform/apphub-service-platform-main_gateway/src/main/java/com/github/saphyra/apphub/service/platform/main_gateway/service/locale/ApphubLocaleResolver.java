package com.github.saphyra.apphub.service.platform.main_gateway.service.locale;

import com.github.saphyra.apphub.lib.common_util.CommonConfigProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class ApphubLocaleResolver {
    private final BrowserLanguageLocaleResolver browserLanguageLocaleResolver;
    private final CommonConfigProperties commonConfigProperties;
    private final CookieLocaleResolver cookieLocaleResolver;
    private final UserSettingLocaleResolver userSettingLocaleResolver;

    public String getLocale(HttpServletRequest request) {
        Optional<String> locale = userSettingLocaleResolver.getLocale(request);

        if (!locale.isPresent()) {
            locale = cookieLocaleResolver.getLocale(request);
        }

        if (!locale.isPresent()) {
            locale = browserLanguageLocaleResolver.getLocale(request);
        }

        return locale.orElseGet(commonConfigProperties::getDefaultLocale);
    }
}
