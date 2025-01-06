package com.github.saphyra.apphub.service.elite_base.message_processing.dao.commodity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Data
@EqualsAndHashCode(exclude = {"id", "lastUpdate"})
public class Commodity {
    private final UUID id;
    private LocalDateTime lastUpdate;
    private CommodityType type;
    private CommodityLocation commodityLocation;
    private UUID externalReference; //CarrierId/StationId
    private Long marketId; //Storing it, because it might not be guaranteed the station with the given marketId already exists
    private final String commodityName;
    private Integer buyPrice;
    private Integer sellPrice;
    private Integer demand;
    private Integer stock;

    private Integer averagePrice;
}
