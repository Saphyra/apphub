package com.github.saphyra.apphub.service.platform.storage.dao.deprecated;

import com.github.saphyra.apphub.service.platform.storage.dao.stored_file.Storage;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity
@Table(schema = "storage", name = "stored_file")
@Deprecated(forRemoval = true)
class DeprecatedStoredFileEntity {
    @Id
    private String storedFileId;
    private String userId;
    private String fileName;
    private String size;
    private LocalDateTime createdAt;
    private boolean fileUploaded;
    @Enumerated(EnumType.STRING)
    private Storage storage;
}
