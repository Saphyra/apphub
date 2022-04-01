package com.github.saphyra.apphub.service.skyxplore.game.tick.production;

import com.github.saphyra.apphub.api.skyxplore.model.game.ProductionOrderModel;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.common_util.collection.OptionalHashMap;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.ConstructionRequirements;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SkillType;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.production.ProductionBuilding;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.production.ProductionBuildingService;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.production.ProductionData;
import com.github.saphyra.apphub.service.skyxplore.game.common.converter.model.ProductionOrderToModelConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.citizen.Citizen;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Building;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.ProductionOrder;
import com.github.saphyra.apphub.service.skyxplore.game.tick.cache.Assignment;
import com.github.saphyra.apphub.service.skyxplore.game.process.cache.GameItemCache;
import com.github.saphyra.apphub.service.skyxplore.game.tick.cache.TickCache;
import com.github.saphyra.apphub.service.skyxplore.game.tick.cache.TickCacheItem;
import com.github.saphyra.apphub.service.skyxplore.game.tick.work.AssignCitizenService;
import com.github.saphyra.apphub.service.skyxplore.game.tick.work.AvailableCitizenProvider;
import com.github.saphyra.apphub.service.skyxplore.game.tick.work.MakeCitizenWorkService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ProductionBuildingOrderProcessorTest {
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID PRODUCTION_ORDER_ID = UUID.randomUUID();
    private static final String BUILDING_DATA_ID = "building-data-id";
    private static final String RESOURCE_DATA_ID = "resource-data-id";
    private static final UUID CITIZEN_ID_1 = UUID.randomUUID();
    private static final UUID CITIZEN_ID_2 = UUID.randomUUID();
    private static final Integer CURRENT_WORK_POINTS = 0;
    private static final Integer REQUIRED_WORK_POINTS = 24512;
    private static final Integer MISSING_WORK_POINTS = REQUIRED_WORK_POINTS - CURRENT_WORK_POINTS;
    private static final UUID BUILDING_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID PLANET_ID = UUID.randomUUID();
    private static final Integer COMPLETED_WORK = 100;

    @Mock
    private ProductionBuildingService productionBuildingService;

    @Mock
    private AllocatedResourceResolver allocatedResourceResolver;

    @Mock
    private TickCache tickCache;

    @Mock
    private AvailableCitizenProvider availableCitizenProvider;

    @Mock
    private AssignCitizenService assignCitizenService;

    @Mock
    private MakeCitizenWorkService makeCitizenWorkService;

    @Mock
    private ProductionOrderToModelConverter productionOrderToModelConverter;

    @InjectMocks
    private ProductionBuildingOrderProcessor underTest;

    @Mock
    private Planet planet;

    @Mock
    private Building building;

    @Mock
    private ProductionOrder order;

    @Mock
    private ProductionBuilding productionBuilding;

    @Mock
    private ProductionData productionData;

    @Mock
    private ConstructionRequirements constructionRequirements;

    @Mock
    private Assignment existingAssignment;

    @Mock
    private Assignment newAssignment;

    @Mock
    private TickCacheItem tickCacheItem;

    @Mock
    private Citizen assignedCitizen;

    @Mock
    private Citizen unemployedCitizen;

    @Mock
    private ProductionOrderModel productionOrderModel;

    @Mock
    private GameItemCache gameItemCache;

    @Test
    public void processOrderByAssignedBuilding() {
        Map<UUID, Assignment> assignments = CollectionUtils.singleValueMap(CITIZEN_ID_1, existingAssignment);
        given(tickCache.get(GAME_ID)).willReturn(tickCacheItem);
        given(tickCacheItem.getCitizenAssignments()).willReturn(assignments);
        given(tickCacheItem.getGameItemCache()).willReturn(gameItemCache);

        given(order.getProductionOrderId()).willReturn(PRODUCTION_ORDER_ID);
        given(order.getDataId()).willReturn(RESOURCE_DATA_ID);
        given(order.getCurrentWorkPoints()).willReturn(CURRENT_WORK_POINTS);
        given(order.getRequiredWorkPoints()).willReturn(REQUIRED_WORK_POINTS);

        given(building.getDataId()).willReturn(BUILDING_DATA_ID);
        given(building.getBuildingId()).willReturn(BUILDING_ID);

        OptionalHashMap<UUID, Citizen> population = new OptionalHashMap<>(CollectionUtils.toMap(
            new BiWrapper<>(CITIZEN_ID_1, assignedCitizen),
            new BiWrapper<>(CITIZEN_ID_2, unemployedCitizen)
        ));
        given(planet.getPopulation()).willReturn(population);
        given(planet.getOwner()).willReturn(USER_ID);
        given(planet.getPlanetId()).willReturn(PLANET_ID);

        given(productionBuildingService.get(BUILDING_DATA_ID)).willReturn(productionBuilding);
        given(productionBuilding.getGives()).willReturn(new OptionalHashMap<>(CollectionUtils.singleValueMap(RESOURCE_DATA_ID, productionData)));
        given(productionBuilding.getWorkers()).willReturn(3);
        given(productionData.getConstructionRequirements()).willReturn(constructionRequirements);
        given(productionData.getRequiredSkill()).willReturn(SkillType.AIMING);
        given(constructionRequirements.getParallelWorkers()).willReturn(4);

        given(availableCitizenProvider.findMostCapableUnemployedCitizen(assignments, population.values(), BUILDING_ID, SkillType.AIMING))
            .willReturn(Optional.of(assignedCitizen))
            .willReturn(Optional.of(unemployedCitizen))
            .willReturn(Optional.empty());

        given(assignedCitizen.getCitizenId()).willReturn(CITIZEN_ID_1);
        given(unemployedCitizen.getCitizenId()).willReturn(CITIZEN_ID_2);

        given(assignCitizenService.assignCitizen(GAME_ID, unemployedCitizen, BUILDING_ID)).willReturn(newAssignment);
        given(makeCitizenWorkService.requestWork(GAME_ID, USER_ID, PLANET_ID, existingAssignment, MISSING_WORK_POINTS, SkillType.AIMING)).willReturn(COMPLETED_WORK);
        given(makeCitizenWorkService.requestWork(GAME_ID, USER_ID, PLANET_ID, newAssignment, MISSING_WORK_POINTS, SkillType.AIMING)).willReturn(COMPLETED_WORK);

        given(productionOrderToModelConverter.convert(order, GAME_ID)).willReturn(productionOrderModel);

        underTest.processOrderByAssignedBuilding(GAME_ID, planet, building, order);

        verify(allocatedResourceResolver).resolveAllocations(GAME_ID, planet, PRODUCTION_ORDER_ID);
        verify(order, times(2)).setCurrentWorkPoints(CURRENT_WORK_POINTS + COMPLETED_WORK);
        verify(gameItemCache).save(productionOrderModel);
    }
}