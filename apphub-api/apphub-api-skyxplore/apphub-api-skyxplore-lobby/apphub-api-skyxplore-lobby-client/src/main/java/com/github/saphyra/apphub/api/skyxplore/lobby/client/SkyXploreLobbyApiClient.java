package com.github.saphyra.apphub.api.skyxplore.lobby.client;

import com.github.saphyra.apphub.lib.common_util.Constants;
import com.github.saphyra.apphub.lib.config.Endpoints;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient("skyxplore-lobby")
public interface SkyXploreLobbyApiClient {
    @PutMapping(Endpoints.SKYXPLORE_CREATE_LOBBY)
    void createLobbyIfNotExists(@RequestHeader(Constants.ACCESS_TOKEN_HEADER) String accessTokenHeader, @RequestHeader(Constants.LOCALE_HEADER) String locale);
}
