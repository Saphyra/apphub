package com.github.saphyra.apphub.service.custom.villany_atesz.stock.dao.item;

import com.github.saphyra.apphub.lib.common_domain.DeleteByUserIdDao;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.AbstractDao;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class StockItemDao extends AbstractDao<StockItemEntity, StockItem, String, StockItemRepository> implements DeleteByUserIdDao {
    private final UuidConverter uuidConverter;

    StockItemDao(StockItemConverter converter, StockItemRepository repository, UuidConverter uuidConverter) {
        super(converter, repository);
        this.uuidConverter = uuidConverter;
    }

    @Override
    public void deleteByUserId(UUID userId) {
        repository.deleteByUserId(uuidConverter.convertDomain(userId));
    }

    public StockItem findByIdValidated(UUID stockItemId) {
        return findById(uuidConverter.convertDomain(stockItemId))
            .orElseThrow(() -> ExceptionFactory.notLoggedException(HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND, "StockItem not found by id " + stockItemId));
    }

    public void deleteByUserIdAndStockItemId(UUID userId, UUID stockItemId) {
        repository.deleteByUserIdAndStockItemId(uuidConverter.convertDomain(userId), uuidConverter.convertDomain(stockItemId));
    }

    public List<StockItem> getByStockCategoryId(UUID stockCategoryId) {
        return converter.convertEntity(repository.getByStockCategoryId(uuidConverter.convertDomain(stockCategoryId)));
    }

    public List<StockItem> getByUserId(UUID userId) {
        return converter.convertEntity(repository.getByUserId(uuidConverter.convertDomain(userId)));
    }
}
