package com.github.saphyra.apphub.service.platform.storage.service;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.CommonConfigProperties;
import com.github.saphyra.apphub.service.platform.storage.client.StorageClient;
import com.github.saphyra.apphub.service.platform.storage.client.StorageClientProvider;
import com.github.saphyra.apphub.service.platform.storage.dao.stored_file.Storage;
import com.github.saphyra.apphub.service.platform.storage.dao.stored_file.StoredFile;
import com.github.saphyra.apphub.service.platform.storage.dao.stored_file.StoredFileDao;
import com.github.saphyra.apphub.service.platform.storage.dao.stored_file.StoredFileFactory;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.io.InputStream;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class StoreFileServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String FILE_NAME = "file-name";
    private static final UUID STORED_FILE_ID = UUID.randomUUID();
    private static final long FILE_SIZE = 3245L;

    @Mock
    private StoredFileFactory storedFileFactory;

    @Mock
    private StoredFileDao storedFileDao;

    @Mock
    private CommonConfigProperties properties;

    @Mock
    private StorageClientProvider storageClientProvider;

    @InjectMocks
    private StoreFileService underTest;

    @Mock
    private StoredFile storedFile;

    @Mock
    private InputStream inputStream;

    @Mock
    private StorageClient storageClient;

    @Test
    public void createFile_nullFileName() {
        Throwable ex = catchThrowable(() -> underTest.createFile(USER_ID, null, FILE_SIZE));

        ExceptionValidator.validateInvalidParam(ex, "fileName", "must not be null");
    }

    @Test
    public void createFile_nullSize() {
        Throwable ex = catchThrowable(() -> underTest.createFile(USER_ID, FILE_NAME, null));

        ExceptionValidator.validateInvalidParam(ex, "size", "must not be null");
    }

    @Test
    public void createFile_tooHighSize() {
        given(properties.getMaxUploadedFileSize()).willReturn(FILE_SIZE + 1);

        Throwable ex = catchThrowable(() -> underTest.createFile(USER_ID, FILE_NAME, FILE_SIZE + 2));

        ExceptionValidator.validateInvalidParam(ex, "size", "too high");
    }

    @Test
    public void createFile() {
        given(properties.getMaxUploadedFileSize()).willReturn(FILE_SIZE + 1);

        given(storedFileFactory.create(USER_ID, FILE_NAME, FILE_SIZE)).willReturn(storedFile);
        given(storedFile.getStoredFileId()).willReturn(STORED_FILE_ID);

        UUID result = underTest.createFile(USER_ID, FILE_NAME, FILE_SIZE);

        verify(storedFileDao).save(storedFile);

        assertThat(result).isEqualTo(STORED_FILE_ID);
    }

    @Test
    public void uploadFile_tooBig() {
        Throwable ex = catchThrowable(() -> underTest.uploadFile(USER_ID, STORED_FILE_ID, inputStream, FILE_SIZE + 2));

        ExceptionValidator.validateInvalidParam(ex, "size", "too high");
    }

    @Test
    public void uploadFile_forbiddenOperation() {
        given(storedFileDao.findByIdValidated(USER_ID, STORED_FILE_ID)).willReturn(storedFile);

        given(storedFile.getUserId()).willReturn(UUID.randomUUID());

        Throwable ex = catchThrowable(() -> underTest.uploadFile(USER_ID, STORED_FILE_ID, inputStream, 0L));

        ExceptionValidator.validateForbiddenOperation(ex);
    }

    @Test
    public void uploadFile_alreadyUploaded() {
        given(storedFileDao.findByIdValidated(USER_ID, STORED_FILE_ID)).willReturn(storedFile);

        given(storedFile.getUserId()).willReturn(USER_ID);
        given(storedFile.isFileUploaded()).willReturn(true);

        Throwable ex = catchThrowable(() -> underTest.uploadFile(USER_ID, STORED_FILE_ID, inputStream, 0L));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.CONFLICT, ErrorCode.ALREADY_EXISTS);
    }

    @Test
    public void uploadFile() {
        given(properties.getMaxUploadedFileSize()).willReturn(FILE_SIZE + 1);

        given(storedFileDao.findByIdValidated(USER_ID, STORED_FILE_ID)).willReturn(storedFile);

        given(storedFile.getUserId()).willReturn(USER_ID);
        given(storedFile.isFileUploaded()).willReturn(false);
        given(storedFile.getStorage()).willReturn(Storage.FTP);

        given(storageClientProvider.getClientForType(Storage.FTP)).willReturn(storageClient);

        underTest.uploadFile(USER_ID, STORED_FILE_ID, inputStream, FILE_SIZE);

        verify(storageClient).upload(STORED_FILE_ID, inputStream, FILE_SIZE);
        verify(storedFile).setExpiration(null);
        verify(storedFileDao).save(storedFile);
    }
}