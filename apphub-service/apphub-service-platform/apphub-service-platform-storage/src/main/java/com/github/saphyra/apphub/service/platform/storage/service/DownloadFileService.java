package com.github.saphyra.apphub.service.platform.storage.service;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.platform.storage.dao.StoredFile;
import com.github.saphyra.apphub.service.platform.storage.dao.StoredFileDao;
import com.github.saphyra.apphub.service.platform.storage.ftp.FtpClientFactory;
import com.github.saphyra.apphub.service.platform.storage.ftp.FtpClientWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class DownloadFileService {
    private final StoredFileDao storedFileDao;
    private final FtpClientFactory ftpClientFactory;
    private final UuidConverter uuidConverter;

    public DownloadResult downloadFile(UUID userId, UUID storedFileId) {
        StoredFile storedFile = storedFileDao.findByIdValidated(storedFileId);

        if (!storedFile.getUserId().equals(userId)) {
            throw ExceptionFactory.forbiddenOperation(userId + " has no access to " + storedFileId);
        }

        FtpClientWrapper ftpClient = ftpClientFactory.create();

        try {
            InputStream inputStream = ftpClient.downloadFile(uuidConverter.convertDomain(storedFile.getStoredFileId()));
            return new DownloadResult(inputStream, storedFile, ftpClient);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
