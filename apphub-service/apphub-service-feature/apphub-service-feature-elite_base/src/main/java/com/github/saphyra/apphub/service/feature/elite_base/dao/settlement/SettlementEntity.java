package com.github.saphyra.apphub.service.feature.elite_base.dao.settlement;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.SCHEMA;
import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.TABLE_SETTLEMENT;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity
@Table(schema = SCHEMA, name = TABLE_SETTLEMENT)
class SettlementEntity {
    @Id
    private String id;
    private String lastUpdate;
    private String starSystemId;
    private String bodyId;
    private String settlementName;
    private Long marketId;
    private Double longitude;
    private Double latitude;
}
