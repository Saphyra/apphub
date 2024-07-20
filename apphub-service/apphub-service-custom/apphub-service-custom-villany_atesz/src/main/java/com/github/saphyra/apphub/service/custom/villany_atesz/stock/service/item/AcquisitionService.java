package com.github.saphyra.apphub.service.custom.villany_atesz.stock.service.item;

import com.github.saphyra.apphub.api.custom.villany_atesz.model.AddToStockRequest;
import com.github.saphyra.apphub.service.custom.villany_atesz.stock.dao.item.StockItem;
import com.github.saphyra.apphub.service.custom.villany_atesz.stock.dao.item.StockItemDao;
import com.github.saphyra.apphub.service.custom.villany_atesz.stock.dao.price.StockItemPrice;
import com.github.saphyra.apphub.service.custom.villany_atesz.stock.dao.price.StockItemPriceDao;
import com.github.saphyra.apphub.service.custom.villany_atesz.stock.service.price.StockItemPriceFactory;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AcquisitionService {
    private final AddToStockRequestValidator addToStockRequestValidator;
    private final StockItemDao stockItemDao;
    private final StockItemPriceDao stockItemPriceDao;
    private final StockItemPriceFactory stockItemPriceFactory;

    @Transactional
    public void acquire(List<AddToStockRequest> requests) {
        addToStockRequestValidator.validate(requests);

        requests.forEach(this::acquire);
    }

    private void acquire(AddToStockRequest request) {
        StockItem stockItem = stockItemDao.findByIdValidated(request.getStockItemId());

        stockItem.setInCar(stockItem.getInCar() + request.getInCar());
        stockItem.setInStorage(stockItem.getInStorage() + request.getInStorage());
        stockItem.setBarCode(request.getBarCode());
        stockItemDao.save(stockItem);

        if (request.getForceUpdatePrice()) {
            stockItemPriceDao.deleteByStockItemId(request.getStockItemId());
        }

        StockItemPrice price = stockItemPriceFactory.create(stockItem.getUserId(), request.getStockItemId(), request.getPrice());
        stockItemPriceDao.save(price);
    }
}
