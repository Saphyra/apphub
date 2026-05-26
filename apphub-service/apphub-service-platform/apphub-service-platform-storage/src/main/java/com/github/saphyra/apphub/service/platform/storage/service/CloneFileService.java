package com.github.saphyra.apphub.service.platform.storage.service;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBean;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.platform.storage.client.StorageClientProvider;
import com.github.saphyra.apphub.service.platform.storage.dao.stored_file.Storage;
import com.github.saphyra.apphub.service.platform.storage.dao.stored_file.StoredFile;
import com.github.saphyra.apphub.service.platform.storage.dao.stored_file.StoredFileDao;
import com.github.saphyra.apphub.service.platform.storage.dao.stored_file.StoredFileFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class CloneFileService {
    private final StoredFileDao storedFileDao;
    private final StoredFileFactory storedFileFactory;
    private final StorageClientProvider storageClientProvider;
    private final ExecutorServiceBean executorServiceBean;

    UUID clone(UUID userId, UUID storedFileId) {
        StoredFile storedFile = storedFileDao.findByIdValidated(userId, storedFileId);

        if (storedFile.getStorage() != Storage.S3) {
            throw ExceptionFactory.notLoggedException(HttpStatus.I_AM_A_TEAPOT, ErrorCode.GENERAL_ERROR, storedFileId + " is not stored in S3, cannot be cloned.");
        }

        StoredFile clone = storedFileFactory.clone(storedFile);

        executorServiceBean.execute(() -> storageClientProvider.getClientForType(storedFile.getStorage())
            .clone(storedFileId, clone.getStoredFileId()));

        return clone.getStoredFileId();
    }
}
