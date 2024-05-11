package com.github.saphyra.apphub.service.custom.villany_atesz.stock.service.category;

import com.github.saphyra.apphub.api.custom.villany_atesz.model.StockCategoryModel;
import com.github.saphyra.apphub.service.custom.villany_atesz.stock.dao.category.StockCategory;
import org.springframework.stereotype.Component;

@Component
class StockCategoryModelConverter {
    StockCategoryModel convert(StockCategory stockCategory) {
        return StockCategoryModel.builder()
            .stockCategoryId(stockCategory.getStockCategoryId())
            .name(stockCategory.getName())
            .measurement(stockCategory.getMeasurement())
            .build();
    }
}
