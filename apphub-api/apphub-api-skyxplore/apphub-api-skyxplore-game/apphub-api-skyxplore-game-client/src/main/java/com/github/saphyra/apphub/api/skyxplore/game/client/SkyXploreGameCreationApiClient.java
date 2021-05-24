package com.github.saphyra.apphub.api.skyxplore.game.client;

import com.github.saphyra.apphub.api.skyxplore.request.game_creation.SkyXploreGameCreationRequest;
import com.github.saphyra.apphub.lib.common_util.Constants;
import com.github.saphyra.apphub.lib.config.Endpoints;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient("skyxplore-game-creation")
public interface SkyXploreGameCreationApiClient {
    @PutMapping(Endpoints.SKYXPLORE_INTERNAL_CREATE_GAME)
    void createGame(@RequestBody SkyXploreGameCreationRequest request, @RequestHeader(Constants.LOCALE_HEADER) String locale);
}
