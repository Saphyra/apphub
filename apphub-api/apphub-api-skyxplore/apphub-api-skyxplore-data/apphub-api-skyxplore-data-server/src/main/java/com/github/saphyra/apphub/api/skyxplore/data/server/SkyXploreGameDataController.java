package com.github.saphyra.apphub.api.skyxplore.data.server;

import com.github.saphyra.apphub.lib.config.Endpoints;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

public interface SkyXploreGameDataController {
    @GetMapping(Endpoints.SKYXPLORE_GET_ITEM_DATA)
    Object getGameData(@PathVariable("dataId") String dataId);
}
