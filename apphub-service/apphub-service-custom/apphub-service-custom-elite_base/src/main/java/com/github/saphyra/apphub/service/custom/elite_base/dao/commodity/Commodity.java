package com.github.saphyra.apphub.service.custom.elite_base.dao.commodity;

import com.github.saphyra.apphub.lib.common_util.LazyLoadedField;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Data
@EqualsAndHashCode(exclude = {"lastUpdate"})
public class Commodity {
    private UUID externalReference; //CarrierId/StationId
    private final String commodityName;
    private LazyLoadedField<LocalDateTime> lastUpdate;
    private CommodityType type;
    private CommodityLocation commodityLocation;
    private Long marketId; //Storing it, because it might not be guaranteed the station with the given marketId already exists
    private Integer buyPrice;
    private Integer sellPrice;
    private Integer demand;
    private Integer stock;

    private Integer averagePrice;

    public LocalDateTime getLastUpdate() {
        return Optional.ofNullable(lastUpdate).map(LazyLoadedField::get).orElse(null);
    }
}
