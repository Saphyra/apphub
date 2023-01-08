package com.github.saphyra.apphub.service.platform.storage.dao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@Data
@Builder
public class StoredFile {
    private final UUID storedFileId;
    private final UUID userId;
    private final LocalDateTime createdAt;
    private final String extension;
    private final int size;
    private boolean fileUploaded;
}
