package com.github.saphyra.apphub.service.notebook.dao.checklist_item;

import com.github.saphyra.apphub.lib.common_util.ForRemoval;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(schema = "notebook", name = "checklist_item")
@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
@ForRemoval("notebook-redesign")
class ChecklistItemEntity {
    @Id
    private String checklistItemId;
    private String userId;
    private String parent;
    @Column(name = "item_order")
    private String order;
    private String checked;
}
