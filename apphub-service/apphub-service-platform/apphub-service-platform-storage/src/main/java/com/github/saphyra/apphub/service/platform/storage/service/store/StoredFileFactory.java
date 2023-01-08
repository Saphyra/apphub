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
//TODO unit test
class StoredFileFactory {
    private final IdGenerator idGenerator;
    private final DateTimeUtil dateTimeUtil;

    public StoredFile create(UUID userId, String extension, Integer size) {
        return StoredFile.builder()
            .storedFileId(idGenerator.randomUuid())
            .userId(userId)
            .createdAt(dateTimeUtil.getCurrentDateTime())
            .fileUploaded(false)
            .extension(extension)
            .size(size)
            .build();
    }
}
