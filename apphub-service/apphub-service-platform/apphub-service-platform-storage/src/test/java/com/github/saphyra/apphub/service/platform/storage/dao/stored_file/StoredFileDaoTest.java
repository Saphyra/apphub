package com.github.saphyra.apphub.service.platform.storage.dao.stored_file;

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
class StoredFileDaoTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID STORED_FILE_ID = UUID.randomUUID();
    private static final String USER_ID_STRING = "user-id";
    private static final String STORED_FILE_ID_STRING = "stored-file-id";

    @Mock
    private StoredFileRepository repository;

    @Mock
    private StoredFileConverter converter;

    @Mock
    private UuidConverter uuidConverter;

    @InjectMocks
    private StoredFileDao underTest;

    @Mock
    private StoredFileEntity entity;

    @Mock
    private StoredFile domain;

    @Test
    void getByUserId() {
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(repository.getByUserId(USER_ID_STRING)).willReturn(List.of(entity));
        given(converter.convertEntity(List.of(entity))).willReturn(List.of(domain));

        assertThat(underTest.getByUserId(USER_ID)).containsExactly(domain);
    }

    @Test
    void findByIdValidated_notFound() {
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(uuidConverter.convertDomain(STORED_FILE_ID)).willReturn(STORED_FILE_ID_STRING);
        given(repository.findById(USER_ID_STRING, STORED_FILE_ID_STRING)).willReturn(Optional.empty());
        given(converter.convertEntity(Optional.empty())).willReturn(Optional.empty());

        Throwable ex = catchThrowable(() -> underTest.findByIdValidated(USER_ID, STORED_FILE_ID));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND);
    }

    @Test
    void findByIdValidated() {
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(uuidConverter.convertDomain(STORED_FILE_ID)).willReturn(STORED_FILE_ID_STRING);
        given(repository.findById(USER_ID_STRING, STORED_FILE_ID_STRING)).willReturn(Optional.of(entity));
        given(converter.convertEntity(Optional.of(entity))).willReturn(Optional.of(domain));

        assertThat(underTest.findByIdValidated(USER_ID, STORED_FILE_ID)).isEqualTo(domain);
    }

    @Test
    void findById() {
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(uuidConverter.convertDomain(STORED_FILE_ID)).willReturn(STORED_FILE_ID_STRING);
        given(repository.findById(USER_ID_STRING, STORED_FILE_ID_STRING)).willReturn(Optional.of(entity));
        given(converter.convertEntity(Optional.of(entity))).willReturn(Optional.of(domain));

        assertThat(underTest.findById(USER_ID, STORED_FILE_ID)).contains(domain);
    }

    @Test
    void delete() {
        given(domain.getUserId()).willReturn(USER_ID);
        given(domain.getStoredFileId()).willReturn(STORED_FILE_ID);
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(uuidConverter.convertDomain(STORED_FILE_ID)).willReturn(STORED_FILE_ID_STRING);

        underTest.delete(domain);

        then(repository).should().delete(USER_ID_STRING, STORED_FILE_ID_STRING);
    }

    @Test
    void save() {
        given(converter.convertDomain(domain)).willReturn(entity);

        underTest.save(domain);

        then(repository).should().save(entity);
    }
}