package com.github.saphyra.apphub.service.custom.elite_base.message_processing.dao.loadout;

import com.github.saphyra.apphub.service.custom.elite_base.message_processing.dao.commodity.CommodityLocation;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Data
@Builder
public class Loadout {
    private LocalDateTime lastUpdate;
    private final LoadoutType type;
    private CommodityLocation commodityLocation;
    private UUID externalReference;
    private Long marketId;
    private String name;
}
