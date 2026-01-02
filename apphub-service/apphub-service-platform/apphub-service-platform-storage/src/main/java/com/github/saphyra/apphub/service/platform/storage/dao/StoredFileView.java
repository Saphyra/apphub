package com.github.saphyra.apphub.service.platform.storage.dao;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class StoredFileView {
    @Id
    private String storedFileId;
    private boolean fileUploaded;
}
