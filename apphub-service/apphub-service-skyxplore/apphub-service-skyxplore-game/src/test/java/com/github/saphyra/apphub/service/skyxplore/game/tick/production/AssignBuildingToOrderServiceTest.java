package com.github.saphyra.apphub.service.skyxplore.game.tick.production;

import com.github.saphyra.apphub.api.skyxplore.model.game.ProductionOrderModel;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.common_util.collection.OptionalHashMap;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.ConstructionRequirements;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.production.ProductionBuilding;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.production.ProductionBuildingService;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.production.ProductionData;
import com.github.saphyra.apphub.service.skyxplore.game.common.converter.model.ProductionOrderToModelConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.LocationType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.ReservedStorage;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Building;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.ProductionOrder;
import com.github.saphyra.apphub.service.skyxplore.game.tick.cache.GameItemCache;
import com.github.saphyra.apphub.service.skyxplore.game.tick.cache.TickCache;
import com.github.saphyra.apphub.service.skyxplore.game.tick.cache.TickCacheItem;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class AssignBuildingToOrderServiceTest {
    private static final String DATA_ID_1 = "data-id-1";
    private static final String DATA_ID_2 = "data-id-2";
    private static final Integer MAX_BATCH_SIZE = 20;
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final Integer REQUIRED_WORK_POINTS = 134;
    private static final UUID BUILDING_ID = UUID.randomUUID();
    private static final UUID ORIGINAL_PRODUCTION_ORDER_ID = UUID.randomUUID();
    private static final UUID RESERVED_STORAGE_ID = UUID.randomUUID();
    private static final UUID PLANET_ID = UUID.randomUUID();
    private static final int RESERVED_AMOUNT = 2;
    private static final UUID EXTERNAL_REFERENCE = UUID.randomUUID();
    public static final int LEFTOVER_AMOUNT = 3;
    private static final String BUILDING_DATA_ID = "building-data-id";

    @Mock
    private ProductionOrderFactory productionOrderFactory;

    @Mock
    private SilentResourceAllocationService silentResourceAllocationService;

    @Mock
    private ProductionBuildingService productionBuildingService;

    @Mock
    private TickCache tickCache;

    @Mock
    private ProductionOrderToModelConverter productionOrderToModelConverter;

    @Mock
    private ProductionBuildingFinder productionBuildingFinder;

    @Mock
    private ProductionOrderProcessingService productionOrderProcessingService;

    @InjectMocks
    private AssignBuildingToOrderService underTest;

    @Mock
    private Planet planet;

    @Mock
    private ProductionOrder originalOrder;

    @Mock
    private ProductionOrder requirementOrder;

    @Mock
    private ProductionOrder leftoverOrder;

    @Mock
    private Building building;

    @Mock
    private ProductionBuilding productionBuilding;

    @Mock
    private ProductionData productionData;

    @Mock
    private ConstructionRequirements constructionRequirements;

    @Mock
    private TickCacheItem tickCacheItem;

    @Mock
    private GameItemCache gameItemCache;

    @Mock
    private ProductionOrderModel originalProductionOrderModel;

    @Mock
    private ProductionOrderModel requirementProductionOrderModel;

    @Mock
    private ProductionOrderModel leftoverProductionOrderModel;

    @Mock
    private ReservedStorage reservedStorage;

    @Test
    public void assignOrder() {
        Map<String, Integer> requiredResources = CollectionUtils.singleValueMap(DATA_ID_2, 4);

        given(productionBuildingFinder.findProducer(planet, DATA_ID_1)).willReturn(Optional.of(building));
        given(productionBuildingService.get(BUILDING_DATA_ID)).willReturn(productionBuilding);
        given(productionBuilding.getGives()).willReturn(new OptionalHashMap<>(CollectionUtils.singleValueMap(DATA_ID_1, productionData)));
        given(productionData.getMaxBatchSize()).willReturn(MAX_BATCH_SIZE);
        given(productionData.getConstructionRequirements()).willReturn(constructionRequirements);
        given(constructionRequirements.getRequiredWorkPoints()).willReturn(REQUIRED_WORK_POINTS);
        given(constructionRequirements.getRequiredResources()).willReturn(requiredResources);

        given(tickCache.get(GAME_ID)).willReturn(tickCacheItem);
        given(tickCacheItem.getGameItemCache()).willReturn(gameItemCache);

        Set<ProductionOrder> orders = new HashSet<>();

        given(originalOrder.getAmount()).willReturn(MAX_BATCH_SIZE + LEFTOVER_AMOUNT);

        given(silentResourceAllocationService.allocateResources(GAME_ID, planet, ORIGINAL_PRODUCTION_ORDER_ID, CollectionUtils.singleValueMap(DATA_ID_2, MAX_BATCH_SIZE * 4))).willReturn(List.of(reservedStorage));

        given(productionOrderFactory.create(RESERVED_STORAGE_ID, PLANET_ID, LocationType.PLANET, DATA_ID_2, RESERVED_AMOUNT)).willReturn(requirementOrder);
        given(productionOrderFactory.create(EXTERNAL_REFERENCE, PLANET_ID, LocationType.PLANET, DATA_ID_1, LEFTOVER_AMOUNT)).willReturn(leftoverOrder);

        given(building.getBuildingId()).willReturn(BUILDING_ID);
        given(building.getDataId()).willReturn(BUILDING_DATA_ID);

        given(originalOrder.getDataId()).willReturn(DATA_ID_1);
        given(originalOrder.getProductionOrderId()).willReturn(ORIGINAL_PRODUCTION_ORDER_ID);
        given(originalOrder.getExternalReference()).willReturn(EXTERNAL_REFERENCE);

        given(productionOrderToModelConverter.convert(originalOrder, GAME_ID)).willReturn(originalProductionOrderModel);
        given(productionOrderToModelConverter.convert(requirementOrder, GAME_ID)).willReturn(requirementProductionOrderModel);
        given(productionOrderToModelConverter.convert(leftoverOrder, GAME_ID)).willReturn(leftoverProductionOrderModel);

        given(reservedStorage.getReservedStorageId()).willReturn(RESERVED_STORAGE_ID);
        given(reservedStorage.getDataId()).willReturn(DATA_ID_2);
        given(reservedStorage.getAmount()).willReturn(RESERVED_AMOUNT);

        given(planet.getPlanetId()).willReturn(PLANET_ID);
        given(planet.getOrders()).willReturn(orders);

        underTest.assignOrder(GAME_ID, planet, originalOrder, productionOrderProcessingService);

        verify(originalOrder).setRequiredWorkPoints(MAX_BATCH_SIZE * REQUIRED_WORK_POINTS);
        verify(originalOrder).setAssignee(BUILDING_ID);
        verify(originalOrder).setAmount(MAX_BATCH_SIZE);

        verify(gameItemCache).save(originalProductionOrderModel);
        verify(gameItemCache).save(requirementProductionOrderModel);
        verify(gameItemCache).save(leftoverProductionOrderModel);

        assertThat(orders).containsExactlyInAnyOrder(requirementOrder, leftoverOrder);

        verify(productionOrderProcessingService).processOrder(GAME_ID, planet, requirementOrder);
        verify(productionOrderProcessingService).processOrder(GAME_ID, planet, leftoverOrder);
    }
}