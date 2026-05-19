package com.github.saphyra.apphub.service.notebook.service;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_file.File;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
@Deprecated(forRemoval = true)
public class FileFactory {
    private final IdGenerator idGenerator;

    public File create(UUID userId, UUID parent, UUID fileId) {
        return File.builder()
            .fileId(idGenerator.randomUuid())
            .userId(userId)
            .parent(parent)
            .storedFileId(fileId)
            .build();
    }
}
