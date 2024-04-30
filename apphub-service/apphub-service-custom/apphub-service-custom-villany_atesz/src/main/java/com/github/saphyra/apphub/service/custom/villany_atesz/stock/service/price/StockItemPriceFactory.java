package com.github.saphyra.apphub.service.custom.villany_atesz.stock.service.price;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.custom.villany_atesz.stock.dao.price.StockItemPrice;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
//TODO unit test
public class StockItemPriceFactory {
    private final IdGenerator idGenerator;

    public StockItemPrice create(UUID userId, UUID stockItemId, Integer price) {
        return StockItemPrice.builder()
            .stockItemPriceId(idGenerator.randomUuid())
            .userId(userId)
            .stockItemId(stockItemId)
            .price(price)
            .build();
    }
}
