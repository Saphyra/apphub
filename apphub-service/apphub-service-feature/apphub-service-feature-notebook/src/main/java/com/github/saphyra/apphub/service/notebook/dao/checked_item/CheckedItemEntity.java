package com.github.saphyra.apphub.service.notebook.dao.checked_item;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(schema = "notebook", name = "checked_item")
@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
class CheckedItemEntity {
    @Id
    private String checkedItemId;
    private String userId;
    private String checked;
}
