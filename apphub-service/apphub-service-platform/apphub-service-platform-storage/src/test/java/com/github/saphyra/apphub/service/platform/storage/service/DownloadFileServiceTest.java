package com.github.saphyra.apphub.service.platform.storage.service;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class DownloadFileServiceTest {
    private static final UUID STORED_FILE_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String FILE_NAME = "file-name";

    @Mock
    private StoredFileDao storedFileDao;

    @Mock
    private FtpClientFactory ftpClientFactory;

    @Mock
    private UuidConverter uuidConverter;

    @InjectMocks
    private DownloadFileService underTest;

    @Mock
    private StoredFile storedFile;

    @Mock
    private FtpClientWrapper ftpClient;

    @Mock
    private InputStream inputStream;

    @Test
    public void downloadFile_forbiddenOperation() {
        given(storedFileDao.findByIdValidated(STORED_FILE_ID)).willReturn(storedFile);

        given(storedFile.getUserId()).willReturn(UUID.randomUUID());

        Throwable ex = catchThrowable(() -> underTest.downloadFile(USER_ID, STORED_FILE_ID));

        ExceptionValidator.validateForbiddenOperation(ex);
    }


    @Test
    public void downloadFile_noFileUploaded() {
        given(storedFileDao.findByIdValidated(STORED_FILE_ID)).willReturn(storedFile);

        given(storedFile.getUserId()).willReturn(USER_ID);
        given(storedFile.isFileUploaded()).willReturn(false);

        Throwable ex = catchThrowable(() -> underTest.downloadFile(USER_ID, STORED_FILE_ID));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.LOCKED, ErrorCode.FILE_NOT_UPLOADED);
    }

    @Test
    public void download() {
        given(storedFileDao.findByIdValidated(STORED_FILE_ID)).willReturn(storedFile);

        given(storedFile.getUserId()).willReturn(USER_ID);
        given(storedFile.isFileUploaded()).willReturn(true);

        given(ftpClientFactory.create()).willReturn(ftpClient);
        given(uuidConverter.convertDomain(STORED_FILE_ID)).willReturn(FILE_NAME);
        given(ftpClient.downloadFile(FILE_NAME)).willReturn(inputStream);

        DownloadResult result = underTest.downloadFile(USER_ID, STORED_FILE_ID);

        assertThat(result.getInputStream()).isEqualTo(inputStream);
        assertThat(result.getStoredFile()).isEqualTo(storedFile);
        assertThat(result.getFtpClient()).isEqualTo(ftpClient);

        verify(ftpClient, times(0)).close();
    }
}