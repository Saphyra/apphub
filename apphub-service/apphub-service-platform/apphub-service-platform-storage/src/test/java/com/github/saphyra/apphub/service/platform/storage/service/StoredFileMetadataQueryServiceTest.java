package com.github.saphyra.apphub.service.platform.storage.service;

import com.github.saphyra.apphub.api.platform.storage.model.StoredFileResponse;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.service.platform.storage.dao.StoredFile;
import com.github.saphyra.apphub.service.platform.storage.dao.StoredFileDao;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class StoredFileMetadataQueryServiceTest {
    private static final UUID STORED_FILE_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final LocalDateTime CREATED_AT = LocalDateTime.now();
    private static final String EXTENSION = "extension";
    private static final String FILE_NAME = "file-name";
    private static final long SIZE = 546L;
    private static final Long CREATED_AT_EPOCH = 897768L;

    @Mock
    private StoredFileDao storedFileDao;

    @Mock
    private DateTimeUtil dateTimeUtil;

    @InjectMocks
    private StoredFileMetadataQueryService underTest;

    @Mock
    private StoredFile storedFile;

    @Test
    public void forbiddenOperation() {
        given(storedFileDao.findByIdValidated(STORED_FILE_ID)).willReturn(storedFile);
        given(storedFile.getUserId()).willReturn(UUID.randomUUID());

        Throwable ex = catchThrowable(() -> underTest.getMetadata(USER_ID, STORED_FILE_ID));

        ExceptionValidator.validateForbiddenOperation(ex);
    }

    @Test
    public void getMetadata() {
        StoredFile storedFile = StoredFile.builder()
            .storedFileId(STORED_FILE_ID)
            .userId(USER_ID)
            .createdAt(CREATED_AT)
            .extension(EXTENSION)
            .fileName(FILE_NAME)
            .size(SIZE)
            .fileUploaded(true)
            .build();
        given(storedFileDao.findByIdValidated(STORED_FILE_ID)).willReturn(storedFile);
        given(dateTimeUtil.toEpochSecond(CREATED_AT)).willReturn(CREATED_AT_EPOCH);

        StoredFileResponse result = underTest.getMetadata(USER_ID, STORED_FILE_ID);

        assertThat(result.getStoredFileId()).isEqualTo(STORED_FILE_ID);
        assertThat(result.getCreatedAt()).isEqualTo(CREATED_AT_EPOCH);
        assertThat(result.getExtension()).isEqualTo(EXTENSION);
        assertThat(result.getFileName()).isEqualTo(FILE_NAME);
        assertThat(result.getSize()).isEqualTo(SIZE);
        assertThat(result.getFileUploaded()).isTrue();
    }
}