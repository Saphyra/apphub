package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.production_dispatcher;

import com.github.saphyra.apphub.api.skyxplore.model.game.ProductionRequestModel;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.ConstructionRequirements;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.StorageType;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.resource.ResourceData;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.resource.ResourceDataService;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.GameProperties;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.ProductionProperties;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.GameProgressDiff;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building_module.BuildingModule;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.production_order.ProductionOrder;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.production_order.ProductionOrderFactory;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.production_order.ProductionOrders;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.production_request.ProductionRequest;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.production_request.ProductionRequestConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.production_request.ProductionRequests;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.reserved_storage.ReservedStorage;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.reserved_storage.ReservedStorages;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.StorageCapacityService;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.construction_area.building_module.BuildingModuleService;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.production_order.ProductionOrderProcessFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class ProductionDispatcherProcessHelperTest {
    private static final UUID LOCATION = UUID.randomUUID();
    private static final UUID PROCESS_ID = UUID.randomUUID();
    private static final UUID PRODUCTION_REQUEST_ID = UUID.randomUUID();
    private static final int MISSING_AMOUNT = 10;
    private static final UUID CONSTRUCTION_AREA_ID = UUID.randomUUID();
    private static final String RESOURCE_DATA_ID = "resource-data-id";
    private static final UUID PRODUCTION_ORDER_ID = UUID.randomUUID();
    private static final UUID GAME_ID = UUID.randomUUID();

    @Mock
    private StorageCapacityService storageCapacityService;

    @Mock
    private GameProperties gameProperties;

    @Mock
    private ResourceDataService resourceDataService;

    @Mock
    private ProductionOrderFactory productionOrderFactory;

    @Mock
    private ProductionOrderProcessFactory productionOrderProcessFactory;

    @Mock
    private ProductionRequestConverter productionRequestConverter;

    @Mock
    private BuildingModuleService buildingModuleService;

    @InjectMocks
    private ProductionDispatcherProcessHelper underTest;

    @Mock
    private Game game;

    @Mock
    private GameData gameData;

    @Mock
    private ProductionRequests productionRequests;

    @Mock
    private ProductionRequest productionRequest;

    @Mock
    private ReservedStorages reservedStorages;

    @Mock
    private ReservedStorage reservedStorage;

    @Mock
    private BuildingModule buildingModule;

    @Mock
    private ProductionOrders productionOrders;

    @Mock
    private ProductionOrder productionOrder;

    @Mock
    private ConstructionRequirements constructionRequirements;

    @Mock
    private ResourceData resourceData;

    @Mock
    private ProductionProperties productionProperties;

    @Mock
    private GameProgressDiff progressDiff;

    @Mock
    private ProductionRequestModel productionRequestModel;

    @BeforeEach
    void setUp() {
        given(game.getData()).willReturn(gameData);
        given(gameData.getProductionRequests()).willReturn(productionRequests);
        given(productionRequests.findByIdValidated(PRODUCTION_REQUEST_ID)).willReturn(productionRequest);
        given(gameData.getReservedStorages()).willReturn(reservedStorages);
        given(reservedStorages.findByIdValidated(productionRequest.getReservedStorageId())).willReturn(reservedStorage);
        given(buildingModuleService.getProducersOf(gameData, LOCATION, RESOURCE_DATA_ID)).willReturn(Stream.of(buildingModule));
        given(gameData.getProductionOrders()).willReturn(productionOrders);
        given(buildingModule.getConstructionAreaId()).willReturn(CONSTRUCTION_AREA_ID);
        given(reservedStorage.getDataId()).willReturn(RESOURCE_DATA_ID);
        given(productionOrders.getByProductionRequestIdAndConstructionAreaIdAndResourceDataId(PRODUCTION_REQUEST_ID, CONSTRUCTION_AREA_ID, RESOURCE_DATA_ID)).willReturn(List.of(productionOrder));
    }

    @Test
    void dispatch_productionOrderInProgress() {
        given(productionOrder.allStarted()).willReturn(false);

        assertThat(underTest.dispatch(game, LOCATION, PROCESS_ID, PRODUCTION_REQUEST_ID, MISSING_AMOUNT)).isZero();
    }

    @Test
    void dispatch_notSupportedStorageType() {
        given(productionOrder.allStarted()).willReturn(true);
        given(resourceDataService.get(RESOURCE_DATA_ID)).willReturn(resourceData);
        given(resourceData.getConstructionRequirements()).willReturn(constructionRequirements);
        given(constructionRequirements.getRequiredResources()).willReturn(Map.of(RESOURCE_DATA_ID, 1));
        given(resourceDataService.get(RESOURCE_DATA_ID)).willReturn(resourceData);
        given(resourceData.getStorageType()).willReturn(StorageType.CONTAINER);
        given(buildingModuleService.getAvailableStorageTypes(gameData, CONSTRUCTION_AREA_ID)).willReturn(Stream.of(StorageType.LIQUID));

        assertThat(underTest.dispatch(game, LOCATION, PROCESS_ID, PRODUCTION_REQUEST_ID, MISSING_AMOUNT)).isZero();
    }

    @Test
    void dispatch() {
        given(productionOrder.allStarted()).willReturn(true);
        given(resourceDataService.get(RESOURCE_DATA_ID)).willReturn(resourceData);
        given(resourceData.getConstructionRequirements()).willReturn(constructionRequirements);
        given(constructionRequirements.getRequiredResources()).willReturn(Map.of(RESOURCE_DATA_ID, 1));
        given(resourceDataService.get(RESOURCE_DATA_ID)).willReturn(resourceData);
        given(resourceData.getStorageType()).willReturn(StorageType.CONTAINER);
        given(buildingModuleService.getAvailableStorageTypes(gameData, CONSTRUCTION_AREA_ID)).willReturn(Stream.of(StorageType.CONTAINER));
        given(storageCapacityService.getTotalConstructionAreaCapacity(gameData, CONSTRUCTION_AREA_ID, StorageType.CONTAINER)).willReturn(100);
        given(storageCapacityService.getOccupiedConstructionAreaCapacity(gameData, CONSTRUCTION_AREA_ID, StorageType.CONTAINER)).willReturn(60);
        given(gameProperties.getProduction()).willReturn(productionProperties);
        given(productionProperties.getProductionOrderMaxDispatchedRatio()).willReturn(0.25);
        given(productionOrderFactory.save(progressDiff, gameData, PRODUCTION_REQUEST_ID, CONSTRUCTION_AREA_ID, RESOURCE_DATA_ID, 10)).willReturn(productionOrder);
        given(productionOrder.getProductionOrderId()).willReturn(PRODUCTION_ORDER_ID);
        given(game.getGameId()).willReturn(GAME_ID);
        given(productionRequestConverter.toModel(GAME_ID, productionRequest)).willReturn(productionRequestModel);
        given(productionRequest.getProductionRequestId()).willReturn(PRODUCTION_REQUEST_ID);
        given(game.getProgressDiff()).willReturn(progressDiff);


        assertThat(underTest.dispatch(game, LOCATION, PROCESS_ID, PRODUCTION_REQUEST_ID, MISSING_AMOUNT)).isEqualTo(10);

        then(productionOrderProcessFactory).should().save(game, LOCATION, PROCESS_ID, PRODUCTION_ORDER_ID);
        then(productionRequest).should().increaseDispatchedAmount(10);
        then(progressDiff).should().save(productionRequestModel);
    }
}