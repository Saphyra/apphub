package com.github.saphyra.apphub.service.notebook.dao.file;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity
@Table(schema = "notebook", name = "file")
class FileEntity {
    @Id
    private String fileId;
    private String userId;
    private String parent;
    private String storedFileId;
}
