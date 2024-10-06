package com.github.saphyra.apphub.api.skyxplore.game.server;

import com.github.saphyra.apphub.api.skyxplore.request.game_creation.SkyXploreGameCreationRequest;
import com.github.saphyra.apphub.api.skyxplore.request.game_creation.SkyXploreLoadGameRequest;
import com.github.saphyra.apphub.lib.config.common.endpoints.skyxplore.SkyXploreGameEndpoints;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

public interface SkyXploreGameCreationController {
    @PutMapping(SkyXploreGameEndpoints.SKYXPLORE_INTERNAL_CREATE_GAME)
    UUID createGame(@RequestBody SkyXploreGameCreationRequest request) throws InterruptedException;

    @PostMapping(SkyXploreGameEndpoints.SKYXPLORE_INTERNAL_LOAD_GAME)
    void loadGame(@RequestBody SkyXploreLoadGameRequest request);
}
