package com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface;

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
class SurfacesTest {
    private static final UUID PLANET_ID = UUID.randomUUID();
    private static final UUID SURFACE_ID = UUID.randomUUID();

    private final Surfaces underTest = new Surfaces();

    @Mock
    private Surface surface1;

    @Mock
    private Surface surface2;

    @Test
    void getByPlanetId() {
        given(surface1.getPlanetId()).willReturn(PLANET_ID);
        given(surface2.getPlanetId()).willReturn(UUID.randomUUID());

        underTest.addAll(List.of(surface1, surface2));

        assertThat(underTest.getByPlanetId(PLANET_ID)).containsExactly(surface1);
    }

    @Test
    void findBySurfaceId_found() {
        given(surface1.getSurfaceId()).willReturn(SURFACE_ID);

        underTest.add(surface1);

        assertThat(underTest.findBySurfaceIdValidated(SURFACE_ID)).isEqualTo(surface1);
    }

    @Test
    void findBySurfaceId_notFound() {
        given(surface1.getSurfaceId()).willReturn(UUID.randomUUID());

        underTest.add(surface1);

        Throwable ex = catchThrowable(() -> underTest.findBySurfaceIdValidated(SURFACE_ID));

        ExceptionValidator.validateLoggedException(ex, HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND);
    }
}