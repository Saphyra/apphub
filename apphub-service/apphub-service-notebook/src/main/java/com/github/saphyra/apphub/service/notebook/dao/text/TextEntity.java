package com.github.saphyra.apphub.service.notebook.dao.text;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(schema = "notebook", name = "text")
@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class TextEntity {
    @Id
    private String textId;
    private String userId;
    private String parent;
    private String content;
}
