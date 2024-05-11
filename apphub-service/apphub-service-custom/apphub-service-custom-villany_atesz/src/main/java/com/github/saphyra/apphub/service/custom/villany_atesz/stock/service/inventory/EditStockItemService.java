package com.github.saphyra.apphub.service.custom.villany_atesz.stock.service.inventory;

import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.service.custom.villany_atesz.stock.dao.category.StockCategoryDao;
import com.github.saphyra.apphub.service.custom.villany_atesz.stock.dao.item.StockItem;
import com.github.saphyra.apphub.service.custom.villany_atesz.stock.dao.item.StockItemDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EditStockItemService {
    private final StockItemDao stockItemDao;
    private final StockCategoryDao stockCategoryDao;

    public void editCategory(UUID stockItemId, UUID stockCategoryId) {
        ValidationUtil.notNull(stockCategoryId, "stockCategoryId");
        stockCategoryDao.findByIdValidated(stockCategoryId);

        StockItem stockItem = stockItemDao.findByIdValidated(stockItemId);

        stockItem.setStockCategoryId(stockCategoryId);

        stockItemDao.save(stockItem);
    }

    public void editName(UUID stockItemId, String name) {
        ValidationUtil.notBlank(name, "name");

        StockItem stockItem = stockItemDao.findByIdValidated(stockItemId);

        stockItem.setName(name);

        stockItemDao.save(stockItem);
    }

    public void editSerialNumber(UUID stockItemId, String serialNumber) {
        ValidationUtil.notNull(serialNumber, "serialNumber");

        StockItem stockItem = stockItemDao.findByIdValidated(stockItemId);

        stockItem.setSerialNumber(serialNumber);

        stockItemDao.save(stockItem);
    }

    public void editInCar(UUID stockItemId, Integer inCar) {
        ValidationUtil.notNull(inCar, "inCar");

        StockItem stockItem = stockItemDao.findByIdValidated(stockItemId);

        stockItem.setInCar(inCar);

        stockItemDao.save(stockItem);
    }

    public void editInStorage(UUID stockItemId, Integer inStorage) {
        ValidationUtil.notNull(inStorage, "inStorage");

        StockItem stockItem = stockItemDao.findByIdValidated(stockItemId);

        stockItem.setInStorage(inStorage);

        stockItemDao.save(stockItem);
    }

    public void editInventoried(UUID stockItemId, Boolean inventoried) {
        ValidationUtil.notNull(inventoried, "inventoried");

        StockItem stockItem = stockItemDao.findByIdValidated(stockItemId);

        stockItem.setInventoried(inventoried);

        stockItemDao.save(stockItem);
    }
}
