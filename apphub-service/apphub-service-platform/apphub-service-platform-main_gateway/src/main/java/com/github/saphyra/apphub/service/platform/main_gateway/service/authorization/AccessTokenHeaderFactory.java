package com.github.saphyra.apphub.service.platform.main_gateway.service.authorization;

import com.github.saphyra.apphub.api.etc.user.model.login.InternalAccessTokenResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessToken;
import org.springframework.stereotype.Component;

@Component
@Deprecated(forRemoval = true) //TODO delete
class AccessTokenHeaderFactory {
    AccessToken create(InternalAccessTokenResponse accessTokenResponse) {
        return AccessToken.builder()
            .accessTokenId(accessTokenResponse.getAccessTokenId())
            .userId(accessTokenResponse.getUserId())
            .roles(accessTokenResponse.getRoles())
            .build();
    }
}
