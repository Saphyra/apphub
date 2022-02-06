package com.github.saphyra.apphub.service.skyxplore.game.tick.production;

import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.common_util.collection.OptionalHashMap;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.production.ProductionBuilding;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.production.ProductionBuildingService;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.production.ProductionData;
import com.github.saphyra.apphub.service.skyxplore.game.GameTestUtils;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Building;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Surface;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.SurfaceMap;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class ProductionBuildingFinderTest {
    private static final String RESOURCE_DATA_ID = "resource-data-id";
    private static final String BUILDING_DATA_ID = "building-data-id";

    @Mock
    private ProductionBuildingService productionBuildingService;

    @Mock
    private BuildingAvailabilityCalculator buildingAvailabilityCalculator;

    @InjectMocks
    private ProductionBuildingFinder underTest;

    @Mock
    private Planet planet;

    @Mock
    private Surface emptySurface;

    @Mock
    private Surface incompatibleSurface;

    @Mock
    private Surface ineffectiveSurface;

    @Mock
    private Surface effectiveSurface;

    @Mock
    private Building incompatibleBuilding;

    @Mock
    private Building ineffectiveBuilding;

    @Mock
    private Building effectiveBuilding;

    @Mock
    private ProductionBuilding productionBuilding;

    @Mock
    private ProductionData productionData;

    @Test
    public void findProducer() {
        given(planet.getSurfaces()).willReturn(new SurfaceMap(CollectionUtils.toMap(
            new BiWrapper<>(GameTestUtils.randomCoordinate(), emptySurface),
            new BiWrapper<>(GameTestUtils.randomCoordinate(), incompatibleSurface),
            new BiWrapper<>(GameTestUtils.randomCoordinate(), ineffectiveSurface),
            new BiWrapper<>(GameTestUtils.randomCoordinate(), effectiveSurface)
        )));

        given(productionBuildingService.containsKey(BUILDING_DATA_ID)).willReturn(true);

        given(incompatibleSurface.getBuilding()).willReturn(incompatibleBuilding);
        given(ineffectiveSurface.getBuilding()).willReturn(ineffectiveBuilding);
        given(effectiveSurface.getBuilding()).willReturn(effectiveBuilding);

        given(incompatibleBuilding.getDataId()).willReturn("asd");
        given(ineffectiveBuilding.getDataId()).willReturn(BUILDING_DATA_ID);
        given(effectiveBuilding.getDataId()).willReturn(BUILDING_DATA_ID);

        given(productionBuildingService.get(BUILDING_DATA_ID)).willReturn(productionBuilding);
        given(productionBuilding.getGives()).willReturn(new OptionalHashMap<>(CollectionUtils.singleValueMap(RESOURCE_DATA_ID, productionData)));

        given(buildingAvailabilityCalculator.calculateBuildingAvailability(planet, ineffectiveBuilding, RESOURCE_DATA_ID)).willReturn(1d);
        given(buildingAvailabilityCalculator.calculateBuildingAvailability(planet, effectiveBuilding, RESOURCE_DATA_ID)).willReturn(2d);

        Optional<Building> result = underTest.findProducer(planet, RESOURCE_DATA_ID);

        assertThat(result).contains(effectiveBuilding);
    }
}