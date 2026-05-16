package com.github.saphyra.apphub.service.platform.storage.service.store;

import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.platform.storage.config.StorageProperties;
import com.github.saphyra.apphub.service.platform.storage.config.StoredFileProperties;
import com.github.saphyra.apphub.service.platform.storage.dao.stored_file.StoredFile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class StoredFileFactory {
    private final IdGenerator idGenerator;
    private final DateTimeUtil dateTimeUtil;
    private final StorageProperties storageProperties;
    private final StoredFileProperties storedFileProperties;

    StoredFile create(UUID userId, String fileName, Long size) {
        LocalDateTime currentTime = dateTimeUtil.getCurrentDateTime();
        return StoredFile.builder()
            .storedFileId(idGenerator.randomUuid())
            .userId(userId)
            .createdAt(currentTime)
            .expiration(currentTime.plusSeconds(storedFileProperties.getExpirationSeconds()))
            .fileName(fileName)
            .size(size)
            .storage(storageProperties.getType())
            .build();
    }
}
