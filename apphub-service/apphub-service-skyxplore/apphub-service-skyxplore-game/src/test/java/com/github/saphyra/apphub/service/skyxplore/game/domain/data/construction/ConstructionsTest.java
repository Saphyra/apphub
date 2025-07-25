package com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction;

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
class ConstructionsTest {
    private static final UUID EXTERNAL_REFERENCE = UUID.randomUUID();
    private static final UUID CONSTRUCTION_ID = UUID.randomUUID();

    private final Constructions underTest = new Constructions();

    @Mock
    private Construction construction;

    @Test
    void findByExternalReferenceValidated_found() {
        given(construction.getExternalReference()).willReturn(EXTERNAL_REFERENCE);

        underTest.add(construction);

        assertThat(underTest.findByExternalReferenceValidated(EXTERNAL_REFERENCE)).isEqualTo(construction);
    }

    @Test
    void findByExternalReferenceValidated_notFound() {
        given(construction.getExternalReference()).willReturn(UUID.randomUUID());

        underTest.add(construction);

        Throwable ex = catchThrowable(() -> underTest.findByExternalReferenceValidated(EXTERNAL_REFERENCE));

        ExceptionValidator.validateLoggedException(ex, HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND);
    }

    @Test
    void findByConstructionIdValidated_found() {
        given(construction.getConstructionId()).willReturn(CONSTRUCTION_ID);

        underTest.add(construction);

        assertThat(underTest.findByIdValidated(CONSTRUCTION_ID)).isEqualTo(construction);
    }

    @Test
    void findByConstructionIdValidated_notFound() {
        given(construction.getConstructionId()).willReturn(UUID.randomUUID());

        underTest.add(construction);

        Throwable ex = catchThrowable(() -> underTest.findByIdValidated(CONSTRUCTION_ID));

        ExceptionValidator.validateLoggedException(ex, HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND);
    }
}