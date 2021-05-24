package com.github.saphyra.apphub.service.platform.main_gateway.service.locale;

import com.github.saphyra.apphub.lib.common_util.CommonConfigProperties;
import com.github.saphyra.apphub.lib.common_util.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpCookie;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@Component
public class CookieLocaleResolver {
    private final CommonConfigProperties commonConfigProperties;

    public Optional<String> getLocale(MultiValueMap<String, HttpCookie> cookies) {
        return Optional.ofNullable(cookies.getFirst(Constants.LOCALE_COOKIE))
            .map(HttpCookie::getValue)
            .filter(locale -> commonConfigProperties.getSupportedLocales().contains(locale));
    }
}
