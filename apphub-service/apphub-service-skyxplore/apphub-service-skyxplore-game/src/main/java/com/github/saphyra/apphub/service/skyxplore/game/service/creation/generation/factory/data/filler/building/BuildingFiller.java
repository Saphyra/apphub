package com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory.data.filler.building;

import com.github.saphyra.apphub.lib.data.AbstractDataService;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SurfaceType;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.BuildingData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building.Building;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.Surface;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building.BuildingFactory;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.isNull;

@Component
@Slf4j
public class BuildingFiller {
    private static final List<BuildingData> EXCAVATOR_BUILDINGS = Arrays.asList(
        new ExcavatorBuilding(SurfaceType.ORE_FIELD),
        new ExcavatorBuilding(SurfaceType.MOUNTAIN)
    );

    private final List<BuildingData> defaultBuildings;
    private final BuildingFactory buildingFactory;
    private final EmptySurfaceProvider emptySurfaceProvider;

    @Builder
    public BuildingFiller(
        List<AbstractDataService<?, ? extends BuildingData>> buildingDataServices,
        BuildingFactory buildingFactory,
        EmptySurfaceProvider emptySurfaceProvider
    ) {
        defaultBuildings = Stream.concat(
            buildingDataServices.stream()
                .flatMap(abstractDataService -> abstractDataService.values()
                    .stream()
                    .filter(BuildingData::isDefaultBuilding)
                ),
            EXCAVATOR_BUILDINGS.stream()
        ).collect(Collectors.toList());
        this.buildingFactory = buildingFactory;
        this.emptySurfaceProvider = emptySurfaceProvider;
    }

    public void fillBuildings(GameData gameData) {
        gameData.getPlanets()
            .values()
            .stream()
            .filter(planet -> !isNull(planet.getOwner()))
            .forEach(planet -> placeDefaultBuildings(planet, gameData));
    }

    private void placeDefaultBuildings(Planet planet, GameData gameData) {
        defaultBuildings.forEach(buildingData -> placeBuilding(planet.getPlanetId(), buildingData, gameData.getSurfaces().getByPlanetId(planet.getPlanetId()), gameData));
    }

    private void placeBuilding(UUID location, BuildingData buildingData, Collection<Surface> surfaces, GameData gameData) {
        Surface surface = emptySurfaceProvider.getEmptySurfaceForType(buildingData.getPrimarySurfaceType(), surfaces, gameData);

        Building building = buildingFactory.create(buildingData.getId(), location, surface.getSurfaceId());
        gameData.getBuildings()
            .add(building);
    }
}
