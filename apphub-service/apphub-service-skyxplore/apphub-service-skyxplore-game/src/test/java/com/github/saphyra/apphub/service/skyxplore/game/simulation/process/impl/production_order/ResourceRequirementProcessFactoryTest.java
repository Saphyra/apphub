package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.production_order;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.ConstructionRequirements;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.production.ProducerBuildingModule;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.production.Production;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.production.ProductionBuildingModuleService;
import com.github.saphyra.apphub.service.skyxplore.game.domain.GameProgressDiff;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ResourceRequirementProcessFactoryTest {
    private static final UUID PROCESS_ID = UUID.randomUUID();
    private static final UUID LOCATION = UUID.randomUUID();
    private static final String RESOURCE_DATA_ID = "resource-data-id";
    private static final int AMOUNT = 10;
    private static final String BUILDING_DATA_ID = "building-data-id";
    private static final String REQUIRED_RESOURCE_DATA_ID = "required-resource-data-id";
    private static final Integer REQUIRED_AMOUNT = 23;
    private static final UUID RESERVED_STORAGE_ID = UUID.randomUUID();

    @Mock
    private ProductionBuildingModuleService productionBuildingModuleService;

    @Mock
    private ProductionRequirementsAllocationService productionRequirementsAllocationService;

    @Mock
    private ProductionOrderProcessFactory productionOrderProcessFactory;

    @InjectMocks
    private ResourceRequirementProcessFactory underTest;

    @Mock
    private GameProgressDiff progressDiff;

    @Mock
    private GameData gameData;

    @Mock
    private ConstructionRequirements constructionRequirements;

    @Mock
    private ProductionOrderProcess productionOrderProcess;

    @Mock
    private ProducerBuildingModule producerBuildingModule;

    @Mock
    private Production production;

    @Test
    void createResourceRequirementProcesses() {
        given(productionBuildingModuleService.get(BUILDING_DATA_ID)).willReturn(producerBuildingModule);
        given(producerBuildingModule.getProduces()).willReturn(List.of(production));
        given(production.getResourceDataId()).willReturn(RESOURCE_DATA_ID);
        given(production.getConstructionRequirements()).willReturn(constructionRequirements);
        given(constructionRequirements.getRequiredResources()).willReturn(Map.of(REQUIRED_RESOURCE_DATA_ID, REQUIRED_AMOUNT));
        given(productionRequirementsAllocationService.allocate(progressDiff, gameData, LOCATION, PROCESS_ID, REQUIRED_RESOURCE_DATA_ID, AMOUNT * REQUIRED_AMOUNT)).willReturn(RESERVED_STORAGE_ID);
        given(productionOrderProcessFactory.create(gameData, PROCESS_ID, LOCATION, RESERVED_STORAGE_ID)).willReturn(List.of(productionOrderProcess));

        List<ProductionOrderProcess> result = underTest.createResourceRequirementProcesses(progressDiff, gameData, PROCESS_ID, LOCATION, RESOURCE_DATA_ID, AMOUNT, BUILDING_DATA_ID);

        assertThat(result).containsExactly(productionOrderProcess);
    }
}