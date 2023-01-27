package com.github.saphyra.apphub.service.platform.storage.dao;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class StoredFileDaoTest {
    private static final UUID STORED_FILE_ID = UUID.randomUUID();
    private static final String STORED_FILE_ID_STRING = "stored-file-id";
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String USER_ID_STRING = "user-id-string";
    private static final LocalDateTime EXPIRATION_TIME = LocalDateTime.now();

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private StoredFileRepository repository;

    @Mock
    private StoredFileConverter converter;

    @InjectMocks
    private StoredFileDao underTest;

    @Mock
    private StoredFile storedFile;

    @Mock
    private StoredFileEntity entity;

    @Test
    public void findByIdValidated_notFound() {
        given(uuidConverter.convertDomain(STORED_FILE_ID)).willReturn(STORED_FILE_ID_STRING);
        given(repository.findById(STORED_FILE_ID_STRING)).willReturn(Optional.empty());

        Throwable ex = catchThrowable(() -> underTest.findByIdValidated(STORED_FILE_ID));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND);
    }

    @Test
    public void findByIdValidated() {
        given(uuidConverter.convertDomain(STORED_FILE_ID)).willReturn(STORED_FILE_ID_STRING);
        given(repository.findById(STORED_FILE_ID_STRING)).willReturn(Optional.of(entity));
        given(converter.convertEntity(Optional.of(entity))).willReturn(Optional.of(storedFile));

        StoredFile result = underTest.findByIdValidated(STORED_FILE_ID);

        assertThat(result).isEqualTo(storedFile);
    }

    @Test
    public void findById() {
        given(uuidConverter.convertDomain(STORED_FILE_ID)).willReturn(STORED_FILE_ID_STRING);
        given(repository.findById(STORED_FILE_ID_STRING)).willReturn(Optional.of(entity));
        given(converter.convertEntity(Optional.of(entity))).willReturn(Optional.of(storedFile));

        Optional<StoredFile> result = underTest.findById(STORED_FILE_ID);

        assertThat(result).contains(storedFile);
    }

    @Test
    public void getByUserId() {
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(repository.getByUserId(USER_ID_STRING)).willReturn(List.of(entity));
        given(converter.convertEntity(List.of(entity))).willReturn(List.of(storedFile));

        List<StoredFile> result = underTest.getByUserId(USER_ID);

        assertThat(result).containsExactly(storedFile);
    }

    @Test
    public void deleteExpired() {
        underTest.deleteExpired(EXPIRATION_TIME);

        verify(repository).deleteByFileUploadedAndCreatedAtBefore(false, EXPIRATION_TIME);
    }
}