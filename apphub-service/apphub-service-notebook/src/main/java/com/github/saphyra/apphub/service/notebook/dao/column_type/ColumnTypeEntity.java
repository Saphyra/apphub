package com.github.saphyra.apphub.service.notebook.dao.column_type;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(schema = "notebook", name = "column_type")
@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
class ColumnTypeEntity {
    @Id
    private String columnId;
    private String userId;
    private String type;
}
