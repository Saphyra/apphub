package com.github.saphyra.apphub.service.custom.elite_base.dao.commodity;

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
@EqualsAndHashCode(exclude = {"lastUpdate"})
public class Commodity {
    private UUID externalReference; //CarrierId/StationId
    private final String commodityName;
    private LocalDateTime lastUpdate;
    private CommodityType type;
    private CommodityLocation commodityLocation;
    private Long marketId; //Storing it, because it might not be guaranteed the station with the given marketId already exists
    private Integer buyPrice;
    private Integer sellPrice;
    private Integer demand;
    private Integer stock;

    private Integer averagePrice;
}
