package com.github.saphyra.apphub.api.skyxplore.data.server;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
import com.github.saphyra.apphub.lib.config.Endpoints;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface SkyXploreDataGameController {
    @PostMapping(Endpoints.INTERNAL_SKYXPLORE_SAVE_GAME_DATA)
    void saveGameData(@RequestBody List<GameItem> items);
}
