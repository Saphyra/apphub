package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.production_order;

import com.github.saphyra.apphub.api.skyxplore.model.game.ContainerType;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProductionOrderModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.StoredResourceModel;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.ConstructionRequirements;
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
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building_module_allocation.BuildingModuleAllocation;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building_module_allocation.BuildingModuleAllocationFactory;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building_module_allocation.BuildingModuleAllocations;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.production_order.ProductionOrder;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.production_order.ProductionOrderConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.production_order.ProductionOrders;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.reserved_storage.ReservedStorage;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.reserved_storage.ReservedStorageFactory;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.stored_resource.StoredResource;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.stored_resource.StoredResourceConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.stored_resource.StoredResources;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.StoredResourceAggregator;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.construction_area.building_module.BuildingModuleService;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.production.ProductionProcess;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.production.ProductionProcessFactory;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.resource_request.ResourceRequestProcessFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class ProductionOrderProcessHelperTest {
    private static final UUID LOCATION = UUID.randomUUID();
    private static final UUID PROCESS_ID = UUID.randomUUID();
    private static final UUID PRODUCTION_ORDER_ID = UUID.randomUUID();
    private static final String RESOURCE_DATA_ID = "resource-data-id";
    private static final String REQUIRED_RESOURCE_DATA_ID = "required-resource-data-id";
    private static final int AMOUNT = 10;
    private static final Integer REQUESTED_AMOUNT = 5;
    private static final UUID CONSTRUCTION_AREA_ID = UUID.randomUUID();
    private static final UUID BUILDING_MODULE_ID = UUID.randomUUID();
    private static final UUID RESERVED_STORAGE_ID = UUID.randomUUID();
    private static final Integer MISSING_AMOUNT = 3;
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID PRODUCTION_PROCESS_ID = UUID.randomUUID();

    @Mock
    private ProductionBuildingModuleDataService productionBuildingModuleDataService;

    @Mock
    private ReservedStorageFactory reservedStorageFactory;

    @Mock
    private ResourceRequestProcessFactory resourceRequestProcessFactory;

    @Mock
    private ResourceDataService resourceDataService;

    @Mock
    private StoredResourceConverter storedResourceConverter;

    @Mock
    private ProductionOrderConverter productionOrderConverter;

    @Mock
    private ProductionProcessFactory productionProcessFactory;

    @Mock
    private BuildingModuleAllocationFactory buildingModuleAllocationFactory;

    @Mock
    private BuildingModuleService buildingModuleService;

    @Mock
    private StoredResourceAggregator storedResourceAggregator;

    @InjectMocks
    private ProductionOrderProcessHelper underTest;

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
    private GameProgressDiff progressDiff;

    @Mock
    private ResourceData resourceData;

    @Mock
    private BuildingModule buildingModule;

    @Mock
    private ReservedStorage reservedStorage;

    @Mock
    private BuildingModuleAllocations buildingModuleAllocations;

    @Mock
    private BuildingModuleAllocation buildingModuleAllocation;

    @Mock
    private StoredResources storedResources;

    @Mock
    private StoredResource storedResource;

    @Mock
    private StoredResourceModel storedResourceModel;

    @Mock
    private ProductionOrderModel productionOrderModel;

    @Mock
    private ProductionProcess productionProcess;

    @Test
    void getRequiredResources() {
        given(game.getData()).willReturn(gameData);
        given(gameData.getProductionOrders()).willReturn(productionOrders);
        given(productionOrders.findByIdValidated(PRODUCTION_ORDER_ID)).willReturn(productionOrder);
        given(productionOrder.getResourceDataId()).willReturn(RESOURCE_DATA_ID);
        given(productionBuildingModuleDataService.findProducerFor(RESOURCE_DATA_ID)).willReturn(new BiWrapper<>(productionBuildingModuleData, production));
        given(production.getConstructionRequirements()).willReturn(constructionRequirements);
        given(constructionRequirements.getRequiredResources()).willReturn(Map.of(REQUIRED_RESOURCE_DATA_ID, AMOUNT));
        given(productionOrder.getRequestedAmount()).willReturn(REQUESTED_AMOUNT);
        given(game.getProgressDiff()).willReturn(progressDiff);
        given(productionOrder.getConstructionAreaId()).willReturn(CONSTRUCTION_AREA_ID);
        given(resourceDataService.get(REQUIRED_RESOURCE_DATA_ID)).willReturn(resourceData);
        given(resourceData.getStorageType()).willReturn(StorageType.CONTAINER);
        given(buildingModuleService.getUsableConstructionAreaContainers(gameData, CONSTRUCTION_AREA_ID, StorageType.CONTAINER)).willReturn(Stream.of(buildingModule));
        given(buildingModule.getBuildingModuleId()).willReturn(BUILDING_MODULE_ID);
        given(reservedStorageFactory.save(progressDiff, gameData, BUILDING_MODULE_ID, ContainerType.STORAGE, PROCESS_ID, REQUIRED_RESOURCE_DATA_ID, AMOUNT * REQUESTED_AMOUNT)).willReturn(reservedStorage);
        given(reservedStorage.getReservedStorageId()).willReturn(RESERVED_STORAGE_ID);

        underTest.orderResources(game, LOCATION, PROCESS_ID, PRODUCTION_ORDER_ID);

        then(resourceRequestProcessFactory).should().save(game, LOCATION, PROCESS_ID, RESERVED_STORAGE_ID);
    }

    @Test
    void tryProduce_noUsableProducer() {
        given(game.getData()).willReturn(gameData);
        given(gameData.getProductionOrders()).willReturn(productionOrders);
        given(productionOrders.findByIdValidated(PRODUCTION_ORDER_ID)).willReturn(productionOrder);
        given(productionOrder.getConstructionAreaId()).willReturn(CONSTRUCTION_AREA_ID);
        given(productionOrder.getResourceDataId()).willReturn(RESOURCE_DATA_ID);
        given(buildingModuleService.getUsableConstructionAreaProducers(gameData, CONSTRUCTION_AREA_ID, RESOURCE_DATA_ID)).willReturn(Stream.of(buildingModule));
        given(gameData.getBuildingModuleAllocations()).willReturn(buildingModuleAllocations);
        given(buildingModule.getBuildingModuleId()).willReturn(BUILDING_MODULE_ID);
        given(buildingModuleAllocations.findByBuildingModuleId(BUILDING_MODULE_ID)).willReturn(Optional.of(buildingModuleAllocation));

        underTest.tryProduce(game, LOCATION, PROCESS_ID, PRODUCTION_ORDER_ID);

        then(buildingModuleAllocationFactory).shouldHaveNoInteractions();
    }

    @Test
    void tryProduce_notEnoughResources() {
        given(game.getData()).willReturn(gameData);
        given(gameData.getProductionOrders()).willReturn(productionOrders);
        given(productionOrders.findByIdValidated(PRODUCTION_ORDER_ID)).willReturn(productionOrder);
        given(productionOrder.getConstructionAreaId()).willReturn(CONSTRUCTION_AREA_ID);
        given(productionOrder.getResourceDataId()).willReturn(RESOURCE_DATA_ID);
        given(buildingModuleService.getUsableConstructionAreaProducers(gameData, CONSTRUCTION_AREA_ID, RESOURCE_DATA_ID)).willReturn(Stream.of(buildingModule));
        given(gameData.getBuildingModuleAllocations()).willReturn(buildingModuleAllocations);
        given(buildingModule.getBuildingModuleId()).willReturn(BUILDING_MODULE_ID);
        given(buildingModuleAllocations.findByBuildingModuleId(BUILDING_MODULE_ID)).willReturn(Optional.empty());
        given(gameData.getStoredResources()).willReturn(storedResources);
        given(storedResources.getByAllocatedBy(PROCESS_ID)).willReturn(List.of());
        given(productionOrder.getMissingAmount()).willReturn(MISSING_AMOUNT);
        given(productionBuildingModuleDataService.findProducerFor(RESOURCE_DATA_ID)).willReturn(new BiWrapper<>(productionBuildingModuleData, production));
        given(production.getConstructionRequirements()).willReturn(constructionRequirements);
        given(constructionRequirements.getRequiredResources()).willReturn(Map.of(REQUIRED_RESOURCE_DATA_ID, AMOUNT));

        underTest.tryProduce(game, LOCATION, PROCESS_ID, PRODUCTION_ORDER_ID);

        then(buildingModuleAllocationFactory).shouldHaveNoInteractions();
    }

    @Test
    void tryProduce_produce() {
        given(game.getData()).willReturn(gameData);
        given(gameData.getProductionOrders()).willReturn(productionOrders);
        given(productionOrders.findByIdValidated(PRODUCTION_ORDER_ID)).willReturn(productionOrder);
        given(productionOrder.getConstructionAreaId()).willReturn(CONSTRUCTION_AREA_ID);
        given(productionOrder.getResourceDataId()).willReturn(RESOURCE_DATA_ID);
        given(buildingModuleService.getUsableConstructionAreaProducers(gameData, CONSTRUCTION_AREA_ID, RESOURCE_DATA_ID)).willReturn(Stream.of(buildingModule));
        given(gameData.getBuildingModuleAllocations()).willReturn(buildingModuleAllocations);
        given(buildingModule.getBuildingModuleId()).willReturn(BUILDING_MODULE_ID);
        given(buildingModuleAllocations.findByBuildingModuleId(BUILDING_MODULE_ID)).willReturn(Optional.empty());
        given(gameData.getStoredResources()).willReturn(storedResources);
        given(storedResources.getByAllocatedBy(PROCESS_ID)).willReturn(List.of(storedResource));
        given(storedResource.getDataId()).willReturn(REQUIRED_RESOURCE_DATA_ID);
        given(storedResource.getAmount()).willReturn(AMOUNT * 2);
        given(productionOrder.getMissingAmount()).willReturn(MISSING_AMOUNT);
        given(productionBuildingModuleDataService.findProducerFor(RESOURCE_DATA_ID)).willReturn(new BiWrapper<>(productionBuildingModuleData, production));
        given(production.getConstructionRequirements()).willReturn(constructionRequirements);
        given(constructionRequirements.getRequiredResources()).willReturn(Map.of(REQUIRED_RESOURCE_DATA_ID, AMOUNT));
        given(game.getGameId()).willReturn(GAME_ID);
        given(storedResourceConverter.toModel(GAME_ID, storedResource)).willReturn(storedResourceModel);
        given(game.getProgressDiff()).willReturn(progressDiff);
        given(productionOrderConverter.toModel(GAME_ID, productionOrder)).willReturn(productionOrderModel);
        given(productionProcessFactory.save(game, LOCATION, PROCESS_ID, PRODUCTION_ORDER_ID, BUILDING_MODULE_ID, 2)).willReturn(productionProcess);
        given(storedResourceAggregator.aggregate(progressDiff, gameData, List.of(storedResource))).willReturn(storedResource);
        given(productionProcess.getProcessId()).willReturn(PRODUCTION_PROCESS_ID);

        underTest.tryProduce(game, LOCATION, PROCESS_ID, PRODUCTION_ORDER_ID);

        then(storedResource).should().decreaseAmount(AMOUNT * 2);
        then(progressDiff).should().save(storedResourceModel);
        then(productionOrder).should().increaseStartedAmount(2);
        then(progressDiff).should().save(productionOrderModel);
        then(buildingModuleAllocationFactory).should().save(progressDiff, gameData, BUILDING_MODULE_ID, PRODUCTION_PROCESS_ID);
    }
}