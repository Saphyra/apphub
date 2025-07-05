package com.github.saphyra.apphub.service.custom.elite_base.dao.loadout;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoadoutCacheKey {
    private final Long marketId;
    private final LoadoutType type;
}
