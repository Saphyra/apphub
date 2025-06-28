package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.production;

import com.github.saphyra.apphub.api.skyxplore.model.game.ContainerType;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.ConstructionRequirements;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SkillType;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.StorageType;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.module.production.ProductionBuildingModuleData;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.module.production.ProductionBuildingModuleDataService;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.production.Production;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.resource.ResourceData;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.resource.ResourceDataService;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.GameProgressDiff;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building_module.BuildingModule;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.production_order.ProductionOrder;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.production_order.ProductionOrders;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.stored_resource.StoredResourceFactory;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.StorageCapacityService;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.construction_area.building_module.BuildingModuleService;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.work.WorkProcessFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class ProductionProcessHelperTest {
    private static final UUID LOCATION = UUID.randomUUID();
    private static final UUID PROCESS_ID = UUID.randomUUID();
    private static final UUID PRODUCTION_ORDER_ID = UUID.randomUUID();
    private static final int AMOUNT = 10;
    private static final String RESOURCE_DATA_ID = "resource-data-id";
    private static final Integer REQUIRED_WORK_POINTS = 1000;
    private static final UUID CONSTRUCTION_AREA_ID = UUID.randomUUID();
    private static final UUID BUILDING_MODULE_ID = UUID.randomUUID();

    @Mock
    private ProductionBuildingModuleDataService productionBuildingModuleDataService;

    @Mock
    private WorkProcessFactory workProcessFactory;

    @Mock
    private StorageCapacityService storageCapacityService;

    @Mock
    private ResourceDataService resourceDataService;

    @Mock
    private StoredResourceFactory storedResourceFactory;

    @Mock
    private BuildingModuleService buildingModuleService;

    @InjectMocks
    private ProductionProcessHelper underTest;

    @Mock
    private Game game;

    @Mock
    private GameData gameData;

    @Mock
    private ProductionOrders productionOrders;

    @Mock
    private ProductionOrder productionOrder;

    @Mock
    private ProductionBuildingModuleData productionBuildingModuleData;

    @Mock
    private Production production;

    @Mock
    private ConstructionRequirements constructionRequirements;

    @Mock
    private ResourceData resourceData;

    @Mock
    private BuildingModule buildingModule;

    @Mock
    private GameProgressDiff progressDiff;

    @Test
    void createWorkProcess() {
        given(game.getData()).willReturn(gameData);
        given(gameData.getProductionOrders()).willReturn(productionOrders);
        given(productionOrders.findByIdValidated(PRODUCTION_ORDER_ID)).willReturn(productionOrder);
        given(productionOrder.getResourceDataId()).willReturn(RESOURCE_DATA_ID);
        given(productionBuildingModuleDataService.findProducerFor(RESOURCE_DATA_ID)).willReturn(new BiWrapper<>(productionBuildingModuleData, production));
        given(production.getConstructionRequirements()).willReturn(constructionRequirements);
        given(constructionRequirements.getRequiredWorkPoints()).willReturn(REQUIRED_WORK_POINTS);
        given(production.getRequiredSkill()).willReturn(SkillType.AIMING);

        underTest.createWorkProcess(game, LOCATION, PROCESS_ID, PRODUCTION_ORDER_ID, AMOUNT);

        then(workProcessFactory).should().save(game, LOCATION, PROCESS_ID, REQUIRED_WORK_POINTS * AMOUNT, SkillType.AIMING);
    }

    @Test
    void resourceProduced_notEnoughStorage() {
        given(game.getData()).willReturn(gameData);
        given(gameData.getProductionOrders()).willReturn(productionOrders);
        given(productionOrders.findByIdValidated(PRODUCTION_ORDER_ID)).willReturn(productionOrder);
        given(productionOrder.getResourceDataId()).willReturn(RESOURCE_DATA_ID);
        given(resourceDataService.get(RESOURCE_DATA_ID)).willReturn(resourceData);
        given(resourceData.getStorageType()).willReturn(StorageType.CONTAINER);
        given(productionOrder.getConstructionAreaId()).willReturn(CONSTRUCTION_AREA_ID);
        given(storageCapacityService.getEmptyConstructionAreaCapacity(gameData, CONSTRUCTION_AREA_ID, StorageType.CONTAINER)).willReturn(AMOUNT - 1);

        assertThat(underTest.resourcesProduced(game, LOCATION, PRODUCTION_ORDER_ID, AMOUNT)).isFalse();
    }

    @Test
    void resourceProduced() {
        given(game.getData()).willReturn(gameData);
        given(gameData.getProductionOrders()).willReturn(productionOrders);
        given(productionOrders.findByIdValidated(PRODUCTION_ORDER_ID)).willReturn(productionOrder);
        given(productionOrder.getResourceDataId()).willReturn(RESOURCE_DATA_ID);
        given(resourceDataService.get(RESOURCE_DATA_ID)).willReturn(resourceData);
        given(resourceData.getStorageType()).willReturn(StorageType.CONTAINER);
        given(productionOrder.getConstructionAreaId()).willReturn(CONSTRUCTION_AREA_ID);
        given(storageCapacityService.getEmptyConstructionAreaCapacity(gameData, CONSTRUCTION_AREA_ID, StorageType.CONTAINER)).willReturn(AMOUNT);
        given(buildingModuleService.getUsableConstructionAreaContainers(gameData, CONSTRUCTION_AREA_ID, StorageType.CONTAINER)).willReturn(Stream.of(buildingModule));
        given(buildingModule.getBuildingModuleId()).willReturn(BUILDING_MODULE_ID);
        given(storageCapacityService.getFreeContainerCapacity(gameData, BUILDING_MODULE_ID, StorageType.CONTAINER)).willReturn(AMOUNT);
        given(game.getProgressDiff()).willReturn(progressDiff);

        assertThat(underTest.resourcesProduced(game, LOCATION, PRODUCTION_ORDER_ID, AMOUNT)).isTrue();

        then(storedResourceFactory).should().save(progressDiff, gameData, LOCATION, RESOURCE_DATA_ID, AMOUNT, BUILDING_MODULE_ID, ContainerType.STORAGE);
    }
}