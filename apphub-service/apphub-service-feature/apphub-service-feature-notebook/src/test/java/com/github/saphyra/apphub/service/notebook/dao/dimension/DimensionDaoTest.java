package com.github.saphyra.apphub.service.notebook.dao.dimension;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class DimensionDaoTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String USER_ID_STRING = "user-id";
    private static final UUID EXTERNAL_REFERENCE = UUID.randomUUID();
    private static final String EXTERNAL_REFERENCE_STRING = "external-reference";
    private static final UUID DIMENSION_ID = UUID.randomUUID();
    private static final String DIMENSION_ID_STRING = "dimension-id";

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private DimensionConverter converter;

    @Mock
    private DimensionRepository repository;

    @InjectMocks
    private DimensionDao underTest;

    @Mock
    private Dimension domain;

    @Mock
    private DimensionEntity entity;

    @Test
    void deleteByUserId() {
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);

        underTest.deleteByUserId(USER_ID);

        then(repository).should().deleteByUserId(USER_ID_STRING);
    }

    @Test
    void getByExternalReference() {
        given(uuidConverter.convertDomain(EXTERNAL_REFERENCE)).willReturn(EXTERNAL_REFERENCE_STRING);
        given(repository.getByExternalReference(EXTERNAL_REFERENCE_STRING)).willReturn(List.of(entity));
        given(converter.convertEntity(List.of(entity))).willReturn(List.of(domain));

        List<Dimension> result = underTest.getByExternalReference(EXTERNAL_REFERENCE);

        assertThat(result).containsExactly(domain);
    }

    @Test
    void findByIdValidated() {
        given(uuidConverter.convertDomain(DIMENSION_ID)).willReturn(DIMENSION_ID_STRING);
        given(repository.findById(DIMENSION_ID_STRING)).willReturn(Optional.of(entity));
        given(converter.convertEntity(Optional.of(entity))).willReturn(Optional.of(domain));

        Dimension result = underTest.findByIdValidated(DIMENSION_ID);

        assertThat(result).isEqualTo(domain);
    }

    @Test
    void findByIdValidated_notFound() {
        Throwable ex = catchThrowable(() -> underTest.findByIdValidated(DIMENSION_ID));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND);
    }
}