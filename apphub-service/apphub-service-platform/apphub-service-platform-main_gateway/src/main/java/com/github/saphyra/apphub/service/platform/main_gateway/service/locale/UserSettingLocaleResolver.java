package com.github.saphyra.apphub.service.platform.main_gateway.service.locale;

import com.github.saphyra.apphub.api.user.client.UserDataApiClient;
import com.github.saphyra.apphub.api.user.model.response.InternalAccessTokenResponse;
import com.github.saphyra.apphub.lib.common_util.CommonConfigProperties;
import com.github.saphyra.apphub.service.platform.main_gateway.service.AccessTokenQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpCookie;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

import java.util.Optional;
import java.util.UUID;

import static com.github.saphyra.apphub.lib.common_domain.Constants.ACCESS_TOKEN_COOKIE;

@RequiredArgsConstructor
@Slf4j
@Component
class UserSettingLocaleResolver {
    private final AccessTokenQueryService accessTokenQueryService;
    private final CommonConfigProperties commonConfigProperties;
    private final UserDataApiClient userDataApi;

    Optional<String> getLocale(MultiValueMap<String, HttpCookie> cookies) {
        Optional<UUID> userIdOptional = Optional.ofNullable(cookies.getFirst(ACCESS_TOKEN_COOKIE))
            .map(HttpCookie::getValue)
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
