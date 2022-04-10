package com.github.saphyra.apphub.service.skyxplore.game.process.impl.production_order;

import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.production.ProductionBuilding;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.production.ProductionBuildingService;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.production.ProductionData;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameConstants;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Building;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Surface;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.SurfaceMap;
import com.github.saphyra.apphub.service.skyxplore.game.process.impl.BuildingCapacityCalculator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class ProducerBuildingFinderServiceTest {
    private static final String RESOURCE_DATA_ID = "resource-data-id";
    private static final String BUILDING_DATA_ID = "building-data-id";

    @Mock
    private ProductionBuildingService productionBuildingService;

    @Mock
    private BuildingCapacityCalculator buildingCapacityCalculator;

    @InjectMocks
    private ProducerBuildingFinderService underTest;

    @Mock
    private Planet planet;

    @Mock
    private Surface surface;

    @Mock
    private Building building;

    @Mock
    private ProductionBuilding productionBuilding;

    @Mock
    private ProductionData productionData;

    @Test
    public void findProducerBuildingDataId() {
        given(planet.getSurfaces()).willReturn(new SurfaceMap(CollectionUtils.singleValueMap(GameConstants.ORIGO, surface)));
        given(surface.getBuilding()).willReturn(building);
        given(building.getDataId()).willReturn(BUILDING_DATA_ID);
        given(productionBuildingService.containsKey(BUILDING_DATA_ID)).willReturn(true);
        given(productionBuildingService.get(BUILDING_DATA_ID)).willReturn(productionBuilding);
        given(productionBuilding.getGives()).willReturn(CollectionUtils.singleValueMap(RESOURCE_DATA_ID, productionData));
        given(buildingCapacityCalculator.calculateCapacity(planet, building)).willReturn(12);

        Optional<String> result = underTest.findProducerBuildingDataId(planet, RESOURCE_DATA_ID);

        assertThat(result).contains(BUILDING_DATA_ID);
    }
}