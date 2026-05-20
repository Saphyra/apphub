package com.github.saphyra.apphub.service.platform.storage.dao.stored_file;

import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.platform.storage.config.StorageProperties;
import com.github.saphyra.apphub.service.platform.storage.config.StoredFileProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class StoredFileFactory {
    private final IdGenerator idGenerator;
    private final DateTimeUtil dateTimeUtil;
    private final StorageProperties storageProperties;
    private final StoredFileProperties storedFileProperties;

    //TODO unit test
    public StoredFile clone(StoredFile storedFile) {
        return create(storedFile.getUserId(), storedFile.getFileName(), storedFile.getSize(), storedFile.getCreatedAt(), null, storedFile.getStorage());
    }

    public StoredFile create(UUID userId, String fileName, Long size) {
        LocalDateTime currentTime = dateTimeUtil.getCurrentDateTime();
        LocalDateTime expiration = currentTime.plusSeconds(storedFileProperties.getExpirationSeconds());
        Storage storage = storageProperties.getType();
        return create(userId, fileName, size, currentTime, expiration, storage);
    }

    private StoredFile create(UUID userId, String fileName, Long size, LocalDateTime currentTime, LocalDateTime expiration, Storage storage) {
        return StoredFile.builder()
            .storedFileId(idGenerator.randomUuid())
            .userId(userId)
            .createdAt(currentTime)
            .expiration(expiration)
            .fileName(fileName)
            .size(size)
            .storage(storage)
            .build();
    }
}
