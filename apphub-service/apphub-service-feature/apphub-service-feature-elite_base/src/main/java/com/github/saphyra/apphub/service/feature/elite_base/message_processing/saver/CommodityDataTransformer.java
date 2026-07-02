package com.github.saphyra.apphub.service.feature.elite_base.message_processing.saver;

import com.github.saphyra.apphub.service.feature.elite_base.dao.item.ItemLocationType;
import com.github.saphyra.apphub.service.feature.elite_base.dao.ObjectType;
import com.github.saphyra.apphub.service.feature.elite_base.dao.item.trading.Tradeable;
import com.github.saphyra.apphub.service.feature.elite_base.dao.item.trading.TradingDaoSupport;
import com.github.saphyra.apphub.service.feature.elite_base.dao.last_update.LastUpdate;
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
    private final TradingDaoSupport tradingDaoSupport;

    public Optional<Tradeable> transform(
        Tradeable maybeStoredCommodity,
        LocalDateTime timestamp,
        ObjectType type,
        ItemLocationType locationType,
        UUID externalReference,
        Long marketId,
        CommoditySaver.CommodityData commodityData,
        LastUpdate originalLastUpdate,
        UUID starSystemId
    ) {
        Tradeable created = tradingDaoSupport.create(
            type,
            locationType,
            externalReference,
            marketId,
            commodityData.getName(),
            commodityData.getBuyPrice(),
            commodityData.getSellPrice(),
            commodityData.getDemand(),
            commodityData.getStock(),
            starSystemId
        );

        if (isNull(maybeStoredCommodity)) {
            //Commodity does not exist in the database. Need to save new.
            return Optional.of(created);
        }

        if (isNull(originalLastUpdate)) {
            //Stored commodity of unknown origin
            return Optional.of(created);
        }

        if (originalLastUpdate.getLastUpdate().isAfter(timestamp)) {
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
