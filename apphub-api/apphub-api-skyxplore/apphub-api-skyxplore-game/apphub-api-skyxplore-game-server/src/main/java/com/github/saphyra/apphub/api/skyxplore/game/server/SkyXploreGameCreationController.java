package com.github.saphyra.apphub.api.skyxplore.game.server;

import com.github.saphyra.apphub.api.skyxplore.request.game_creation.SkyXploreGameCreationRequest;
import com.github.saphyra.apphub.lib.config.Endpoints;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface SkyXploreGameCreationController {
    @PutMapping(Endpoints.INTERNAL_SKYXPLORE_CREATE_GAME)
    void createGame(@RequestBody SkyXploreGameCreationRequest request) throws InterruptedException;
}
