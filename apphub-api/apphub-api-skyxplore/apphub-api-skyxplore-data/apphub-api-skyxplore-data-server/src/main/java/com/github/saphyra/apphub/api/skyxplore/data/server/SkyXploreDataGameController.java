package com.github.saphyra.apphub.api.skyxplore.data.server;

import com.github.saphyra.apphub.lib.config.Endpoints;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface SkyXploreDataGameController {
    @PostMapping(Endpoints.SKYXPLORE_INTERNAL_SAVE_GAME_DATA)
    void saveGameData(@RequestBody List<Object> items);
}
