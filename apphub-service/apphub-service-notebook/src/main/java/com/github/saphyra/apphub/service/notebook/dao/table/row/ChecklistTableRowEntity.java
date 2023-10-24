package com.github.saphyra.apphub.service.notebook.dao.table.row;

import com.github.saphyra.apphub.lib.common_util.ForRemoval;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(schema = "notebook", name = "checklist_table_row")
@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
@ForRemoval("notebook-redesign")
public class ChecklistTableRowEntity {
    @Id
    private String rowId;
    private String userId;
    private String parent;
    private Integer rowIndex;
    private String checked;
}
