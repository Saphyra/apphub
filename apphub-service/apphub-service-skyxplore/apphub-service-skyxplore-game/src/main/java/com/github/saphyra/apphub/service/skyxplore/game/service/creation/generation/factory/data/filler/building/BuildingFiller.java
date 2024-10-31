package com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory.data.filler.building;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SurfaceType;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameConstants;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building_module.BuildingModuleFactory;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction_area.ConstructionArea;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction_area.ConstructionAreaFactory;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.Surface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class BuildingFiller {
    private static final Collection<String> HQ_MODULES = List.of(
        "sawmill",
        "foundry",
        "hamster_wheel",
        "hq_barracks",
        "hq_canteen",
        "hq_storage",
        "hq_extraction"
    );

    private final EmptySurfaceProvider emptySurfaceProvider;
    private final ConstructionAreaFactory constructionAreaFactory;
    private final BuildingModuleFactory buildingModuleFactory;

    public void fillBuildings(GameData gameData) {
        gameData.getPlanets()
            .values()
            .stream()
            .filter(planet -> !isNull(planet.getOwner()))
            .forEach(planet -> placeHeadquarters(planet, gameData));
    }

    private void placeHeadquarters(Planet planet, GameData gameData) {
        Surface surface = emptySurfaceProvider.getEmptySurfaceForType(
            SurfaceType.DESERT,
            gameData.getSurfaces().getByPlanetId(planet.getPlanetId()),
            gameData
        );

        ConstructionArea constructionArea = constructionAreaFactory.create(planet.getPlanetId(), surface.getSurfaceId(), GameConstants.DATA_ID_HEADQUARTERS);
        gameData.getConstructionAreas()
            .add(constructionArea);

        HQ_MODULES.stream()
            .map(dataId -> buildingModuleFactory.create(planet.getPlanetId(), constructionArea.getConstructionAreaId(), dataId))
            .forEach(module -> gameData.getBuildingModules().add(module));
    }
}
