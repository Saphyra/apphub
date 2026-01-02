package com.github.saphyra.apphub.service.platform.storage.event;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.service.platform.storage.dao.StoredFileDao;
import com.github.saphyra.apphub.service.platform.storage.dao.StoredFileView;
import com.github.saphyra.apphub.service.platform.storage.ftp.FtpClientFactory;
import com.github.saphyra.apphub.service.platform.storage.ftp.FtpClientWrapper;
import org.apache.commons.net.ftp.FTPFile;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class FileCleanupEventProcessorTest {
    private static final String EXISTING_FILE_NAME_STRING = UUID.randomUUID().toString();
    private static final UUID NOT_FOUND_FILE_NAME = UUID.randomUUID();
    private static final String NOT_FOUND_FILE_NAME_STRING = NOT_FOUND_FILE_NAME.toString();
    private static final String UNKNOWN_FILE_NAME = "unknown-file-name";

    @Mock
    private StoredFileDao storedFileDao;

    @Mock
    private FtpClientFactory ftpClientFactory;

    @Mock
    private UuidConverter uuidConverter;

    @InjectMocks
    private FileCleanupEventProcessor underTest;

    @Mock
    private FtpClientWrapper client;

    @Mock
    private StoredFileView viewWithFile;

    @Mock
    private StoredFileView viewWithNoFileUploaded;

    @Mock
    private StoredFileView viewWithFileNotFound;

    @Mock
    private FTPFile matchingFile;

    @Mock
    private FTPFile unknownFile;

    @Test
    void cleanup() {
        given(ftpClientFactory.create()).willReturn(client);
        given(storedFileDao.getAllView()).willReturn(List.of(viewWithFile, viewWithNoFileUploaded, viewWithFileNotFound));
        given(viewWithFile.isFileUploaded()).willReturn(true);
        given(viewWithNoFileUploaded.isFileUploaded()).willReturn(false);
        given(viewWithFileNotFound.isFileUploaded()).willReturn(true);

        given(viewWithFile.getStoredFileId()).willReturn(EXISTING_FILE_NAME_STRING);
        given(viewWithFileNotFound.getStoredFileId()).willReturn(NOT_FOUND_FILE_NAME_STRING);

        given(client.listFiles("/")).willReturn(List.of(matchingFile, unknownFile));
        given(matchingFile.getName()).willReturn(EXISTING_FILE_NAME_STRING);
        given(unknownFile.getName()).willReturn(UNKNOWN_FILE_NAME);
        given(uuidConverter.convertEntity(NOT_FOUND_FILE_NAME_STRING)).willReturn(NOT_FOUND_FILE_NAME);

        underTest.cleanup();

        then(client).should().deleteFile(UNKNOWN_FILE_NAME);
        then(storedFileDao).should().deleteAllById(List.of(NOT_FOUND_FILE_NAME));
        then(client).should().close();
    }
}