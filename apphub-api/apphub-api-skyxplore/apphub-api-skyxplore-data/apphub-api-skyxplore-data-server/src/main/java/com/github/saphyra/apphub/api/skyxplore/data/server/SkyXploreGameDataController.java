package com.github.saphyra.apphub.api.skyxplore.data.server;

import com.github.saphyra.apphub.lib.config.Endpoints;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

public interface SkyXploreGameDataController {
    @GetMapping(Endpoints.SKYXPLORE_GET_ITEM_DATA)
    Object getGameData(@PathVariable("dataId") String dataId);

    @GetMapping(Endpoints.SKYXPLORE_DATA_AVAILABLE_BUILDINGS)
    List<String> getAvailableBuildings(@PathVariable("surfaceType") String surfaceType);

    @GetMapping(Endpoints.SKYXPLORE_DATA_TERRAFORMING_POSSIBILITIES)
    List<String> getTerraformingPossibilities(@PathVariable("surfaceType") String surfaceType);
}
