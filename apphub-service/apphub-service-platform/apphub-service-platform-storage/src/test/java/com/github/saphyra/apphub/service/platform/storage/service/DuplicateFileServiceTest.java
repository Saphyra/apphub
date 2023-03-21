package com.github.saphyra.apphub.service.platform.storage.service;

import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBean;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
import com.github.saphyra.apphub.service.platform.storage.dao.StoredFile;
import com.github.saphyra.apphub.service.platform.storage.dao.StoredFileDao;
import com.github.saphyra.apphub.service.platform.storage.ftp.FtpClientWrapper;
import com.github.saphyra.apphub.service.platform.storage.service.store.StoreFileService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.InputStream;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class DuplicateFileServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID STORED_FILE_ID = UUID.randomUUID();
    private static final String FILE_NAME = "file-name";
    private static final Long SIZE = 234L;
    private static final UUID NEW_STORED_FILE_ID = UUID.randomUUID();

    @Mock
    private DownloadFileService downloadFileService;

    @Mock
    private StoreFileService storeFileService;

    @Mock
    private StoredFileDao storedFileDao;

    @Mock
    private ExecutorServiceBean executorServiceBean;

    @Mock
    private AccessTokenProvider accessTokenProvider;

    @InjectMocks
    private DuplicateFileService underTest;

    @Mock
    private DownloadResult downloadResult;

    @Mock
    private StoredFile storedFile;

    @Mock
    private FtpClientWrapper ftpClient;

    @Mock
    private InputStream inputStream;

    @Mock
    private AccessTokenHeader accessTokenHeader;

    @Test
    public void duplicateFile() {
        given(storedFileDao.findByIdValidated(STORED_FILE_ID)).willReturn(storedFile);
        given(storedFile.getFileName()).willReturn(FILE_NAME);
        given(storedFile.getSize()).willReturn(SIZE);

        given(storeFileService.createFile(USER_ID, FILE_NAME, SIZE)).willReturn(NEW_STORED_FILE_ID);
        given(accessTokenProvider.get()).willReturn(accessTokenHeader);

        given(downloadFileService.downloadFile(USER_ID, STORED_FILE_ID)).willReturn(downloadResult);
        given(downloadResult.getInputStream()).willReturn(inputStream);
        given(downloadResult.getFtpClient()).willReturn(ftpClient);

        UUID result = underTest.duplicateFile(USER_ID, STORED_FILE_ID);

        assertThat(result).isEqualTo(NEW_STORED_FILE_ID);

        ArgumentCaptor<Runnable> argumentCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(executorServiceBean).execute(argumentCaptor.capture());
        argumentCaptor.getValue()
            .run();

        verify(accessTokenProvider).set(accessTokenHeader);
        verify(storeFileService).uploadFile(USER_ID, NEW_STORED_FILE_ID, inputStream, 0L);
        verify(ftpClient).close();
        verify(accessTokenProvider).clear();
    }
}