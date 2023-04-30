package com.github.saphyra.apphub.service.platform.storage.service.store;

import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.platform.storage.dao.StoredFile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class StoredFileFactory {
    private final IdGenerator idGenerator;
    private final DateTimeUtil dateTimeUtil;

    StoredFile create(UUID userId, String fileName, Long size) {
        return StoredFile.builder()
            .storedFileId(idGenerator.randomUuid())
            .userId(userId)
            .createdAt(dateTimeUtil.getCurrentDateTime())
            .fileUploaded(false)
            .fileName(fileName)
            .size(size)
            .build();
    }
}
