package com.github.saphyra.apphub.service.custom.elite_base.message_processing.dao.commodity.avg_price;

import com.github.saphyra.apphub.lib.common_util.CachedDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

import static java.util.Objects.isNull;

@Component
@Slf4j
public class CommodityAveragePriceDao extends CachedDao<CommodityAveragePriceEntity, CommodityAveragePrice, String, CommodityAveragePriceRepository> {
    CommodityAveragePriceDao(CommodityAveragePriceConverter converter, CommodityAveragePriceRepository repository, CommodityAveragePriceCache cache) {
        super(converter, repository, true, cache);
    }

    @Override
    protected String extractId(CommodityAveragePrice commodityAveragePrice) {
        return commodityAveragePrice.getCommodityName();
    }

    @Override
    protected boolean shouldSave(CommodityAveragePrice commodityAveragePrice) {
        if (isNull(commodityAveragePrice.getAveragePrice())) {
            return false;
        }

        Optional<CommodityAveragePrice> maybeStored = findById(extractId(commodityAveragePrice));
        if (maybeStored.isEmpty()) {
            return true;
        }

        CommodityAveragePrice stored = maybeStored.get();
        if (commodityAveragePrice.getLastUpdate().isBefore(stored.getLastUpdate())) {
            return false;
        }

        return !Objects.equals(maybeStored.get().getAveragePrice(), commodityAveragePrice.getAveragePrice());
    }
}
