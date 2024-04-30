package com.github.saphyra.apphub.service.custom.villany_atesz.stock.dao.price;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@AllArgsConstructor
@Builder
@Data
public class StockItemPrice {
    private final UUID stockItemPriceId;
    private final UUID userId;
    private final UUID stockItemId;
    private Integer price;
}
