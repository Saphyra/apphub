package com.github.saphyra.apphub.api.skyxplore.data.server;

import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_util.Constants;
import com.github.saphyra.apphub.lib.config.Endpoints;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

public interface SkyXploreCharacterController {
    @GetMapping(Endpoints.IS_SKYXPLORE_CHARACTER_EXISTS)
    boolean isCharacterExistsForUser(@RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);
}
