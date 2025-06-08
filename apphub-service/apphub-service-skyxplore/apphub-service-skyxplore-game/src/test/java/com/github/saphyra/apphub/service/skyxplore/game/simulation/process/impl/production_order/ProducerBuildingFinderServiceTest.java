package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.production_order;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.production.ProducerBuildingModule;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.production.Production;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.production.ProductionBuildingModuleService;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building_module.BuildingModule;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building_module.BuildingModules;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.building.BuildingCapacityCalculator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ProducerBuildingFinderServiceTest {
    private static final UUID LOCATION = UUID.randomUUID();
    private static final String INCOMPATIBLE_DATA_ID = "incompatible-data-id";
    private static final String DATA_ID = "data-id";
    private static final String NOT_PRODUCER_DATA_ID = "not-producer-data-id";
    private static final String RESOURCE_DATA_ID = "resource-data-id";
    private static final UUID BUILDING_MODULE_ID = UUID.randomUUID();

    @Mock
    private ProductionBuildingModuleService productionBuildingModuleService;

    @Mock
    private BuildingCapacityCalculator buildingCapacityCalculator;

    @InjectMocks
    private ProducerBuildingFinderService underTest;

    @Mock
    private BuildingModules buildingModules;

    @Mock
    private BuildingModule notProducerBuildingModule;

    @Mock
    private BuildingModule incompatibleModule;

    @Mock
    private BuildingModule occupiedModule;

    @Mock
    private BuildingModule matchingModule;

    @Mock
    private GameData gameData;

    @Mock
    private ProducerBuildingModule incompatibleProducerModule;

    @Mock
    private ProducerBuildingModule producerBuildingModule;

    @Mock
    private Production production;

    @Test
    void findProducerBuildingDataId() {
        given(gameData.getBuildingModules()).willReturn(buildingModules);
        given(buildingModules.getByLocation(LOCATION)).willReturn(List.of(incompatibleModule, occupiedModule, notProducerBuildingModule, matchingModule));
        given(incompatibleModule.getDataId()).willReturn(INCOMPATIBLE_DATA_ID);
        given(notProducerBuildingModule.getDataId()).willReturn(NOT_PRODUCER_DATA_ID);
        given(occupiedModule.getDataId()).willReturn(DATA_ID);
        given(matchingModule.getDataId()).willReturn(DATA_ID);
        given(productionBuildingModuleService.get(NOT_PRODUCER_DATA_ID)).willReturn(null);
        given(productionBuildingModuleService.get(INCOMPATIBLE_DATA_ID)).willReturn(incompatibleProducerModule);
        given(productionBuildingModuleService.get(DATA_ID)).willReturn(producerBuildingModule);
        given(incompatibleProducerModule.getProduces()).willReturn(Collections.emptyList());
        given(producerBuildingModule.getProduces()).willReturn(List.of(production));
        given(production.getResourceDataId()).willReturn(RESOURCE_DATA_ID);
        given(buildingCapacityCalculator.isAvailable(gameData, occupiedModule)).willReturn(false);
        given(buildingCapacityCalculator.isAvailable(gameData, matchingModule)).willReturn(true);
        given(matchingModule.getBuildingModuleId()).willReturn(BUILDING_MODULE_ID);

        assertThat(underTest.findProducerBuildingId(gameData, LOCATION, RESOURCE_DATA_ID)).contains(BUILDING_MODULE_ID);
    }
}