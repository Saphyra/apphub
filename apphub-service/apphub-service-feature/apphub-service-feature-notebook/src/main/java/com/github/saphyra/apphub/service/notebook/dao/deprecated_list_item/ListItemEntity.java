package com.github.saphyra.apphub.service.notebook.dao.deprecated_list_item;

import com.github.saphyra.apphub.api.feature.notebook.model.ListItemType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(schema = "notebook", name = "list_item")
@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Deprecated(forRemoval = true)
class ListItemEntity {
    @Id
    private String listItemId;

    private String userId;

    private String parent;

    @Enumerated(EnumType.STRING)
    private ListItemType type;

    private String title;

    private String pinned;
    private String archived;
}
