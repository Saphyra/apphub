package com.github.saphyra.apphub.service.elite_base.message_processing.saver;

import com.github.saphyra.apphub.service.elite_base.dao.commodity.Commodity;
import com.github.saphyra.apphub.service.elite_base.dao.commodity.CommodityDao;
import com.github.saphyra.apphub.service.elite_base.dao.commodity.CommodityFactory;
import com.github.saphyra.apphub.service.elite_base.dao.commodity.CommodityLocation;
import com.github.saphyra.apphub.service.elite_base.dao.commodity.last_update.CommodityLastUpdateDao;
import com.github.saphyra.apphub.service.elite_base.dao.commodity.last_update.CommodityLastUpdateFactory;
import com.github.saphyra.apphub.service.elite_base.message_processing.structure.commodity.EdCommodity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class CommoditySaver {
    private final CommodityDao commodityDao;
    private final CommodityFactory commodityFactory;
    private final CommodityLastUpdateDao commodityLastUpdateDao;
    private final CommodityLastUpdateFactory commodityLastUpdateFactory;

    public synchronized void saveAll(LocalDateTime timestamp, CommodityLocation commodityLocation, UUID externalReference, Long marketId, EdCommodity[] commodities) {
        if ((isNull(commodityLocation) || isNull(externalReference)) && isNull(marketId)) {
            throw new IllegalStateException("Both commodityLocation or externalReference and marketId is null");
        }

        commodityLastUpdateDao.save(commodityLastUpdateFactory.create(externalReference, timestamp));

        Map<String, Commodity> existingCommodities = commodityDao.getByExternalReferenceOrMarketId(externalReference, marketId)
            .stream()
            .collect(Collectors.toMap(Commodity::getCommodityName, Function.identity()));

        List<Commodity> modifiedCommodities = Arrays.stream(commodities)
            .map(edCommodity -> create(existingCommodities.get(edCommodity.getName()), timestamp, commodityLocation, externalReference, marketId, edCommodity))
            .flatMap(Optional::stream)
            .toList();

        List<String> newCommodityNames = Arrays.stream(commodities)
            .map(EdCommodity::getName)
            .toList();
        List<Commodity> deletedCommodities = existingCommodities.values()
            .stream()
            .filter(c -> !newCommodityNames.contains(c.getCommodityName()))
            .toList();

        commodityDao.deleteAll(deletedCommodities);
        commodityDao.saveAll(modifiedCommodities);
    }

    private Optional<Commodity> create(Commodity maybeStoredCommodity, LocalDateTime timestamp, CommodityLocation commodityLocation, UUID externalReference, Long marketId, EdCommodity edCommodity) {
        Commodity created = commodityFactory.create(
            timestamp,
            commodityLocation,
            externalReference,
            marketId,
            edCommodity.getName(),
            edCommodity.getBuyPrice(),
            edCommodity.getSellPrice(),
            edCommodity.getDemand(),
            edCommodity.getStock(),
            edCommodity.getAveragePrice()
        );

        if (isNull(maybeStoredCommodity)) {
            //Commodity does not exist in the database. Need to save new.
            return Optional.of(created);
        }

        if (maybeStoredCommodity.getLastUpdate().isAfter(timestamp)) {
            //Existing commodity is newer version than the updated one
            return Optional.empty();
        }

        if (maybeStoredCommodity.equals(created)) {
            //No update happened.
            return Optional.empty();
        } else {
            //Update arrived. Syncing columns...
            updateFields(
                maybeStoredCommodity,
                commodityLocation,
                externalReference,
                marketId,
                edCommodity.getBuyPrice(),
                edCommodity.getSellPrice(),
                edCommodity.getDemand(),
                edCommodity.getStock(),
                edCommodity.getAveragePrice()
            );

            return Optional.of(maybeStoredCommodity);
        }
    }

    private void updateFields(
        Commodity commodity,
        CommodityLocation commodityLocation,
        UUID externalReference,
        Long marketId,
        Integer buyPrice,
        Integer sellPrice,
        Integer demand,
        Integer stock,
        Integer averagePrice
    ) {
        List.of(
                new UpdateHelper(commodityLocation, commodity::getCommodityLocation, () -> commodity.setCommodityLocation(commodityLocation)),
                new UpdateHelper(externalReference, commodity::getExternalReference, () -> commodity.setExternalReference(externalReference)),
                new UpdateHelper(marketId, commodity::getMarketId, () -> commodity.setMarketId(marketId)),
                new UpdateHelper(buyPrice, commodity::getBuyPrice, () -> commodity.setBuyPrice(buyPrice)),
                new UpdateHelper(sellPrice, commodity::getSellPrice, () -> commodity.setSellPrice(sellPrice)),
                new UpdateHelper(demand, commodity::getDemand, () -> commodity.setDemand(demand)),
                new UpdateHelper(stock, commodity::getStock, () -> commodity.setStock(stock)),
                new UpdateHelper(averagePrice, commodity::getAveragePrice, () -> commodity.setAveragePrice(averagePrice))
            )
            .forEach(UpdateHelper::modify);
    }
}
