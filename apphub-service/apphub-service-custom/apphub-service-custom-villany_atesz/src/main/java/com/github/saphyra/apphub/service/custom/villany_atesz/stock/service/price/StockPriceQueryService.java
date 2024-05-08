package com.github.saphyra.apphub.service.custom.villany_atesz.stock.service.price;

import com.github.saphyra.apphub.service.custom.villany_atesz.stock.dao.price.StockItemPrice;
import com.github.saphyra.apphub.service.custom.villany_atesz.stock.dao.price.StockItemPriceDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.UUID;

@Component
@RequiredArgsConstructor
//TODO unit test
public class StockPriceQueryService {
    private final StockItemPriceDao stockItemPriceDao;

    public int getPriceOf(UUID stockItemId){
        return stockItemPriceDao.getByStockItemId(stockItemId)
            .stream()
            .max((Comparator.comparing(StockItemPrice::getPrice)))
            .map(StockItemPrice::getPrice)
            .orElse(0);
    }
}
