package com.github.saphyra.apphub.api.skyxplore.data.server;

import com.github.saphyra.apphub.api.skyxplore.response.game.citizen.CitizenStatsAndSkills;
import com.github.saphyra.apphub.lib.config.common.endpoints.skyxplore.SkyXploreDataEndpoints;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

public interface SkyXploreGameDataController {
    @GetMapping(SkyXploreDataEndpoints.SKYXPLORE_GET_ITEM_DATA)
    Object getGameData(@PathVariable("dataId") String dataId);

    /**
     * Buildings can be built on given surfaceType
     */
    @Deprecated(forRemoval = true)
    @GetMapping(SkyXploreDataEndpoints.SKYXPLORE_DATA_AVAILABLE_BUILDINGS)
    List<String> getAvailableBuildings(@PathVariable("surfaceType") String surfaceType);

    /**
     * @return TerraformingPossibilities
     */
    @GetMapping(SkyXploreDataEndpoints.SKYXPLORE_DATA_TERRAFORMING_POSSIBILITIES)
    List<Object> getTerraformingPossibilities(@PathVariable("surfaceType") String surfaceType);

    /**
     * @return ConstructionAreaData
     */
    //TODO API test
    @GetMapping(SkyXploreDataEndpoints.SKYXPLORE_DATA_CONSTRUCTION_AREAS)
    List<Object> getAvailableConstructionAreas(@PathVariable("surfaceType") String surfaceType);

    @GetMapping(SkyXploreDataEndpoints.SKYXPLORE_DATA_CITIZEN_STATS_AND_SKILLS)
    CitizenStatsAndSkills getStatsAndSkills();

    @GetMapping(SkyXploreDataEndpoints.SKYXPLORE_DATA_RESOURCES)
    List<String> getResourceDataIds();
}
