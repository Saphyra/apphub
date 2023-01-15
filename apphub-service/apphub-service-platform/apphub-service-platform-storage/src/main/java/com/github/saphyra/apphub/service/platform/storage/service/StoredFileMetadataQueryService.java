package com.github.saphyra.apphub.service.platform.storage.service;

import com.github.saphyra.apphub.api.platform.storage.model.StoredFileResponse;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.platform.storage.dao.StoredFile;
import com.github.saphyra.apphub.service.platform.storage.dao.StoredFileDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class StoredFileMetadataQueryService {
    private final StoredFileDao storedFileDao;
    private final DateTimeUtil dateTimeUtil;

    StoredFileResponse getMetadata(UUID userId, UUID storedFileId) {
        StoredFile storedFile = storedFileDao.findByIdValidated(storedFileId);

        if (!storedFile.getUserId().equals(userId)) {
            throw ExceptionFactory.forbiddenOperation(userId + " has no access to StoredFile " + storedFile);
        }

        return StoredFileResponse.builder()
            .storedFileId(storedFileId)
            .createdAt(dateTimeUtil.toEpochSecond(storedFile.getCreatedAt()))
            .extension(storedFile.getExtension())
            .fileName(storedFile.getFileName())
            .size(storedFile.getSize())
            .fileUploaded(storedFile.isFileUploaded())
            .build();
    }
}
