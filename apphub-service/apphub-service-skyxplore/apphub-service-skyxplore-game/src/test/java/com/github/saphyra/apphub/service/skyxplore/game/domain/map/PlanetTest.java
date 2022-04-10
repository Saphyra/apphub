package com.github.saphyra.apphub.service.skyxplore.game.domain.map;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class PlanetTest {
    private static final UUID CONSTRUCTION_ID = UUID.randomUUID();
    private static final UUID BUILDING_ID = UUID.randomUUID();

    @Mock
    private Surface surface;

    @Mock
    private Coordinate coordinate;

    @Mock
    private Building building;

    @Mock
    private Construction construction;

    @Test
    public void getBuildings() {
        Planet underTest = Planet.builder()
            .surfaces(new SurfaceMap(CollectionUtils.singleValueMap(coordinate, surface)))
            .build();
        given(surface.getBuilding()).willReturn(building);

        List<Building> result = underTest.getBuildings();

        assertThat(result).containsExactly(building);
    }

    @Test
    public void findBuildingByConstructionIdValidated_notFound() {
        Planet underTest = Planet.builder()
            .surfaces(new SurfaceMap())
            .build();

        Throwable ex = catchThrowable(() -> underTest.findBuildingByConstructionIdValidated(CONSTRUCTION_ID));

        ExceptionValidator.validateLoggedException(ex, HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND);
    }

    @Test
    public void findBuildingByConstructionIdValidated() {
        Planet underTest = Planet.builder()
            .surfaces(new SurfaceMap(CollectionUtils.singleValueMap(coordinate, surface)))
            .build();

        given(surface.getBuilding()).willReturn(building);
        given(building.getConstruction()).willReturn(construction);
        given(construction.getConstructionId()).willReturn(CONSTRUCTION_ID);

        Building result = underTest.findBuildingByConstructionIdValidated(CONSTRUCTION_ID);

        assertThat(result).isEqualTo(building);
    }

    @Test
    public void findSurfaceByBuildingIdValidated_notFound() {
        Planet underTest = Planet.builder()
            .surfaces(new SurfaceMap())
            .build();

        Throwable ex = catchThrowable(() -> underTest.findSurfaceByBuildingIdValidated(BUILDING_ID));

        ExceptionValidator.validateLoggedException(ex, HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND);
    }

    @Test
    public void findSurfaceByBuildingId() {
        Planet underTest = Planet.builder()
            .surfaces(new SurfaceMap(CollectionUtils.singleValueMap(coordinate, surface)))
            .build();

        given(surface.getBuilding()).willReturn(building);
        given(building.getBuildingId()).willReturn(BUILDING_ID);

        Surface result = underTest.findSurfaceByBuildingIdValidated(BUILDING_ID);

        assertThat(result).isEqualTo(surface);
    }
}