package com.github.saphyra.apphub.service.custom.villany_atesz.stock.dao.category;

import com.github.saphyra.apphub.lib.common_domain.DeleteByUserIdDao;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.AbstractDao;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class StockCategoryDao extends AbstractDao<StockCategoryEntity, StockCategory, String, StockCategoryRepository> implements DeleteByUserIdDao {
    private final UuidConverter uuidConverter;

    public StockCategoryDao(StockCategoryConverter converter, StockCategoryRepository repository, UuidConverter uuidConverter) {
        super(converter, repository);
        this.uuidConverter = uuidConverter;
    }

    @Override
    public void deleteByUserId(UUID userId) {
        repository.deleteByUserId(uuidConverter.convertDomain(userId));
    }

    public StockCategory findByIdValidated(UUID stockCategoryId) {
        return findById(stockCategoryId)
            .orElseThrow(() -> ExceptionFactory.notLoggedException(HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND, "StockCategory not found by id " + stockCategoryId));
    }

    public void deleteByUserIdAndStockCategoryId(UUID userId, UUID stockCategoryId) {
        repository.deleteByUserIdAndStockCategoryId(uuidConverter.convertDomain(userId), uuidConverter.convertDomain(stockCategoryId));
    }

    public List<StockCategory> getByUserId(UUID userId) {
        return converter.convertEntity(repository.getByUserId(uuidConverter.convertDomain(userId)));
    }

    public Optional<StockCategory> findById(UUID stockCategoryId) {
        return findById(uuidConverter.convertDomain(stockCategoryId));
    }
}
