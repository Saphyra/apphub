package com.github.saphyra.apphub.service.custom.elite_base.dao.item.trading.commodity;

import com.github.saphyra.apphub.service.custom.elite_base.dao.item.ItemLocationType;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CommodityFactory {
    public Commodity create(ItemLocationType locationType, UUID externalReference, Long marketId, String name, Integer buyPrice, Integer sellPrice, Integer demand, Integer stock) {
        return Commodity.builder()
            .locationType(locationType)
            .externalReference(externalReference)
            .marketId(marketId)
            .itemName(name)
            .buyPrice(buyPrice)
            .sellPrice(sellPrice)
            .demand(demand)
            .stock(stock)
            .build();
    }
}
