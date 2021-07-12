package com.github.saphyra.apphub.api.skyxplore.game.server;

import com.github.saphyra.apphub.api.skyxplore.request.game_creation.SkyXploreGameCreationRequest;
import com.github.saphyra.apphub.api.skyxplore.request.game_creation.SkyXploreLoadGameRequest;
import com.github.saphyra.apphub.lib.config.Endpoints;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface SkyXploreGameCreationController {
    @PutMapping(Endpoints.SKYXPLORE_INTERNAL_CREATE_GAME)
    void createGame(@RequestBody SkyXploreGameCreationRequest request) throws InterruptedException;

    @PostMapping(Endpoints.SKYXPLORE_INTERNAL_LOAD_GAME)
    void loadGame(@RequestBody SkyXploreLoadGameRequest request);
}
