package com.github.saphyra.apphub.service.platform.storage.service;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.service.platform.storage.dao.StoredFile;
import com.github.saphyra.apphub.service.platform.storage.dao.StoredFileDao;
import com.github.saphyra.apphub.service.platform.storage.ftp.FtpClientFactory;
import com.github.saphyra.apphub.service.platform.storage.ftp.FtpClientWrapper;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@RunWith(MockitoJUnitRunner.class)
public class DeleteFileServiceTest {
    private static final UUID STORED_FILE_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String FILE_NAME = "file-name";

    @Mock
    private StoredFileDao storedFileDao;

    @Mock
    private FtpClientFactory ftpClientFactory;

    @Mock
    private ErrorReporterService errorReporterService;

    @Mock
    private UuidConverter uuidConverter;

    @InjectMocks
    private DeleteFileService underTest;

    @Mock
    private StoredFile storedFile;

    @Mock
    private FtpClientWrapper ftpClient;

    @Test
    public void forbiddenOperation() {
        given(storedFileDao.findByIdValidated(STORED_FILE_ID)).willReturn(storedFile);
        given(storedFile.getUserId()).willReturn(UUID.randomUUID());

        Throwable ex = catchThrowable(() -> underTest.deleteFile(USER_ID, STORED_FILE_ID));

        ExceptionValidator.validateForbiddenOperation(ex);
    }

    @Test
    public void deleteFile() {
        given(storedFileDao.findByIdValidated(STORED_FILE_ID)).willReturn(storedFile);
        given(storedFile.getUserId()).willReturn(USER_ID);
        given(ftpClientFactory.create()).willReturn(ftpClient);
        given(uuidConverter.convertDomain(STORED_FILE_ID)).willReturn(FILE_NAME);
        given(storedFile.getStoredFileId()).willReturn(STORED_FILE_ID);

        underTest.deleteFile(USER_ID, STORED_FILE_ID);

        verify(ftpClient).deleteFile(FILE_NAME);
        verify(storedFileDao).delete(storedFile);
        verifyNoInteractions(errorReporterService);
        verify(ftpClient).close();
    }

    @Test
    public void error() {
        given(storedFileDao.findByIdValidated(STORED_FILE_ID)).willReturn(storedFile);
        given(storedFile.getUserId()).willReturn(USER_ID);
        given(ftpClientFactory.create()).willReturn(ftpClient);
        given(storedFile.getStoredFileId()).willReturn(STORED_FILE_ID);
        given(uuidConverter.convertDomain(STORED_FILE_ID)).willReturn(FILE_NAME);
        doThrow(new RuntimeException())
            .when(ftpClient)
            .deleteFile(FILE_NAME);

        underTest.deleteFile(USER_ID, STORED_FILE_ID);

        verify(storedFileDao).delete(storedFile);
        verify(errorReporterService).report(any(), any());
        verify(ftpClient).close();
    }
}