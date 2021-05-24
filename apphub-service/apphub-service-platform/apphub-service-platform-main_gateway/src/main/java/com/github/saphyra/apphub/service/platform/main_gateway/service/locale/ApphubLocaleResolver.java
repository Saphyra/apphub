package com.github.saphyra.apphub.service.platform.main_gateway.service.locale;

import com.github.saphyra.apphub.lib.common_util.CommonConfigProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class ApphubLocaleResolver {
    private final BrowserLanguageLocaleResolver browserLanguageLocaleResolver;
    private final CommonConfigProperties commonConfigProperties;
    private final CookieLocaleResolver cookieLocaleResolver;
    private final UserSettingLocaleResolver userSettingLocaleResolver;

    public String getLocale(HttpHeaders headers, MultiValueMap<String, HttpCookie> cookies) {
        Optional<String> locale = userSettingLocaleResolver.getLocale(cookies);

        if (!locale.isPresent()) {
            locale = cookieLocaleResolver.getLocale(cookies);
        }

        if (!locale.isPresent()) {
            locale = browserLanguageLocaleResolver.getLocale(headers);
        }

        return locale.orElseGet(commonConfigProperties::getDefaultLocale);
    }
}
