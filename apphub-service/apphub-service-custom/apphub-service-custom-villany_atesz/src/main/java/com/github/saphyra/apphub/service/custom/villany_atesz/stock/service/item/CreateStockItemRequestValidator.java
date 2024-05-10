package com.github.saphyra.apphub.service.custom.villany_atesz.stock.service.item;

import com.github.saphyra.apphub.api.custom.villany_atesz.model.CreateStockItemRequest;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.service.custom.villany_atesz.stock.dao.category.StockCategoryDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
class CreateStockItemRequestValidator {
    private final StockCategoryDao stockCategoryDao;

    void validate(CreateStockItemRequest request) {
        ValidationUtil.notNull(request.getStockCategoryId(), "stockCategoryId");
        ValidationUtil.notBlank(request.getName(), "name");
        ValidationUtil.notNull(request.getSerialNumber(), "serialNumber");
        ValidationUtil.notNull(request.getInCar(), "inCar");
        ValidationUtil.notNull(request.getInStorage(), "inStorage");
        ValidationUtil.notNull(request.getPrice(), "price");

        stockCategoryDao.findByIdValidated(request.getStockCategoryId());
    }
}
