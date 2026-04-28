package com.github.saphyra.apphub.service.platform.storage.service;

import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.platform.storage.dao.StoredFile;
import com.github.saphyra.apphub.service.platform.storage.dao.StoredFileDao;
import com.github.saphyra.apphub.service.platform.storage.client.StorageClientProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeleteFileService {
    private final StoredFileDao storedFileDao;
    private final StorageClientProvider storageClientProvider;

    public void deleteFile(UUID userId, UUID storedFileId) {
        storedFileDao.findById(storedFileId)
            .ifPresent(storedFile -> deleteFile(userId, storedFile));
    }

    public void deleteFile(UUID userId, StoredFile storedFile) {
        if (!storedFile.getUserId().equals(userId)) {
            throw ExceptionFactory.forbiddenOperation(userId + " has no access to StoredFile " + storedFile.getStoredFileId());
        }

        storageClientProvider.getClientForType(storedFile.getStorage())
            .delete(storedFile.getStoredFileId());

        storedFileDao.delete(storedFile);
    }
}
