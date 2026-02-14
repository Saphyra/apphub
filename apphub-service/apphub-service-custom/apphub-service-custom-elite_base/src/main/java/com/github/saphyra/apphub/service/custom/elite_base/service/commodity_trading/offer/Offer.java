package com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading.offer;

import com.github.saphyra.apphub.service.custom.elite_base.dao.item.ItemLocationType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Offer {
    private final UUID externalReference;
    private final ItemLocationType locationType;
    private final int price;
    private final int amount;
    private final UUID starSystemId;
    private final String starName;
    private final LocalDateTime lastUpdate;
    private final double distanceFromReferenceSystem;
}
