package com.github.saphyra.apphub.service.notebook.dao.image;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity
@Table(schema = "notebook", name = "image")
class ImageEntity {
    @Id
    private String imageId;
    private String userId;
    private String parent;
    private String fileId;
}
