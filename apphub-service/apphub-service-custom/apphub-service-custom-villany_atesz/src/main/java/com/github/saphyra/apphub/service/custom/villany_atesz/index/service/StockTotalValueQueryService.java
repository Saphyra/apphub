package com.github.saphyra.apphub.service.custom.villany_atesz.index.service;

import com.github.saphyra.apphub.service.custom.villany_atesz.stock.dao.item.StockItemDao;
import com.github.saphyra.apphub.service.custom.villany_atesz.stock.service.price.StockItemPriceQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class StockTotalValueQueryService {
    private final StockItemDao stockItemDao;
    private final StockItemPriceQueryService stockItemPriceQueryService;

    public Integer getTotalValue(UUID userId) {
        return stockItemDao.getByUserId(userId)
            .stream()
            .mapToInt(stockItem -> (stockItem.getInCar() + stockItem.getInStorage()) * stockItemPriceQueryService.getPriceOf(stockItem.getStockItemId()))
            .sum();
    }
}
