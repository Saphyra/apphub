package com.github.saphyra.apphub.service.platform.storage.dao.deprecated;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Deprecated(forRemoval = true)
public class StoredFileView {
    @Id
    private String storedFileId;
    private String userId;
    private boolean fileUploaded;
}
