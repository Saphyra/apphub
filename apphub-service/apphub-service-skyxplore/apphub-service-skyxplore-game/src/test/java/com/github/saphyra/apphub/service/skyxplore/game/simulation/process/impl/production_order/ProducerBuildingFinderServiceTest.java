package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.production_order;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SurfaceType;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.production.ProductionBuildingData;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.production.ProductionBuildingService;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.production.ProductionData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building.Building;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building.Buildings;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.Surface;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.Surfaces;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.building.BuildingCapacityCalculator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ProducerBuildingFinderServiceTest {
    private static final UUID LOCATION = UUID.randomUUID();
    private static final String RESOURCE_DATA_ID = "resource-data-id";
    private static final String PRODUCER_BUILDING_DATA_ID = "producer-building-data-id";
    private static final String IRRELEVANT_BUILDING_DATA_ID = "irrelevant-building-data-id";
    private static final String OCCUPIED_BUILDING_DATA_ID = "occupied-building-data-id";
    private static final String NOT_PRODUCTION_BUILDING_DATA_ID = "not-production-building-data-id";
    private static final UUID SURFACE_ID = UUID.randomUUID();

    @Mock
    private ProductionBuildingService productionBuildingService;

    @Mock
    private BuildingCapacityCalculator buildingCapacityCalculator;

    @InjectMocks
    private ProducerBuildingFinderService underTest;

    @Mock
    private GameData gameData;

    @Mock
    private Buildings buildings;

    @Mock
    private Building producerBuilding;

    @Mock
    private Building notProductionBuilding;

    @Mock
    private Building irrelevantBuilding;

    @Mock
    private Building occupiedBuilding;

    @Mock
    private ProductionBuildingData producerBuildingData;

    @Mock
    private ProductionBuildingData irrelevantBuildingData;

    @Mock
    private ProductionBuildingData occupiedBuildingData;

    @Mock
    private ProductionData producerProductionData;

    @Mock
    private Surfaces surfaces;

    @Mock
    private Surface surface;

    @Test
    void findProducerBuildingDataId() {
        given(gameData.getBuildings()).willReturn(buildings);
        given(buildings.getByLocation(LOCATION)).willReturn(List.of(irrelevantBuilding, occupiedBuilding, notProductionBuilding, producerBuilding));

        given(producerBuilding.getDataId()).willReturn(PRODUCER_BUILDING_DATA_ID);
        given(irrelevantBuilding.getDataId()).willReturn(IRRELEVANT_BUILDING_DATA_ID);
        given(occupiedBuilding.getDataId()).willReturn(OCCUPIED_BUILDING_DATA_ID);
        given(notProductionBuilding.getDataId()).willReturn(NOT_PRODUCTION_BUILDING_DATA_ID);

        given(productionBuildingService.get(PRODUCER_BUILDING_DATA_ID)).willReturn(producerBuildingData);
        given(productionBuildingService.get(IRRELEVANT_BUILDING_DATA_ID)).willReturn(irrelevantBuildingData);
        given(productionBuildingService.get(OCCUPIED_BUILDING_DATA_ID)).willReturn(occupiedBuildingData);
        given(productionBuildingService.get(NOT_PRODUCTION_BUILDING_DATA_ID)).willReturn(null);

        given(producerBuildingData.getGives()).willReturn(Map.of(RESOURCE_DATA_ID, producerProductionData));
        given(occupiedBuildingData.getGives()).willReturn(Map.of(RESOURCE_DATA_ID, producerProductionData));
        given(irrelevantBuildingData.getGives()).willReturn(Collections.emptyMap());

        given(gameData.getSurfaces()).willReturn(surfaces);
        given(producerBuilding.getSurfaceId()).willReturn(SURFACE_ID);
        given(occupiedBuilding.getSurfaceId()).willReturn(SURFACE_ID);
        given(surfaces.findBySurfaceId(SURFACE_ID)).willReturn(surface);
        given(surface.getSurfaceType()).willReturn(SurfaceType.CONCRETE);
        given(producerProductionData.getPlaced()).willReturn(List.of(SurfaceType.CONCRETE));

        given(buildingCapacityCalculator.calculateCapacity(gameData, occupiedBuilding)).willReturn(0);
        given(buildingCapacityCalculator.calculateCapacity(gameData, producerBuilding)).willReturn(1);

        Optional<String> result = underTest.findProducerBuildingDataId(gameData, LOCATION, RESOURCE_DATA_ID);

        assertThat(result).contains(PRODUCER_BUILDING_DATA_ID);
    }
}