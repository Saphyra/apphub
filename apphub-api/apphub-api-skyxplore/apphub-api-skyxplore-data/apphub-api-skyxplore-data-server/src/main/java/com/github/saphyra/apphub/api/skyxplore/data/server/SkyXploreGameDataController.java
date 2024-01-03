package com.github.saphyra.apphub.api.skyxplore.data.server;

import com.github.saphyra.apphub.api.skyxplore.response.game.citizen.CitizenStatsAndSkills;
import com.github.saphyra.apphub.lib.config.common.Endpoints;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

public interface SkyXploreGameDataController {
    @GetMapping(Endpoints.SKYXPLORE_GET_ITEM_DATA)
    Object getGameData(@PathVariable("dataId") String dataId);

    /**
     * Buildings can be built on given surfaceType
     */
    @GetMapping(Endpoints.SKYXPLORE_DATA_AVAILABLE_BUILDINGS)
    List<String> getAvailableBuildings(@PathVariable("surfaceType") String surfaceType);

    /**
     * SurfaceTypes the given surface can be terraformed to
     */
    @GetMapping(Endpoints.SKYXPLORE_DATA_TERRAFORMING_POSSIBILITIES)
    List<Object> getTerraformingPossibilities(@PathVariable("surfaceType") String surfaceType);

    @GetMapping(Endpoints.SKYXPLORE_DATA_CITIZEN_STATS_AND_SKILLS)
    CitizenStatsAndSkills getStatsAndSkills();
}
