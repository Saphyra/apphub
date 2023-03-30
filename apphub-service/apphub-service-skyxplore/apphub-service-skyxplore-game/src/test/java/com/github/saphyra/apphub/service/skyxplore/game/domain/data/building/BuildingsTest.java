package com.github.saphyra.apphub.service.skyxplore.game.domain.data.building;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class BuildingsTest {
    private static final UUID SURFACE_ID = UUID.randomUUID();
    private static final UUID LOCATION = UUID.randomUUID();
    private static final String DATA_ID = "data-id";
    private static final UUID BUILDING_ID = UUID.randomUUID();

    private final Buildings underTest = new Buildings();

    @Mock
    private Building building1;

    @Mock
    private Building building2;

    @Mock
    private Building building3;

    @Test
    void findBySurfaceId_found() {
        given(building1.getSurfaceId()).willReturn(SURFACE_ID);

        underTest.add(building1);

        assertThat(underTest.findBySurfaceId(SURFACE_ID)).contains(building1);
    }

    @Test
    void findBySurfaceId_notFound() {
        given(building1.getSurfaceId()).willReturn(UUID.randomUUID());

        underTest.add(building1);

        assertThat(underTest.findBySurfaceId(SURFACE_ID)).isEmpty();
    }

    @Test
    void getByLocationAndDataId() {
        given(building1.getLocation()).willReturn(LOCATION);
        given(building1.getDataId()).willReturn(DATA_ID);

        given(building2.getLocation()).willReturn(UUID.randomUUID());

        given(building3.getLocation()).willReturn(LOCATION);
        given(building3.getDataId()).willReturn("asd");

        underTest.addAll(List.of(building1, building2, building3));

        assertThat(underTest.getByLocationAndDataId(LOCATION, DATA_ID)).containsExactly(building1);
    }

    @Test
    void getByLocation() {
        given(building1.getLocation()).willReturn(LOCATION);

        given(building2.getLocation()).willReturn(UUID.randomUUID());

        underTest.addAll(List.of(building1, building2));

        assertThat(underTest.getByLocation(LOCATION)).containsExactly(building1);
    }

    @Test
    void findByBuildingId_found() {
        given(building1.getBuildingId()).willReturn(BUILDING_ID);

        underTest.add(building1);

        assertThat(underTest.findByBuildingId(BUILDING_ID)).isEqualTo(building1);
    }

    @Test
    void findByBuildingId_notFound() {
        given(building1.getBuildingId()).willReturn(UUID.randomUUID());

        underTest.add(building1);

        Throwable ex = catchThrowable(() -> underTest.findByBuildingId(BUILDING_ID));

        ExceptionValidator.validateLoggedException(ex, HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND);
    }

    @Test
    void deleteByBuildingId() {
        given(building1.getBuildingId()).willReturn(BUILDING_ID);
        given(building2.getBuildingId()).willReturn(BUILDING_ID);
        given(building3.getBuildingId()).willReturn(UUID.randomUUID());

        underTest.addAll(List.of(building1, building2, building3));

        underTest.deleteByBuildingId(BUILDING_ID);

        assertThat(underTest).containsExactly(building3);
    }
}