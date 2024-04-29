package com.github.saphyra.apphub.service.custom.villany_atesz.stock.service.category;

import com.github.saphyra.apphub.api.custom.villany_atesz.model.StockCategoryModel;
import com.github.saphyra.apphub.lib.common_util.AbstractCache;
import com.github.saphyra.apphub.service.custom.villany_atesz.stock.dao.category.StockCategoryDao;
import com.google.common.cache.CacheBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

@Component
@Slf4j
//TODO clear when StockItem modified
class StockCategoryModelCache extends AbstractCache<UUID, StockCategoryModel> {
    private final StockCategoryModelConverter stockCategoryModelConverter;
    private final StockCategoryDao stockCategoryDao;

    StockCategoryModelCache(StockCategoryModelConverter stockCategoryModelConverter, StockCategoryDao stockCategoryDao) {
        super(CacheBuilder.newBuilder().expireAfterAccess(Duration.ofMinutes(10)).build());
        this.stockCategoryModelConverter = stockCategoryModelConverter;
        this.stockCategoryDao = stockCategoryDao;
    }

    @Override
    protected Optional<StockCategoryModel> load(UUID stockCategoryId) {
        return stockCategoryDao.findById(stockCategoryId)
            .map(stockCategoryModelConverter::convert);
    }
}
