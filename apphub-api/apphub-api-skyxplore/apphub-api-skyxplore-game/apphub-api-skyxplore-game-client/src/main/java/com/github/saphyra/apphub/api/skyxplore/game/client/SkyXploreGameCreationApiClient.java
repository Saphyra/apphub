package com.github.saphyra.apphub.api.skyxplore.game.client;

import com.github.saphyra.apphub.api.skyxplore.request.game_creation.SkyXploreLoadGameRequest;
import com.github.saphyra.apphub.api.skyxplore.request.game_creation.SkyXploreGameCreationRequest;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.config.common.Endpoints;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "skyxplore-game-creation", url = "${serviceUrls.skyxploreGame}")
public interface SkyXploreGameCreationApiClient {
    @PutMapping(Endpoints.SKYXPLORE_INTERNAL_CREATE_GAME)
    void createGame(@RequestBody SkyXploreGameCreationRequest request, @RequestHeader(Constants.LOCALE_HEADER) String locale);

    @PostMapping(Endpoints.SKYXPLORE_INTERNAL_LOAD_GAME)
    void loadGame(@RequestBody SkyXploreLoadGameRequest request, @RequestHeader(Constants.LOCALE_HEADER) String locale);
}
