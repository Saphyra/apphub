package com.github.saphyra.apphub.service.platform.storage.dao.stored_file;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

import static java.util.Objects.isNull;

@AllArgsConstructor
@Data
@Builder
public class StoredFile {
    private final UUID storedFileId;
    private final UUID userId;
    private final LocalDateTime createdAt;
    private final String fileName;
    private final long size;
    private LocalDateTime expiration;
    private Storage storage;

    public boolean isFileUploaded() {
        return isNull(expiration);
    }
}
