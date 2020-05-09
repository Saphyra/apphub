package com.github.saphyra.apphub.service.platform.main_gateway.service.authentication;

import com.github.saphyra.apphub.api.user.model.response.InternalAccessTokenResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import org.springframework.stereotype.Component;

@Component
class AccessTokenHeaderFactory {
    AccessTokenHeader create(InternalAccessTokenResponse accessTokenResponse) {
        return AccessTokenHeader.builder()
            .accessTokenId(accessTokenResponse.getAccessTokenId())
            .userId(accessTokenResponse.getUserId())
            .build();
    }
}
