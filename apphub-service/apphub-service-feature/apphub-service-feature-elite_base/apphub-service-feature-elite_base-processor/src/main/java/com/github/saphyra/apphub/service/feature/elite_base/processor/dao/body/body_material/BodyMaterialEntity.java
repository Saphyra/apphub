package com.github.saphyra.apphub.service.feature.elite_base.processor.dao.body.body_material;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.github.saphyra.apphub.service.feature.elite_base.processor.common.DatabaseConstants.SCHEMA;
import static com.github.saphyra.apphub.service.feature.elite_base.processor.common.DatabaseConstants.TABLE_BODY_MATERIAL_V2;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity
@Table(schema = SCHEMA, name = TABLE_BODY_MATERIAL_V2)
class BodyMaterialEntity {
    @EmbeddedId
    private BodyMaterialEntityId id;
    private Double percent;
}
