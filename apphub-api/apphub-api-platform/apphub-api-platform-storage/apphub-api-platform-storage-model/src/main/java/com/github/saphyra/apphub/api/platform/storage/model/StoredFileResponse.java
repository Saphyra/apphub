package com.github.saphyra.apphub.api.platform.storage.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoredFileResponse {
    private UUID storedFileId;
    private Long createdAt;
    private String extension;
    private String fileName;
    private Long size;
    private Boolean fileUploaded;
}
