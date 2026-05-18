package com.github.saphyra.apphub.service.platform.storage.service;

import com.github.saphyra.apphub.lib.common_domain.AccessToken;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
import com.github.saphyra.apphub.service.platform.storage.client.DownloadResult;
import com.github.saphyra.apphub.service.platform.storage.client.ftp.FtpStorageClient;
import com.github.saphyra.apphub.service.platform.storage.client.s3.S3StorageClient;
import com.github.saphyra.apphub.service.platform.storage.dao.stored_file.Storage;
import com.github.saphyra.apphub.service.platform.storage.dao.stored_file.StoredFile;
import com.github.saphyra.apphub.service.platform.storage.dao.stored_file.StoredFileDao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.InputStream;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class FtpToS3MigrationServiceTest {
    private static final UUID STORED_FILE_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final long SIZE = 12345L;

    @Mock
    private StoredFileDao storedFileDao;

    @Mock
    private S3StorageClient s3StorageClient;

    @Mock
    private FtpStorageClient ftpStorageClient;

    @Mock
    private ErrorReporterService errorReporterService;

    @Mock
    private AccessTokenProvider accessTokenProvider;

    @InjectMocks
    private FtpToS3MigrationService underTest;

    @Mock
    private StoredFile storedFile;

    @Mock
    private DownloadResult downloadResult;

    @Mock
    private InputStream inputStream;

    @Test
    void migrate_success() throws Exception {
        given(storedFileDao.getFtpFileIds()).willReturn(List.of(new BiWrapper<>(STORED_FILE_ID, USER_ID)));
        AccessToken accessToken = AccessToken.builder()
            .userId(USER_ID)
            .build();
        given(accessTokenProvider.set(accessToken)).willReturn(accessTokenProvider);
        given(ftpStorageClient.download(STORED_FILE_ID)).willReturn(downloadResult);
        given(storedFileDao.findByIdValidated(USER_ID, STORED_FILE_ID)).willReturn(storedFile);
        given(storedFile.getSize()).willReturn(SIZE);
        given(downloadResult.getInputStream()).willReturn(inputStream);

        underTest.migrate();

        then(s3StorageClient).should().upload(STORED_FILE_ID, inputStream, SIZE);
        then(ftpStorageClient).should().delete(STORED_FILE_ID);
        then(storedFile).should().setStorage(Storage.S3);
        then(storedFileDao).should().save(storedFile);
        then(downloadResult).should().close();
        then(accessTokenProvider).should().close();
    }

    @Test
    void migrate_errorReported() {
        given(storedFileDao.getFtpFileIds()).willReturn(List.of(new BiWrapper<>(STORED_FILE_ID, USER_ID)));
        given(accessTokenProvider.set(any(AccessToken.class))).willReturn(accessTokenProvider);
        RuntimeException exception = new RuntimeException("test error");
        given(ftpStorageClient.download(STORED_FILE_ID)).willThrow(exception);

        underTest.migrate();

        then(errorReporterService).should().report(eq("Failed migrating StoredFile " + STORED_FILE_ID), eq(exception));
        then(s3StorageClient).shouldHaveNoInteractions();
        then(storedFileDao).shouldHaveNoMoreInteractions();
    }
}