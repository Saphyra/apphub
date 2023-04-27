package com.github.saphyra.apphub.service.skyxplore.game.domain.data.deconstruction;

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
class DeconstructionsTest {
    private static final UUID EXTERNAL_REFERENCE = UUID.randomUUID();
    private static final UUID DECONSTRUCTION_ID = UUID.randomUUID();
    private static final UUID LOCATION = UUID.randomUUID();

    private final Deconstructions underTest = new Deconstructions();

    @Mock
    private Deconstruction deconstruction1;

    @Mock
    private Deconstruction deconstruction2;

    @Test
    void findByExternalReferenceValidated_found() {
        given(deconstruction1.getExternalReference()).willReturn(EXTERNAL_REFERENCE);

        underTest.add(deconstruction1);

        assertThat(underTest.findByExternalReferenceValidated(EXTERNAL_REFERENCE)).isEqualTo(deconstruction1);
    }

    @Test
    void findByExternalReferenceValidated_notFound() {
        given(deconstruction1.getExternalReference()).willReturn(UUID.randomUUID());

        underTest.add(deconstruction1);

        Throwable ex = catchThrowable(() -> underTest.findByExternalReferenceValidated(EXTERNAL_REFERENCE));

        ExceptionValidator.validateLoggedException(ex, HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND);
    }

    @Test
    void findByDeconstructionId_found() {
        given(deconstruction1.getDeconstructionId()).willReturn(DECONSTRUCTION_ID);

        underTest.add(deconstruction1);

        assertThat(underTest.findByDeconstructionId(DECONSTRUCTION_ID)).isEqualTo(deconstruction1);
    }

    @Test
    void findByDeconstructionId_notFound() {
        given(deconstruction1.getDeconstructionId()).willReturn(UUID.randomUUID());

        underTest.add(deconstruction1);

        Throwable ex = catchThrowable(() -> underTest.findByDeconstructionId(DECONSTRUCTION_ID));

        ExceptionValidator.validateLoggedException(ex, HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND);
    }

    @Test
    void getByLocation() {
        given(deconstruction1.getLocation()).willReturn(LOCATION);
        given(deconstruction2.getLocation()).willReturn(UUID.randomUUID());

        underTest.addAll(List.of(deconstruction1, deconstruction2));

        assertThat(underTest.getByLocation(LOCATION)).containsExactly(deconstruction1);
    }
}