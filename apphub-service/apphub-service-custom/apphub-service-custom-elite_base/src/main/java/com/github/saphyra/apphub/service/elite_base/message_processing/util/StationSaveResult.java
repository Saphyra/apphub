package com.github.saphyra.apphub.service.elite_base.message_processing.util;

import com.github.saphyra.apphub.service.elite_base.message_processing.dao.commodity.CommodityLocation;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Data
@Builder
public class StationSaveResult {
    private final UUID externalReference;
    private final CommodityLocation commodityLocation;
}
