package com.github.saphyra.apphub.api.skyxplore.lobby.client;

import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.config.common.endpoints.skyxplore.SkyXploreLobbyEndpoints;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.UUID;

@FeignClient(name = "skyxplore-lobby", url = "${serviceUrls.skyxploreLobby}")
public interface SkyXploreLobbyApiClient {
    @PostMapping(SkyXploreLobbyEndpoints.SKYXPLORE_INTERNAL_GAME_LOADED)
    void gameLoaded(@PathVariable("gameId") UUID gameId, @RequestHeader(Constants.LOCALE_HEADER) String locale);
}
