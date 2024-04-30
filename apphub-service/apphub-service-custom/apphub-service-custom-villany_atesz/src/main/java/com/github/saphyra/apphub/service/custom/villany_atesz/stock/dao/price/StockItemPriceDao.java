package com.github.saphyra.apphub.service.custom.villany_atesz.stock.dao.price;

import com.github.saphyra.apphub.lib.common_domain.DeleteByUserIdDao;
import com.github.saphyra.apphub.lib.common_util.AbstractDao;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
//TODO unit test
public class StockItemPriceDao extends AbstractDao<StockItemPriceEntity, StockItemPrice, String, StockItemPriceRepository> implements DeleteByUserIdDao {
    private final UuidConverter uuidConverter;

    StockItemPriceDao(StockItemPriceConverter converter, StockItemPriceRepository repository, UuidConverter uuidConverter) {
        super(converter, repository);
        this.uuidConverter = uuidConverter;
    }

    @Override
    public void deleteByUserId(UUID userId) {
        repository.deleteByUserId(uuidConverter.convertDomain(userId));
    }

    public void deleteByStockItemId(UUID stockItemId) {
        repository.deleteByStockItemId(uuidConverter.convertDomain(stockItemId));
    }

    public List<StockItemPrice> getByStockItemId(UUID stockItemId) {
        return converter.convertEntity(repository.getByStockItemId(uuidConverter.convertDomain(stockItemId)));
    }
}
