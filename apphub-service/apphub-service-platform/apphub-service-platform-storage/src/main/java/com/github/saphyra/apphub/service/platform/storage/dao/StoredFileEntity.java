package com.github.saphyra.apphub.service.platform.storage.dao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity
@Table(schema = "storage", name = "stored_file")
class StoredFileEntity {
    @Id
    private String storedFileId;
    private String userId;
    private String extension;
    private String size;
    private LocalDateTime createdAt;
    private boolean fileUploaded;
}
