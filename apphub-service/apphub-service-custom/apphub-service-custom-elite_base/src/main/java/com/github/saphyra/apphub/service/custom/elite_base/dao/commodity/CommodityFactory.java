package com.github.saphyra.apphub.service.custom.elite_base.dao.commodity;

import com.github.saphyra.apphub.lib.common_util.LazyLoadedField;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class CommodityFactory {
    public Commodity create(
        LocalDateTime timestamp,
        CommodityType type,
        CommodityLocation commodityLocation,
        UUID externalReference,
        Long marketId,
        String name,
        Integer buyPrice,
        Integer sellPrice,
        Integer demand,
        Integer stock,
        Integer averagePrice
    ) {
        return Commodity.builder()
            .lastUpdate(LazyLoadedField.loaded(timestamp))
            .type(type)
            .commodityLocation(commodityLocation)
            .externalReference(externalReference)
            .marketId(marketId)
            .commodityName(name.toLowerCase())
            .buyPrice(buyPrice)
            .sellPrice(sellPrice)
            .demand(demand)
            .stock(stock)
            .averagePrice(averagePrice)
            .build();
    }
}
