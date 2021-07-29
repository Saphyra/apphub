package com.github.saphyra.apphub.service.notebook.dao.list_item;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(schema = "notebook", name = "list_item")
@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
class ListItemEntity {
    @Id
    private String listItemId;

    private String userId;

    private String parent;

    @Enumerated(EnumType.STRING)
    private ListItemType type;

    private String title;

    private String pinned;
}
