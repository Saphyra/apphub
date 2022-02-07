package com.github.saphyra.apphub.service.skyxplore.game.tick.production;

import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.common_util.collection.OptionalHashMap;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.ConstructionRequirements;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.production.ProductionBuilding;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.production.ProductionBuildingService;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.production.ProductionData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Building;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.ProductionOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class BuildingAvailabilityCalculatorTest {
    private static final String RESOURCE_DATA_ID = "resource-data-id";
    private static final String BUILDING_DATA_ID = "building-data-id";
    private static final Integer WORK_POINTS_PER_RESOURCE = 41;
    private static final Integer LEVEL = 134;
    private static final Integer WORKERS = 25;
    private static final UUID BUILDING_ID = UUID.randomUUID();
    private static final Integer ORDER_REQUIRED_WORK_POINTS = 10;
    private static final Integer ORDER_CURRENT_WORK_POINTS = 4;

    @Mock
    private ProductionBuildingService productionBuildingService;

    @InjectMocks
    private BuildingAvailabilityCalculator underTest;

    @Mock
    private Planet planet;

    @Mock
    private Building building;

    @Mock
    private ProductionBuilding productionBuilding;

    @Mock
    private ProductionData productionData;

    @Mock
    private ConstructionRequirements constructionRequirements;

    @Mock
    private ProductionOrder order;

    @Test
    public void calculateBuildingAvailability() {
        given(building.getDataId()).willReturn(BUILDING_DATA_ID);
        given(building.getLevel()).willReturn(LEVEL);
        given(building.getBuildingId()).willReturn(BUILDING_ID);

        given(planet.getOrders()).willReturn(Set.of(order));
        given(order.getAssignee()).willReturn(BUILDING_ID);
        given(order.getRequiredWorkPoints()).willReturn(ORDER_REQUIRED_WORK_POINTS);
        given(order.getCurrentWorkPoints()).willReturn(ORDER_CURRENT_WORK_POINTS);

        given(productionBuildingService.get(BUILDING_DATA_ID)).willReturn(productionBuilding);
        given(productionBuilding.getGives()).willReturn(new OptionalHashMap<>(CollectionUtils.singleValueMap(RESOURCE_DATA_ID, productionData)));
        given(productionBuilding.getWorkers()).willReturn(WORKERS);
        given(productionData.getConstructionRequirements()).willReturn(constructionRequirements);
        given(constructionRequirements.getRequiredWorkPoints()).willReturn(WORK_POINTS_PER_RESOURCE);

        double result = underTest.calculateBuildingAvailability(planet, building, RESOURCE_DATA_ID);

        assertThat(result).isEqualTo(WORKERS * LEVEL / (double) WORK_POINTS_PER_RESOURCE / (1 + ORDER_REQUIRED_WORK_POINTS - ORDER_CURRENT_WORK_POINTS));
    }

}