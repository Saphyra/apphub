package com.github.saphyra.apphub.service.skyxplore.game.creation.service.factory.building;

import com.github.saphyra.apphub.lib.data.AbstractDataService;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SurfaceType;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.BuildingData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Building;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Surface;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@Slf4j
public class BuildingPlacementService {
    private static final List<BuildingData> EXCAVATOR_BUILDINGS = Arrays.asList(
        new ExcavatorBuilding(SurfaceType.ORE_FIELD),
        new ExcavatorBuilding(SurfaceType.MOUNTAIN)
    );

    private final List<BuildingData> defaultBuildings;
    private final BuildingFactory buildingFactory;
    private final EmptySurfaceProvider emptySurfaceProvider;

    public BuildingPlacementService(
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

    public void placeDefaultBuildings(Planet planet) {
        defaultBuildings.forEach(buildingData -> placeBuilding(buildingData, planet.getSurfaces().values()));
    }

    private void placeBuilding(BuildingData buildingData, Collection<Surface> surfaces) {
        Surface surface = emptySurfaceProvider.getEmptySurfaceForType(buildingData.getPrimarySurfaceType(), surfaces);

        Building building = buildingFactory.create(buildingData.getId(), surface.getSurfaceId());
        surface.setBuilding(building);
    }

    private static class ExcavatorBuilding extends BuildingData {
        private static final String EXCAVATOR_ID = "excavator";

        private final SurfaceType surfaceType;

        private ExcavatorBuilding(SurfaceType surfaceType) {
            setId(EXCAVATOR_ID);
            this.surfaceType = surfaceType;
        }

        @Override
        public List<SurfaceType> getPlaceableSurfaceTypes() {
            return Arrays.asList(surfaceType);
        }

        @Override
        public SurfaceType getPrimarySurfaceType() {
            return surfaceType;
        }
    }
}
