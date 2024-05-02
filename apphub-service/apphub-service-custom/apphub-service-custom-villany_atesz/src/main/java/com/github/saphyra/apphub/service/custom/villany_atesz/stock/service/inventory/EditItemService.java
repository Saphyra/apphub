package com.github.saphyra.apphub.service.custom.villany_atesz.stock.service.inventory;

import com.github.saphyra.apphub.service.custom.villany_atesz.stock.dao.item.StockItem;
import com.github.saphyra.apphub.service.custom.villany_atesz.stock.dao.item.StockItemDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
//TODO unit test
public class EditItemService {
    private final StockItemDao stockItemDao;

    public void editCategory(UUID stockItemId, UUID stockCategoryId) {
        //TODO validate

        StockItem stockItem = stockItemDao.findByIdValidated(stockItemId);

        stockItem.setStockCategoryId(stockCategoryId);

        stockItemDao.save(stockItem);
    }

    public void editName(UUID stockItemId, String name) {
        //TODO validate

        StockItem stockItem = stockItemDao.findByIdValidated(stockItemId);

        stockItem.setName(name);

        stockItemDao.save(stockItem);
    }

    public void editSerialNumber(UUID stockItemId, String serialNumber) {
        //TODO validate

        StockItem stockItem = stockItemDao.findByIdValidated(stockItemId);

        stockItem.setSerialNumber(serialNumber);

        stockItemDao.save(stockItem);
    }

    public void editInCar(UUID stockItemId, Integer inCar) {
        //TODO validate

        StockItem stockItem = stockItemDao.findByIdValidated(stockItemId);

        stockItem.setInCar(inCar);

        stockItemDao.save(stockItem);
    }

    public void editInStorage(UUID stockItemId, Integer inStorage) {
        //TODO validate

        StockItem stockItem = stockItemDao.findByIdValidated(stockItemId);

        stockItem.setInCar(inStorage);

        stockItemDao.save(stockItem);
    }

    public void editInventoried(UUID stockItemId, Boolean inventoried) {
        //TODO validate

        StockItem stockItem = stockItemDao.findByIdValidated(stockItemId);

        stockItem.setInventoried(inventoried);

        stockItemDao.save(stockItem);
    }
}
