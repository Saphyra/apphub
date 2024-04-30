package com.github.saphyra.apphub.service.custom.villany_atesz.stock.dao.price;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(schema = "villany_atesz", name = "stock_item_price")
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
class StockItemPriceEntity {
    @Id
    private String stockItemPriceId;
    private String userId;
    private String stockItemId;
    private String price;
}
