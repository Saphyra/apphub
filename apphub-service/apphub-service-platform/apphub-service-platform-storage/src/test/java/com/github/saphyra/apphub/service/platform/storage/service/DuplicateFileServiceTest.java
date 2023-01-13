package com.github.saphyra.apphub.service.platform.storage.service;

import com.github.saphyra.apphub.service.platform.storage.dao.StoredFile;
import com.github.saphyra.apphub.service.platform.storage.ftp.FtpClientWrapper;
import com.github.saphyra.apphub.service.platform.storage.service.store.StoreFileService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.InputStream;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class DuplicateFileServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID STORED_FILE_ID = UUID.randomUUID();
    private static final String FILE_NAME = "file-name";
    private static final String EXTENSION = "extension";
    private static final Long SIZE = 234L;
    private static final UUID NEW_STORED_FILE_ID = UUID.randomUUID();

    @Mock
    private DownloadFileService downloadFileService;

    @Mock
    private StoreFileService storeFileService;

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

    @Test
    public void duplicateFile() {
        given(downloadFileService.downloadFile(USER_ID, STORED_FILE_ID)).willReturn(downloadResult);
        given(downloadResult.getStoredFile()).willReturn(storedFile);
        given(downloadResult.getInputStream()).willReturn(inputStream);
        given(downloadResult.getFtpClient()).willReturn(ftpClient);

        given(storedFile.getFileName()).willReturn(FILE_NAME);
        given(storedFile.getExtension()).willReturn(EXTENSION);
        given(storedFile.getSize()).willReturn(SIZE);

        given(storeFileService.createFile(USER_ID, FILE_NAME, EXTENSION, SIZE)).willReturn(NEW_STORED_FILE_ID);
        given(storeFileService.uploadFile(USER_ID, NEW_STORED_FILE_ID, inputStream)).willReturn(NEW_STORED_FILE_ID);

        UUID result = underTest.duplicateFile(USER_ID, STORED_FILE_ID);

        assertThat(result).isEqualTo(NEW_STORED_FILE_ID);

        verify(ftpClient).close();
    }
}