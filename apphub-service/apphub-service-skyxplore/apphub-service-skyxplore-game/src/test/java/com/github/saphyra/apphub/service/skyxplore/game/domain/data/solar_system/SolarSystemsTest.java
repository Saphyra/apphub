package com.github.saphyra.apphub.service.skyxplore.game.domain.data.solar_system;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class SolarSystemsTest {
    private static final UUID SOLAR_SYSTEM_ID = UUID.randomUUID();

    private final SolarSystems underTest = new SolarSystems();

    @Mock
    private SolarSystem solarSystem;

    @Test
    void findByIdValidated_found() {
        given(solarSystem.getSolarSystemId()).willReturn(SOLAR_SYSTEM_ID);
        underTest.add(solarSystem);

        assertThat(underTest.findByIdValidated(SOLAR_SYSTEM_ID)).isEqualTo(solarSystem);
    }

    @Test
    void findByIdValidated_notFound() {
        Throwable ex = catchThrowable(() -> underTest.findByIdValidated(SOLAR_SYSTEM_ID));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND);
    }
}