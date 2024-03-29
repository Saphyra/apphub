package com.github.saphyra.apphub.service.platform.storage.service.store;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.CommonConfigProperties;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.service.platform.storage.dao.StoredFile;
import com.github.saphyra.apphub.service.platform.storage.dao.StoredFileDao;
import com.github.saphyra.apphub.service.platform.storage.ftp.FtpClientFactory;
import com.github.saphyra.apphub.service.platform.storage.ftp.FtpClientWrapper;
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
    private static final Long SIZE = 2345L;
    private static final UUID STORED_FILE_ID = UUID.randomUUID();

    @Mock
    private StoredFileFactory storedFileFactory;

    @Mock
    private StoredFileDao storedFileDao;

    @Mock
    private FtpClientFactory ftpClientFactory;

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private CommonConfigProperties properties;

    @InjectMocks
    private StoreFileService underTest;

    @Mock
    private StoredFile storedFile;

    @Mock
    private InputStream inputStream;

    @Mock
    private FtpClientWrapper ftpClient;

    @Test
    public void createFile_nullFileName() {
        Throwable ex = catchThrowable(() -> underTest.createFile(USER_ID, null, SIZE));

        ExceptionValidator.validateInvalidParam(ex, "fileName", "must not be null");
    }

    @Test
    public void createFile_nullSize() {
        Throwable ex = catchThrowable(() -> underTest.createFile(USER_ID, FILE_NAME, null));

        ExceptionValidator.validateInvalidParam(ex, "size", "must not be null");
    }

    @Test
    public void createFile_tooHighSize() {
        given(properties.getMaxUploadedFileSize()).willReturn(SIZE + 1);

        Throwable ex = catchThrowable(() -> underTest.createFile(USER_ID, FILE_NAME, SIZE + 2));

        ExceptionValidator.validateInvalidParam(ex, "size", "too high");
    }

    @Test
    public void createFile() {
        given(properties.getMaxUploadedFileSize()).willReturn(SIZE + 1);

        given(storedFileFactory.create(USER_ID, FILE_NAME, SIZE)).willReturn(storedFile);
        given(storedFile.getStoredFileId()).willReturn(STORED_FILE_ID);

        UUID result = underTest.createFile(USER_ID, FILE_NAME, SIZE);

        verify(storedFileDao).save(storedFile);

        assertThat(result).isEqualTo(STORED_FILE_ID);
    }

    @Test
    public void uploadFile_tooBig() {
        Throwable ex = catchThrowable(() -> underTest.uploadFile(USER_ID, STORED_FILE_ID, inputStream, SIZE + 2));

        ExceptionValidator.validateInvalidParam(ex, "size", "too high");
    }

    @Test
    public void uploadFile_forbiddenOperation() {
        given(storedFileDao.findByIdValidated(STORED_FILE_ID)).willReturn(storedFile);

        given(storedFile.getUserId()).willReturn(UUID.randomUUID());

        Throwable ex = catchThrowable(() -> underTest.uploadFile(USER_ID, STORED_FILE_ID, inputStream, 0L));

        ExceptionValidator.validateForbiddenOperation(ex);
    }

    @Test
    public void uploadFile_alreadyUploaded() {
        given(storedFileDao.findByIdValidated(STORED_FILE_ID)).willReturn(storedFile);

        given(storedFile.getUserId()).willReturn(USER_ID);
        given(storedFile.isFileUploaded()).willReturn(true);

        Throwable ex = catchThrowable(() -> underTest.uploadFile(USER_ID, STORED_FILE_ID, inputStream, 0L));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.CONFLICT, ErrorCode.ALREADY_EXISTS);
    }

    @Test
    public void uploadFile() {
        given(storedFileDao.findByIdValidated(STORED_FILE_ID)).willReturn(storedFile);

        given(storedFile.getUserId()).willReturn(USER_ID);
        given(storedFile.isFileUploaded()).willReturn(false);

        given(ftpClientFactory.create()).willReturn(ftpClient);
        given(uuidConverter.convertDomain(STORED_FILE_ID)).willReturn(FILE_NAME);

        underTest.uploadFile(USER_ID, STORED_FILE_ID, inputStream, 0L);

        verify(ftpClient).storeFile(FILE_NAME, inputStream);
        verify(storedFile).setFileUploaded(true);
        verify(storedFileDao).save(storedFile);
        verify(ftpClient).close();
    }
}