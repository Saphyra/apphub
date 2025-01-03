package com.github.saphyra.apphub.service.elite_base.dao.commodity.last_update;

import com.github.saphyra.apphub.service.elite_base.dao.commodity.CommodityType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class CommodityLastUpdateFactory {
    public CommodityLastUpdate create(UUID externalReference, CommodityType type, LocalDateTime timestamp) {
        return CommodityLastUpdate.builder()
            .externalReference(externalReference)
            .commodityType(type)
            .lastUpdate(timestamp)
            .build();
    }
}
