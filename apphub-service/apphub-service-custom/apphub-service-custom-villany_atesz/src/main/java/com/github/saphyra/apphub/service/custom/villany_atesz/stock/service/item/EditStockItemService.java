package com.github.saphyra.apphub.service.custom.villany_atesz.stock.service.item;

import com.github.saphyra.apphub.api.custom.villany_atesz.model.StockItemRequest;
import com.github.saphyra.apphub.service.custom.villany_atesz.stock.dao.item.StockItem;
import com.github.saphyra.apphub.service.custom.villany_atesz.stock.dao.item.StockItemDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class EditStockItemService {
    private final StockItemDao stockItemDao;
    private final StockItemValidator stockItemValidator;

    public void edit(UUID stockItemId, StockItemRequest request) {
        stockItemValidator.validate(request);

        StockItem stockItem = stockItemDao.findByIdValidated(stockItemId);

        stockItem.setStockCategoryId(request.getStockCategoryId());
        stockItem.setName(request.getName());
        stockItem.setSerialNumber(request.getSerialNumber());
        stockItem.setInCar(request.getInCar());
        stockItem.setInStorage(request.getInStorage());

        stockItemDao.save(stockItem);

        //TODO update price
    }
}
