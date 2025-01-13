package com.github.saphyra.apphub.service.elite_base.message_processing.dao.commodity.avg_price;

import com.github.saphyra.apphub.lib.common_util.StaticCachedDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

import static java.util.Objects.isNull;

@Component
@Slf4j
public class CommodityAveragePriceDao extends StaticCachedDao<CommodityAveragePriceEntity, CommodityAveragePrice, String, CommodityAveragePriceRepository> {
    CommodityAveragePriceDao(CommodityAveragePriceConverter converter, CommodityAveragePriceRepository repository) {
        super(converter, repository, true);
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
