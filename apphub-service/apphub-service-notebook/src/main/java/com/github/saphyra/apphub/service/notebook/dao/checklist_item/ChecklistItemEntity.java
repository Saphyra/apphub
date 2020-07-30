package com.github.saphyra.apphub.service.notebook.dao.checklist_item;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(schema = "notebook", name = "checklist_item")
@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
class ChecklistItemEntity {
    @Id
    private String checklistItemId;
    private String userId;
    private String parent;
    @Column(name = "item_order")
    private String order;
    private String checked;
}
