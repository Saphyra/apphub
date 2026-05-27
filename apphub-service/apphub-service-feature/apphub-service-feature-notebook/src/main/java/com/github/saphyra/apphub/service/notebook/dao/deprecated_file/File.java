package com.github.saphyra.apphub.service.notebook.dao.deprecated_file;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@AllArgsConstructor
@Data
@Builder
@Deprecated(forRemoval = true)
public class File {
    private final UUID fileId;
    private final UUID userId;
    private final UUID parent;
    private UUID storedFileId;
}
