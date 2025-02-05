package com.github.saphyra.apphub.service.custom.elite_base.message_processing.dao.commodity.avg_price;

import jakarta.persistence.Entity;
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
@Table(schema = "elite_base", name = "commodity_average_price")
class CommodityAveragePriceEntity {
    @Id
    private String commodityName;
    private String lastUpdate;
    private Integer averagePrice;
}
