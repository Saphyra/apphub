package com.github.saphyra.apphub.service.platform.main_gateway.service.locale;

import com.github.saphyra.apphub.api.user.client.UserDataApiClient;
import com.github.saphyra.apphub.api.user.model.response.InternalAccessTokenResponse;
import com.github.saphyra.apphub.lib.config.CommonConfigProperties;
import com.github.saphyra.apphub.service.platform.main_gateway.service.AccessTokenQueryService;
import com.github.saphyra.util.CookieUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.util.UUID;

import static com.github.saphyra.apphub.lib.common_util.Constants.ACCESS_TOKEN_COOKIE;

@RequiredArgsConstructor
@Slf4j
@Component
class UserSettingLocaleResolver {
    private final AccessTokenQueryService accessTokenQueryService;
    private final CommonConfigProperties commonConfigProperties;
    private final CookieUtil cookieUtil;
    private final UserDataApiClient userDataApi;

    Optional<String> getLocale(HttpServletRequest request) {
        Optional<UUID> userIdOptional = cookieUtil.getCookie(request, ACCESS_TOKEN_COOKIE)
            .flatMap(accessTokenQueryService::getAccessToken)
            .map(InternalAccessTokenResponse::getUserId);
        if (userIdOptional.isPresent()) {
            try {
                return Optional.of(userDataApi.getLanguage(userIdOptional.get(), commonConfigProperties.getDefaultLocale()));
            } catch (Exception e) {
                log.warn("Error occurred during userLanguage query", e);
            }
        }
        return Optional.empty();
    }
}
