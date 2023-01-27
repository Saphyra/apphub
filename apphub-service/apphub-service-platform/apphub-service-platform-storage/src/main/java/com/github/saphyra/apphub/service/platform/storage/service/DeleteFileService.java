package com.github.saphyra.apphub.service.platform.storage.service;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.platform.storage.dao.StoredFile;
import com.github.saphyra.apphub.service.platform.storage.dao.StoredFileDao;
import com.github.saphyra.apphub.service.platform.storage.ftp.FtpClientFactory;
import com.github.saphyra.apphub.service.platform.storage.ftp.FtpClientWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeleteFileService {
    private final StoredFileDao storedFileDao;
    private final FtpClientFactory ftpClientFactory;
    private final ErrorReporterService errorReporterService;
    private final UuidConverter uuidConverter;

    public void deleteFile(UUID userId, UUID storedFileId) {
        StoredFile storedFile = storedFileDao.findByIdValidated(storedFileId);

        deleteFile(userId, storedFile);
    }

    public void deleteFile(UUID userId, StoredFile storedFile) {
        if (!storedFile.getUserId().equals(userId)) {
            throw ExceptionFactory.forbiddenOperation(userId + " has no access to StoredFile " + storedFile.getStoredFileId());
        }

        try (FtpClientWrapper ftpClient = ftpClientFactory.create()) {
            ftpClient.deleteFile(uuidConverter.convertDomain(storedFile.getStoredFileId()));
        } catch (Exception e) {
            errorReporterService.report("Failed deleting FTP file " + storedFile.getStoredFileId(), e);
        }

        storedFileDao.delete(storedFile);
    }
}
