package com.github.saphyra.apphub.service.platform.storage.service;

import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.platform.storage.dao.stored_file.StoredFile;
import com.github.saphyra.apphub.service.platform.storage.client.DownloadResult;
import com.github.saphyra.apphub.service.platform.storage.client.StorageClientProvider;
import com.github.saphyra.apphub.service.platform.storage.dao.stored_file.StoredFileDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class DownloadFileService {
    private final StoredFileDao storedFileDao;
    private final StorageClientProvider storageClientProvider;

    public BiWrapper<String, DownloadResult> downloadFile(UUID userId, UUID storedFileId) {
        StoredFile storedFile = storedFileDao.findByIdValidated(userId, storedFileId);

        if (!storedFile.getUserId().equals(userId)) {
            throw ExceptionFactory.forbiddenOperation(userId + " has no access to " + storedFileId);
        }

        if (!storedFile.isFileUploaded()) {
            throw ExceptionFactory.notLoggedException(HttpStatus.LOCKED, ErrorCode.FILE_NOT_UPLOADED, storedFileId + " has not file uploaded.");
        }

        DownloadResult downloadResult = storageClientProvider.getClientForType(storedFile.getStorage())
            .download(storedFileId);

        return new BiWrapper<>(storedFile.getFileName(), downloadResult);
    }
}
