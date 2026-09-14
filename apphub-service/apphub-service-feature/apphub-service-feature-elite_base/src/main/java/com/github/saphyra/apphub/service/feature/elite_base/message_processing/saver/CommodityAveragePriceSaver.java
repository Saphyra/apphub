package com.github.saphyra.apphub.service.feature.elite_base.message_processing.saver;

import com.github.saphyra.apphub.service.feature.elite_base.dao.ObjectType;
import com.github.saphyra.apphub.service.feature.elite_base.dao.item.trading.commodity.avg_price.CommodityAveragePrice;
import com.github.saphyra.apphub.service.feature.elite_base.dao.item.trading.commodity.avg_price.CommodityAveragePriceDao;
import com.github.saphyra.apphub.service.feature.elite_base.dao.item.trading.commodity.avg_price.CommodityAveragePriceFactory;
import com.github.saphyra.apphub.service.feature.elite_base.dao.last_update.LastUpdate;
import com.github.saphyra.apphub.service.feature.elite_base.dao.last_update.LastUpdateDao;
import com.github.saphyra.apphub.service.feature.elite_base.dao.last_update.LastUpdateFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
class CommodityAveragePriceSaver {
    private final CommodityAveragePriceDao commodityAveragePriceDao;
    private final CommodityAveragePriceFactory commodityAveragePriceFactory;
    private final LastUpdateFactory lastUpdateFactory;
    private final LastUpdateDao lastUpdateDao;

    public void saveAveragePrices(LocalDateTime timestamp, Map<String, Integer> commodityPrices) {
        Map<String, CommodityAveragePrice> existing = commodityAveragePriceDao.findAllById(commodityPrices.keySet())
            .stream()
            .collect(Collectors.toMap(CommodityAveragePrice::getCommodityName, cp -> cp));

        List<CommodityAveragePrice> toSave = commodityPrices.entrySet()
            .stream()
            .map(e -> commodityAveragePriceFactory.create(e.getKey(), e.getValue()))
            .filter(cap -> {
                CommodityAveragePrice existingCap = existing.get(cap.getCommodityName());
                if (timestamp.isBefore(lastUpdateDao.findByIdOrDefault(cap.getCommodityName(), ObjectType.COMMODITY_AVERAGE_PRICE).getLastUpdate())) {
                    //Outdated entry
                    return false;
                }

                LastUpdate lastUpdate = lastUpdateFactory.create(cap.getCommodityName(), ObjectType.COMMODITY_AVERAGE_PRICE, timestamp);
                lastUpdateDao.save(lastUpdate);

                if (isNull(existingCap)) {
                    //New entry
                    return true;
                }

                if (existingCap.getAveragePrice().equals(cap.getAveragePrice())) {
                    //No need to save if value is not changed
                    return false;
                }

                //Modified entry
                return true;
            })
            .toList();

        commodityAveragePriceDao.saveAll(toSave);
    }
}
