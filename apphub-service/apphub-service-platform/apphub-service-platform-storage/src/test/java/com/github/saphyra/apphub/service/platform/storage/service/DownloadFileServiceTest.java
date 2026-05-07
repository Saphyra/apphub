package com.github.saphyra.apphub.service.platform.storage.service;

import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.service.platform.storage.dao.Storage;
import com.github.saphyra.apphub.service.platform.storage.dao.StoredFile;
import com.github.saphyra.apphub.service.platform.storage.dao.StoredFileDao;
import com.github.saphyra.apphub.service.platform.storage.client.DownloadResult;
import com.github.saphyra.apphub.service.platform.storage.client.StorageClient;
import com.github.saphyra.apphub.service.platform.storage.client.StorageClientProvider;
import com.github.saphyra.apphub.service.platform.storage.client.ftp.FtpClientWrapper;
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
    private StorageClientProvider storageClientProvider;

    @InjectMocks
    private DownloadFileService underTest;

    @Mock
    private StoredFile storedFile;

    @Mock
    private FtpClientWrapper ftpClient;

    @Mock
    private InputStream inputStream;

    @Mock
    private StorageClient storageClient;

    @Mock
    private DownloadResult downloadResult;

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
        given(storedFile.getStorage()).willReturn(Storage.FTP);
        given(storedFile.getFileName()).willReturn(FILE_NAME);

        given(storageClientProvider.getClientForType(Storage.FTP)).willReturn(storageClient);
        given(storageClient.download(STORED_FILE_ID)).willReturn(downloadResult);

        BiWrapper<String, DownloadResult> result = underTest.downloadFile(USER_ID, STORED_FILE_ID);

        assertThat(result.getEntity1()).isEqualTo(FILE_NAME);
        assertThat(result.getEntity2()).isEqualTo(downloadResult);

        verify(ftpClient, times(0)).close();
    }
}