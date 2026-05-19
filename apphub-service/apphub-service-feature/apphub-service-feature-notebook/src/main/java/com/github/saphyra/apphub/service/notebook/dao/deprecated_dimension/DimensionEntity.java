package com.github.saphyra.apphub.service.notebook.dao.deprecated_dimension;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(schema = "notebook", name = "dimension")
@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Deprecated(forRemoval = true)
class DimensionEntity {
    @Id
    private String dimensionId;
    private String userId;
    private String externalReference;
    private String index;
}
