package com.github.saphyra.apphub.service.elite_base.message_processing.dao.loadout;

import com.github.saphyra.apphub.service.elite_base.message_processing.dao.commodity.CommodityLocation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class LoadoutFactory {
    public Loadout create(LocalDateTime timestamp, LoadoutType type, CommodityLocation commodityLocation, UUID externalReference, Long marketId, String name) {
        return Loadout.builder()
            .lastUpdate(timestamp)
            .type(type)
            .commodityLocation(commodityLocation)
            .externalReference(externalReference)
            .marketId(marketId)
            .name(name)
            .build();
    }
}
