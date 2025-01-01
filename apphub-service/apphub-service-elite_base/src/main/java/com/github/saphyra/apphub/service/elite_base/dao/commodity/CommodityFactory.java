package com.github.saphyra.apphub.service.elite_base.dao.commodity;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class CommodityFactory {
    private final IdGenerator idGenerator;

    public Commodity create(
        LocalDateTime timestamp,
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
            .id(idGenerator.randomUuid())
            .lastUpdate(timestamp)
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
