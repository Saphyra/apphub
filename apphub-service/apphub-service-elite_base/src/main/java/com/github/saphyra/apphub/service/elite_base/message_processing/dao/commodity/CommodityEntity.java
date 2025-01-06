package com.github.saphyra.apphub.service.elite_base.message_processing.dao.commodity;

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
@Table(schema = "elite_base", name = "commodity")
class CommodityEntity {
    @Id
    private String id;
    @Enumerated(EnumType.STRING)
    private CommodityType type;
    @Enumerated(EnumType.STRING)
    private CommodityLocation commodityLocation;
    private String externalReference;
    private Long marketId;
    private String commodityName;
    private Integer buyPrice;
    private Integer sellPrice;
    private Integer demand;
    private Integer stock;
}
