package com.github.saphyra.apphub.service.custom.elite_base.dao.commodity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.github.saphyra.apphub.service.custom.elite_base.common.DatabaseConstants.SCHEMA;
import static com.github.saphyra.apphub.service.custom.elite_base.common.DatabaseConstants.TABLE_COMMODITY;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity
@Table(schema = SCHEMA, name = TABLE_COMMODITY)
class CommodityEntity {
    @EmbeddedId
    private CommodityEntityId id;
    @Enumerated(EnumType.STRING)
    private CommodityLocation commodityLocation;
    @Enumerated(EnumType.STRING)
    private CommodityType type;
    private Long marketId;
    private Integer buyPrice;
    private Integer sellPrice;
    private Integer demand;
    private Integer stock;
}
