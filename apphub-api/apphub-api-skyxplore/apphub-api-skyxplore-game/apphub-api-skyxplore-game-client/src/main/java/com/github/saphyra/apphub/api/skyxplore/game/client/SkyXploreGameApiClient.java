package com.github.saphyra.apphub.api.skyxplore.game.client;

import com.github.saphyra.apphub.lib.common_util.Constants;
import com.github.saphyra.apphub.lib.config.Endpoints;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient("skyxplore-game")
public interface SkyXploreGameApiClient {
    @GetMapping(Endpoints.INTERNAL_SKYXPLORE_IS_USER_IN_GAME)
    boolean isUserInGame(@RequestHeader(Constants.ACCESS_TOKEN_HEADER) String accessTokenHeader, @RequestHeader(Constants.LOCALE_HEADER) String locale);
}
