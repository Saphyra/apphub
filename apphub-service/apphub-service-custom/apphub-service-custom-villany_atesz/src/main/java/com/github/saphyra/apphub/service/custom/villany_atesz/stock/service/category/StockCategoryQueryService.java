package com.github.saphyra.apphub.service.custom.villany_atesz.stock.service.category;

import com.github.saphyra.apphub.api.custom.villany_atesz.model.StockCategoryModel;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.custom.villany_atesz.stock.dao.category.StockCategoryDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class StockCategoryQueryService {
    private final StockCategoryDao stockCategoryDao;
    private final StockCategoryModelConverter stockCategoryModelConverter;
    private final StockCategoryModelCache stockCategoryModelCache;

    public List<StockCategoryModel> getStockCategories(UUID userId) {
        return stockCategoryDao.getByUserId(userId)
            .stream()
            .map(stockCategoryModelConverter::convert)
            .collect(Collectors.toList());
    }

    public StockCategoryModel findByStockCategoryId(UUID stockCategoryId) {
        return stockCategoryModelCache.load(stockCategoryId)
            .orElseThrow(() -> ExceptionFactory.loggedException(HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND, "StockCategoryModel not found by id " + stockCategoryId));
    }
}
