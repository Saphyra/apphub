package com.github.saphyra.apphub.service.custom.elite_base.dao.commodity;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class CommodityDomainId {
    private final UUID externalReference;
    private final String commodityName;
}
