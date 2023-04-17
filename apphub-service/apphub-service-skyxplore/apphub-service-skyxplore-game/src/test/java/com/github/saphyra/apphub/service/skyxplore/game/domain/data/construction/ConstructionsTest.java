package com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction;

import com.github.saphyra.apphub.api.skyxplore.model.game.ConstructionType;
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
class ConstructionsTest {
    private static final UUID EXTERNAL_REFERENCE = UUID.randomUUID();
    private static final UUID CONSTRUCTION_ID = UUID.randomUUID();
    private static final UUID LOCATION = UUID.randomUUID();

    private final Constructions underTest = new Constructions();

    @Mock
    private Construction construction1;

    @Mock
    private Construction construction2;

    @Mock
    private Construction construction3;

    @Test
    void findByExternalReferenceValidated_found() {
        given(construction1.getExternalReference()).willReturn(EXTERNAL_REFERENCE);

        underTest.add(construction1);

        assertThat(underTest.findByExternalReferenceValidated(EXTERNAL_REFERENCE)).isEqualTo(construction1);
    }

    @Test
    void findByExternalReferenceValidated_notFound() {
        given(construction1.getExternalReference()).willReturn(UUID.randomUUID());

        underTest.add(construction1);

        Throwable ex = catchThrowable(() -> underTest.findByExternalReferenceValidated(EXTERNAL_REFERENCE));

        ExceptionValidator.validateLoggedException(ex, HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND);
    }

    @Test
    void getByLocationAndType() {
        given(construction1.getLocation()).willReturn(LOCATION);
        given(construction1.getConstructionType()).willReturn(ConstructionType.CONSTRUCTION);

        given(construction2.getLocation()).willReturn(UUID.randomUUID());

        given(construction3.getLocation()).willReturn(LOCATION);
        given(construction3.getConstructionType()).willReturn(ConstructionType.TERRAFORMATION);

        underTest.addAll(List.of(construction1, construction2, construction3));

        assertThat(underTest.getByLocationAndType(LOCATION, ConstructionType.CONSTRUCTION)).containsExactly(construction1);
    }

    @Test
    void findByConstructionIdValidated_found() {
        given(construction1.getConstructionId()).willReturn(CONSTRUCTION_ID);

        underTest.add(construction1);

        assertThat(underTest.findByConstructionIdValidated(CONSTRUCTION_ID)).isEqualTo(construction1);
    }

    @Test
    void findByConstructionIdValidated_notFound() {
        given(construction1.getConstructionId()).willReturn(UUID.randomUUID());

        underTest.add(construction1);

        Throwable ex = catchThrowable(() -> underTest.findByConstructionIdValidated(CONSTRUCTION_ID));

        ExceptionValidator.validateLoggedException(ex, HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND);
    }
}