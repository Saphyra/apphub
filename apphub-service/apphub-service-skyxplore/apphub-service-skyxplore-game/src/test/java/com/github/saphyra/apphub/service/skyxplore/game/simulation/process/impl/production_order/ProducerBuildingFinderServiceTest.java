package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.production_order;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.production.ProducerBuildingModule;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.production.Production;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.production.ProductionBuildingService;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building_module.BuildingModule;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building_module.BuildingModules;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Construction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Constructions;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.deconstruction.Deconstruction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.deconstruction.Deconstructions;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.building.BuildingCapacityCalculator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ProducerBuildingFinderServiceTest {
    private static final UUID LOCATION = UUID.randomUUID();
    private static final UUID DECONSTRUCTED_BUILDING_MODULE_ID = UUID.randomUUID();
    private static final UUID CONSTRUCTED_BUILDING_MODULE_ID = UUID.randomUUID();
    private static final String INCOMPATIBLE_DATA_ID = "incompatible-data-id";
    private static final String DATA_ID = "data-id";
    private static final String NOT_PRODUCER_DATA_ID = "not-producer-data-id";
    private static final String RESOURCE_DATA_ID = "resource-data-id";

    @Mock
    private ProductionBuildingService productionBuildingService;

    @Mock
    private BuildingCapacityCalculator buildingCapacityCalculator;

    @InjectMocks
    private ProducerBuildingFinderService underTest;

    @Mock
    private BuildingModules buildingModules;

    @Mock
    private BuildingModule deconstructedModule;

    @Mock
    private BuildingModule constructedModule;

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
    private Deconstructions deconstructions;

    @Mock
    private Deconstruction deconstruction;

    @Mock
    private ProducerBuildingModule incompatibleProducerModule;

    @Mock
    private ProducerBuildingModule producerBuildingModule;

    @Mock
    private Production production;

    @Mock
    private Constructions constructions;

    @Mock
    private Construction construction;

    @Test
    void findProducerBuildingDataId() {
        given(gameData.getBuildingModules()).willReturn(buildingModules);
        given(buildingModules.getByLocation(LOCATION)).willReturn(List.of(deconstructedModule, constructedModule, incompatibleModule, occupiedModule, notProducerBuildingModule, matchingModule));
        given(gameData.getDeconstructions()).willReturn(deconstructions);
        given(deconstructedModule.getBuildingModuleId()).willReturn(DECONSTRUCTED_BUILDING_MODULE_ID);
        given(constructedModule.getBuildingModuleId()).willReturn(CONSTRUCTED_BUILDING_MODULE_ID);
        given(deconstructions.findByExternalReference(DECONSTRUCTED_BUILDING_MODULE_ID)).willReturn(Optional.of(deconstruction));
        given(gameData.getConstructions()).willReturn(constructions);
        given(constructions.findByExternalReference(CONSTRUCTED_BUILDING_MODULE_ID)).willReturn(Optional.of(construction));
        given(incompatibleModule.getDataId()).willReturn(INCOMPATIBLE_DATA_ID);
        given(notProducerBuildingModule.getDataId()).willReturn(NOT_PRODUCER_DATA_ID);
        given(occupiedModule.getDataId()).willReturn(DATA_ID);
        given(matchingModule.getDataId()).willReturn(DATA_ID);
        given(productionBuildingService.get(NOT_PRODUCER_DATA_ID)).willReturn(null);
        given(productionBuildingService.get(INCOMPATIBLE_DATA_ID)).willReturn(incompatibleProducerModule);
        given(productionBuildingService.get(DATA_ID)).willReturn(producerBuildingModule);
        given(incompatibleProducerModule.getProduces()).willReturn(Collections.emptyList());
        given(producerBuildingModule.getProduces()).willReturn(List.of(production));
        given(production.getResourceDataId()).willReturn(RESOURCE_DATA_ID);
        given(buildingCapacityCalculator.isAvailable(gameData, occupiedModule)).willReturn(false);
        given(buildingCapacityCalculator.isAvailable(gameData, matchingModule)).willReturn(true);

        assertThat(underTest.findProducerBuildingDataId(gameData, LOCATION, RESOURCE_DATA_ID)).contains(DATA_ID);
    }
}