package com.github.saphyra.apphub.service.platform.main_gateway.service.locale;

import com.github.saphyra.apphub.lib.common_util.Constants;
import com.github.saphyra.util.CookieUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

import static org.apache.commons.lang.StringUtils.isBlank;

@RequiredArgsConstructor
@Slf4j
@Component
//TODO unit test
class CookieLocaleResolver {
    private final CookieUtil cookieUtil;
    private final SupportedLocalesConfiguration supportedLocalesConfiguration;

    Optional<String> getCookie(HttpServletRequest request) {
        return cookieUtil.getCookie(request, Constants.LOCALE_COOKIE)
            .filter(cookieValue -> !isBlank(cookieValue))
            .filter(supportedLocalesConfiguration::isSupported);
    }
}
