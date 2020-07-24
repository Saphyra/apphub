package com.github.saphyra.apphub.service.notebook.dao.content;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(schema = "notebook", name = "content")
@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class ContentEntity {
    @Id
    private String contentId;
    private String userId;
    private String parent;
    private String content;
}
