package com.github.saphyra.apphub.service.custom.villany_atesz.stock.service.item;

import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.service.custom.villany_atesz.stock.dao.item.StockItem;
import com.github.saphyra.apphub.service.custom.villany_atesz.stock.dao.item.StockItemDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class MoveStockService {
    private final StockItemDao stockItemDao;

    public void moveToCar(UUID stockItemId, Integer amount) {
        ValidationUtil.notZero(amount, "amount");

        StockItem stockItem = stockItemDao.findByIdValidated(stockItemId);

        stockItem.setInStorage(stockItem.getInStorage() - amount);
        stockItem.setInCar(stockItem.getInCar() + amount);

        stockItemDao.save(stockItem);
    }

    public void moveToStorage(UUID stockItemId, Integer amount) {
        ValidationUtil.notZero(amount, "amount");

        StockItem stockItem = stockItemDao.findByIdValidated(stockItemId);

        stockItem.setInStorage(stockItem.getInStorage() + amount);
        stockItem.setInCar(stockItem.getInCar() - amount);

        stockItemDao.save(stockItem);
    }
}
