package com.github.saphyra.apphub.service.platform.storage.service;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.CommonConfigProperties;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.platform.storage.client.StorageClientProvider;
import com.github.saphyra.apphub.service.platform.storage.dao.stored_file.StoredFile;
import com.github.saphyra.apphub.service.platform.storage.dao.stored_file.StoredFileDao;
import com.github.saphyra.apphub.service.platform.storage.dao.stored_file.StoredFileFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class StoreFileService {
    private final StoredFileFactory storedFileFactory;
    private final StoredFileDao storedFileDao;
    private final CommonConfigProperties properties;
    private final StorageClientProvider storageClientProvider;

    UUID createFile(UUID userId, String fileName, Long size) {
        ValidationUtil.notNull(fileName, "fileName");
        ValidationUtil.atLeast(size, 0, "size");
        ValidationUtil.maximum(size, properties.getMaxUploadedFileSize(), "size");

        StoredFile storedFile = storedFileFactory.create(userId, fileName, size);

        storedFileDao.save(storedFile);

        return storedFile.getStoredFileId();
    }

    void uploadFile(UUID userId, UUID storedFileId, InputStream file, Long size) {
        ValidationUtil.maximum(size, properties.getMaxUploadedFileSize(), "size");

        StoredFile storedFile = storedFileDao.findByIdValidated(userId, storedFileId);

        if (!userId.equals(storedFile.getUserId())) {
            throw ExceptionFactory.forbiddenOperation(userId + " has no access to StoredFile " + storedFileId);
        }

        if (storedFile.isFileUploaded()) {
            throw ExceptionFactory.notLoggedException(HttpStatus.CONFLICT, ErrorCode.ALREADY_EXISTS, "File already uploaded for StoredFile " + storedFile);
        }

        storageClientProvider.getClientForType(storedFile.getStorage())
            .upload(storedFileId, file, size);

        storedFile.setExpiration(null);
        storedFileDao.save(storedFile);
    }
}
