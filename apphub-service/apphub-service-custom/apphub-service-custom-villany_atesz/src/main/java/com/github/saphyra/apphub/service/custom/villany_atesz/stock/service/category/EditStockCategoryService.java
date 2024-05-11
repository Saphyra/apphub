package com.github.saphyra.apphub.service.custom.villany_atesz.stock.service.category;

import com.github.saphyra.apphub.api.custom.villany_atesz.model.StockCategoryModel;
import com.github.saphyra.apphub.service.custom.villany_atesz.stock.dao.category.StockCategory;
import com.github.saphyra.apphub.service.custom.villany_atesz.stock.dao.category.StockCategoryDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class EditStockCategoryService {
    private final StockCategoryValidator stockCategoryValidator;
    private final StockCategoryDao stockCategoryDao;
    private final StockCategoryModelCache stockCategoryModelCache;

    public void edit(UUID stockCategoryId, StockCategoryModel request) {
        stockCategoryValidator.validate(request);

        StockCategory stockCategory = stockCategoryDao.findByIdValidated(stockCategoryId);

        stockCategory.setName(request.getName());
        stockCategory.setMeasurement(request.getMeasurement());

        stockCategoryDao.save(stockCategory);

        stockCategoryModelCache.invalidate(stockCategoryId);
    }
}
