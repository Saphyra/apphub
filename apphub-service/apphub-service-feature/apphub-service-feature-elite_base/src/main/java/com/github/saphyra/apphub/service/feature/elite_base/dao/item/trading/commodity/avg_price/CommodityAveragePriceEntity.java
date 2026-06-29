package com.github.saphyra.apphub.service.feature.elite_base.dao.item.trading.commodity.avg_price;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.SCHEMA;
import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.TABLE_COMMODITY_AVERAGE_PRICE;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity
@Table(schema = SCHEMA, name = TABLE_COMMODITY_AVERAGE_PRICE)
class CommodityAveragePriceEntity {
    @Id
    private String commodityName;
    private String lastUpdate;
    private Integer averagePrice;
}
