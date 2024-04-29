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
//TODO unit test
public class CreateStockCategoryService {
    private final StockCategoryValidator stockCategoryValidator;
    private final StockCategoryFactory stockCategoryFactory;
    private final StockCategoryDao stockCategoryDao;

    public void create(UUID userId, StockCategoryModel request) {
        stockCategoryValidator.validate(request);

        StockCategory stockCategory = stockCategoryFactory.create(userId, request);

        stockCategoryDao.save(stockCategory);
    }
}
