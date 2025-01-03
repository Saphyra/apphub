package com.github.saphyra.apphub.service.elite_base.dao.commodity.last_update;

import com.github.saphyra.apphub.service.elite_base.dao.commodity.CommodityType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Data
public class CommodityLastUpdate {
    private final UUID externalReference;
    private final CommodityType commodityType;
    private LocalDateTime lastUpdate;
}
