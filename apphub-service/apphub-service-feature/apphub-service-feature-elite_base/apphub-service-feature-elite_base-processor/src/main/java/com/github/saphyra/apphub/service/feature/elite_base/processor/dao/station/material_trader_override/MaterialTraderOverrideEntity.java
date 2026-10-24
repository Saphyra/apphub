package com.github.saphyra.apphub.service.feature.elite_base.processor.dao.station.material_trader_override;

import com.github.saphyra.apphub.api.feature.elite_base.model.MaterialType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.github.saphyra.apphub.service.feature.elite_base.processor.common.DatabaseConstants.SCHEMA;
import static com.github.saphyra.apphub.service.feature.elite_base.processor.common.DatabaseConstants.TABLE_MATERIAL_TRADER_OVERRIDE;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity
@Table(schema = SCHEMA, name = TABLE_MATERIAL_TRADER_OVERRIDE)
class MaterialTraderOverrideEntity {
    @Id
    private String stationId;

    @Enumerated(EnumType.STRING)
    private MaterialType materialType;

    private Boolean verified;
}
