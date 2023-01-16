package com.github.saphyra.apphub.service.skyxplore.game.process.impl.request_work;

import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameConstants;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Building;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Surface;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.SurfaceMap;
import com.github.saphyra.apphub.service.skyxplore.game.process.impl.BuildingCapacityCalculator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class ProductionBuildingFinderTest {
    private static final String DATA_ID = "data-id";
    private static final UUID BUILDING_ID = UUID.randomUUID();

    @Mock
    private BuildingCapacityCalculator buildingCapacityCalculator;

    @InjectMocks
    private ProductionBuildingFinder underTest;

    @Mock
    private Planet planet;

    @Mock
    private Surface surface;

    @Mock
    private Building building;

    @Test
    public void findSuitableProductionBuilding() {
        given(planet.getSurfaces()).willReturn(CollectionUtils.singleValueMap(GameConstants.ORIGO, surface, new SurfaceMap()));
        given(surface.getBuilding()).willReturn(building);
        given(building.getDataId()).willReturn(DATA_ID);
        given(buildingCapacityCalculator.calculateCapacity(planet, building)).willReturn(1);
        given(building.getBuildingId()).willReturn(BUILDING_ID);

        Optional<UUID> result = underTest.findSuitableProductionBuilding(planet, DATA_ID);

        assertThat(result).contains(BUILDING_ID);
    }
}