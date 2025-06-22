package com.github.saphyra.apphub.service.skyxplore.game.domain.data.coordinate;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.geometry.Coordinate;
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
class CoordinatesTest {
    private static final UUID REFERENCE_ID_1 = UUID.randomUUID();
    private static final UUID REFERENCE_ID_2 = UUID.randomUUID();

    private final Coordinates underTest = new Coordinates();

    @Mock
    private ReferredCoordinate referredCoordinate1;

    @Mock
    private ReferredCoordinate referredCoordinate2;

    @Mock
    private Coordinate coordinate1;

    @Test
    void findByReferenceId_found() {
        given(referredCoordinate1.getReferenceId()).willReturn(REFERENCE_ID_1);
        given(referredCoordinate1.getCoordinate()).willReturn(coordinate1);

        underTest.add(referredCoordinate1);

        assertThat(underTest.findByReferenceIdValidated(REFERENCE_ID_1)).isEqualTo(coordinate1);
    }

    @Test
    void findByReferenceId_notFound() {
        given(referredCoordinate1.getReferenceId()).willReturn(UUID.randomUUID());

        underTest.add(referredCoordinate1);

        Throwable ex = catchThrowable(() -> underTest.findByReferenceIdValidated(REFERENCE_ID_1));

        ExceptionValidator.validateLoggedException(ex, HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND);
    }

    @Test
    void getByReferenceId() {
        underTest.addAll(List.of(referredCoordinate1, referredCoordinate2));

        given(referredCoordinate1.getReferenceId()).willReturn(REFERENCE_ID_1);
        given(referredCoordinate2.getReferenceId()).willReturn(REFERENCE_ID_2);

        assertThat(underTest.getByReferenceId(REFERENCE_ID_1)).containsExactly(referredCoordinate1);

    }
}