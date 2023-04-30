package com.github.saphyra.apphub.service.skyxplore.game.domain.data.coordinate;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.geometry.Coordinate;
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
class CoordinatesTest {
    private static final UUID REFERENCE_ID = UUID.randomUUID();

    private final Coordinates underTest = new Coordinates();

    @Mock
    private ReferredCoordinate referredCoordinate;

    @Mock
    private Coordinate coordinate;

    @Test
    void findByReferenceId_found() {
        given(referredCoordinate.getReferenceId()).willReturn(REFERENCE_ID);
        given(referredCoordinate.getCoordinate()).willReturn(coordinate);

        underTest.add(referredCoordinate);

        assertThat(underTest.findByReferenceId(REFERENCE_ID)).isEqualTo(coordinate);
    }

    @Test
    void findByReferenceId_notFound() {
        given(referredCoordinate.getReferenceId()).willReturn(UUID.randomUUID());

        underTest.add(referredCoordinate);

        Throwable ex = catchThrowable(() -> underTest.findByReferenceId(REFERENCE_ID));

        ExceptionValidator.validateLoggedException(ex, HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND);
    }
}