package com.github.saphyra.apphub.service.notebook.dao.table.join;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(schema = "notebook", name = "table_join")
@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
class TableJoinEntity {
    @Id
    private String tableJoinId;
    private String userId;
    private String parent;
    private Integer rowIndex;
    private Integer columnIndex;
}
