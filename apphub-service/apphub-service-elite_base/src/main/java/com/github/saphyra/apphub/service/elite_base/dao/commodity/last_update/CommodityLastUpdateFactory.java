package com.github.saphyra.apphub.service.elite_base.dao.commodity.last_update;

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
    public CommodityLastUpdate create(UUID externalReference, LocalDateTime timestamp) {
        return CommodityLastUpdate.builder()
            .externalReference(externalReference)
            .lastUpdate(timestamp)
            .build();
    }
}
