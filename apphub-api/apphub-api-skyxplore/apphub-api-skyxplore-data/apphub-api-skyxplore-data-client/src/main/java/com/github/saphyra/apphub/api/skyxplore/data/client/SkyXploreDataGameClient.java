package com.github.saphyra.apphub.api.skyxplore.data.client;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
import com.github.saphyra.apphub.lib.common_util.Constants;
import com.github.saphyra.apphub.lib.config.Endpoints;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@FeignClient("skyxplore-data-game")
public interface SkyXploreDataGameClient {
    @PostMapping(Endpoints.SKYXPLORE_INTERNAL_SAVE_GAME_DATA)
    void saveGameData(@RequestBody List<GameItem> items, @RequestHeader(Constants.LOCALE_HEADER) String locale);
}
