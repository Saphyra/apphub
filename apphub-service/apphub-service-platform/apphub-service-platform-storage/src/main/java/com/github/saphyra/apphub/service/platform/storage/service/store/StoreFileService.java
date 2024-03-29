package com.github.saphyra.apphub.service.platform.storage.service.store;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.CommonConfigProperties;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.platform.storage.dao.StoredFile;
import com.github.saphyra.apphub.service.platform.storage.dao.StoredFileDao;
import com.github.saphyra.apphub.service.platform.storage.ftp.FtpClientFactory;
import com.github.saphyra.apphub.service.platform.storage.ftp.FtpClientWrapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import jakarta.transaction.Transactional;

import java.io.InputStream;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class StoreFileService {
    private final StoredFileFactory storedFileFactory;
    private final StoredFileDao storedFileDao;
    private final FtpClientFactory ftpClientFactory;
    private final UuidConverter uuidConverter;
    private final CommonConfigProperties properties;

    public UUID createFile(UUID userId, String fileName,  Long size) {
        ValidationUtil.notNull(fileName, "fileName");
        ValidationUtil.atLeast(size, 0, "size");
        ValidationUtil.maximum(size, properties.getMaxUploadedFileSize(), "size");

        StoredFile storedFile = storedFileFactory.create(userId, fileName, size);

        storedFileDao.save(storedFile);

        return storedFile.getStoredFileId();
    }

    @SneakyThrows
    @Transactional
    public void uploadFile(UUID userId, UUID storedFileId, InputStream file, Long size) {
        ValidationUtil.maximum(size, properties.getMaxUploadedFileSize(), "size");

        StoredFile storedFile = storedFileDao.findByIdValidated(storedFileId);

        if (!userId.equals(storedFile.getUserId())) {
            throw ExceptionFactory.forbiddenOperation(userId + " has no access to StoredFile " + storedFileId);
        }

        if (storedFile.isFileUploaded()) {
            throw ExceptionFactory.notLoggedException(HttpStatus.CONFLICT, ErrorCode.ALREADY_EXISTS, "File already uploaded for StoredFile " + storedFile);
        }

        try (FtpClientWrapper ftpClient = ftpClientFactory.create()) {

            ftpClient.storeFile(uuidConverter.convertDomain(storedFileId), file);

            storedFile.setFileUploaded(true);

            storedFileDao.save(storedFile);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
