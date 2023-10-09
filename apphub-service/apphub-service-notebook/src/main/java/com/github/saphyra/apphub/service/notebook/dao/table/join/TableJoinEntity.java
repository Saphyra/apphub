package com.github.saphyra.apphub.service.notebook.dao.table.join;

import com.github.saphyra.apphub.api.notebook.model.table.ColumnType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(schema = "notebook", name = "table_join")
@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Deprecated
class TableJoinEntity {
    @Id
    private String tableJoinId;
    private String userId;
    private String parent;
    private Integer rowIndex;
    private Integer columnIndex;

    @Enumerated(EnumType.STRING)
    private ColumnType columnType;
}
