package com.github.saphyra.apphub.service.notebook.dao.table.row;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(schema = "notebook", name = "checklist_table_row")
@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class ChecklistTableRowEntity {
    @Id
    private String rowId;
    private String userId;
    private String parent;
    private Integer rowIndex;
    private String checked;
}
