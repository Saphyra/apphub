package com.github.saphyra.apphub.service.custom.elite_base.dao.item.trading.commodity.avg_price;

import com.github.saphyra.apphub.lib.common_util.cache.AbstractCache;
import com.google.common.cache.CacheBuilder;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
class CommodityAveragePriceCache extends AbstractCache<String, CommodityAveragePrice> {
    private final CommodityAveragePriceDao commodityAveragePriceDao;

    public CommodityAveragePriceCache(@Lazy CommodityAveragePriceDao commodityAveragePriceDao) {
        super(CacheBuilder.newBuilder().build());
        this.commodityAveragePriceDao = commodityAveragePriceDao;
    }

    @Override
    protected Optional<CommodityAveragePrice> load(String key) {
        return commodityAveragePriceDao.findById(key);
    }
}
