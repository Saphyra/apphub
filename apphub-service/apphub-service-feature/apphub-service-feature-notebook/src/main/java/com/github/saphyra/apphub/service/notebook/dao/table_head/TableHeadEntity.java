package com.github.saphyra.apphub.service.notebook.dao.table_head;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(schema = "notebook", name = "table_head")
@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
class TableHeadEntity {
    @Id
    private String tableHeadId;
    private String userId;
    private String parent;
    private Integer columnIndex;
}
