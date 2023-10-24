package com.github.saphyra.apphub.service.notebook.dao.column_type;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class ColumnTypeDaoTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String USER_ID_STRING = "user-id";
    private static final UUID COLUMN_ID = UUID.randomUUID();
    private static final String COLUMN_ID_STRING = "column-id";

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private ColumnTypeConverter converter;

    @Mock
    private ColumnTypeRepository repository;

    @InjectMocks
    private ColumnTypeDao underTest;

    @Mock
    private ColumnTypeDto domain;

    @Mock
    private ColumnTypeEntity entity;

    @Test
    void deleteByUserId() {
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);

        underTest.deleteByUserId(USER_ID);

        then(repository).should().deleteByUserId(USER_ID_STRING);
    }

    @Test
    void findByIdValidated() {
        given(uuidConverter.convertDomain(COLUMN_ID)).willReturn(COLUMN_ID_STRING);
        given(repository.findById(COLUMN_ID_STRING)).willReturn(Optional.of(entity));
        given(converter.convertEntity(Optional.of(entity))).willReturn(Optional.of(domain));

        ColumnTypeDto result = underTest.findByIdValidated(COLUMN_ID);

        assertThat(result).isEqualTo(domain);
    }

    @Test
    void findByIdValidated_notFound() {
        Throwable ex = catchThrowable(() -> underTest.findByIdValidated(COLUMN_ID));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND);
    }

    @Test
    void deleteById() {
        given(uuidConverter.convertDomain(COLUMN_ID)).willReturn(COLUMN_ID_STRING);
        given(repository.existsById(COLUMN_ID_STRING)).willReturn(true);

        underTest.deleteById(COLUMN_ID);

        then(repository).should().deleteById(COLUMN_ID_STRING);
    }
}