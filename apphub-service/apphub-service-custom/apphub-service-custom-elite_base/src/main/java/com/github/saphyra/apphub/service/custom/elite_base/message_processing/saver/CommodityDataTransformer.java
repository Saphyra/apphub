package com.github.saphyra.apphub.service.custom.elite_base.message_processing.saver;

import com.github.saphyra.apphub.service.custom.elite_base.dao.commodity.Commodity;
import com.github.saphyra.apphub.service.custom.elite_base.dao.commodity.CommodityFactory;
import com.github.saphyra.apphub.service.custom.elite_base.dao.commodity.CommodityLocation;
import com.github.saphyra.apphub.service.custom.elite_base.dao.commodity.CommodityType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static java.util.Objects.isNull;

@RequiredArgsConstructor
@Component
@Slf4j
public class CommodityDataTransformer {
    private final CommodityFactory commodityFactory;

    public Optional<Commodity> transform(Commodity maybeStoredCommodity, LocalDateTime timestamp, CommodityType type, CommodityLocation commodityLocation, UUID externalReference, Long marketId, CommoditySaver.CommodityData commodityData) {
        Commodity created = commodityFactory.create(
            timestamp,
            type,
            commodityLocation,
            externalReference,
            marketId,
            commodityData.getName(),
            commodityData.getBuyPrice(),
            commodityData.getSellPrice(),
            commodityData.getDemand(),
            commodityData.getStock(),
            commodityData.getAveragePrice()
        );

        if (isNull(maybeStoredCommodity)) {
            //Commodity does not exist in the database. Need to save new.
            return Optional.of(created);
        }

        LocalDateTime lastUpdate = Optional.ofNullable(maybeStoredCommodity.getLastUpdate())
            .orElse(LocalDateTime.MIN);
        if (lastUpdate.isAfter(timestamp)) {
            //Existing commodity is newer version than the updated one
            return Optional.empty();
        }

        if (maybeStoredCommodity.equals(created)) {
            //No update happened.
            return Optional.empty();
        } else {
            //Update arrived.
            return Optional.of(created);
        }
    }
}
