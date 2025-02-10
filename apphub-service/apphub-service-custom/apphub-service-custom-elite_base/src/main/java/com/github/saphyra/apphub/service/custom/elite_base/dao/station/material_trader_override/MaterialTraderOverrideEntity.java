package com.github.saphyra.apphub.service.custom.elite_base.dao.station.material_trader_override;

import com.github.saphyra.apphub.api.custom.elite_base.model.MaterialType;
import com.github.saphyra.apphub.service.custom.elite_base.common.DatabaseConstants;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity
@Table(schema = DatabaseConstants.SCHEMA, name = DatabaseConstants.TABLE_MATERIAL_TRADER_OVERRIDE)
class MaterialTraderOverrideEntity {
    @Id
    private String stationId;

    @Enumerated(EnumType.STRING)
    private MaterialType materialType;
}
