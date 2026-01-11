package com.github.saphyra.apphub.service.custom.elite_base.message_processing.saver;

import com.github.saphyra.apphub.service.custom.elite_base.dao.item.trading.commodity.avg_price.CommodityAveragePrice;
import com.github.saphyra.apphub.service.custom.elite_base.dao.item.trading.commodity.avg_price.CommodityAveragePriceDao;
import com.github.saphyra.apphub.service.custom.elite_base.dao.item.trading.commodity.avg_price.CommodityAveragePriceFactory;
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
//TODO unit test
class CommodityAveragePriceSaver {
    private final CommodityAveragePriceDao commodityAveragePriceDao;
    private final CommodityAveragePriceFactory commodityAveragePriceFactory;

    public void saveAveragePrices(LocalDateTime lastUpdate, Map<String, Integer> commodityPrices) {
        Map<String, CommodityAveragePrice> existing = commodityAveragePriceDao.findAllById(commodityPrices.keySet())
            .stream()
            .collect(Collectors.toMap(CommodityAveragePrice::getCommodityName, cp -> cp));

        List<CommodityAveragePrice> toSave = commodityPrices.entrySet()
            .stream()
            .map(e -> commodityAveragePriceFactory.create(lastUpdate, e.getKey(), e.getValue()))
            .filter(cap -> {
                CommodityAveragePrice existingCap = existing.get(cap.getCommodityName());
                if (isNull(existingCap)) {
                    return true;
                }

                return !existingCap.getAveragePrice().equals(cap.getAveragePrice()) && lastUpdate.isAfter(existingCap.getLastUpdate());
            })
            .toList();

        commodityAveragePriceDao.saveAll(toSave);
    }
}
