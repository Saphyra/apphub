package com.github.saphyra.apphub.service.custom.villany_atesz.stock.service.item;

import com.github.saphyra.apphub.api.custom.villany_atesz.model.CreateStockItemRequest;
import com.github.saphyra.apphub.service.custom.villany_atesz.stock.dao.item.StockItem;
import com.github.saphyra.apphub.service.custom.villany_atesz.stock.dao.item.StockItemDao;
import com.github.saphyra.apphub.service.custom.villany_atesz.stock.dao.price.StockItemPrice;
import com.github.saphyra.apphub.service.custom.villany_atesz.stock.dao.price.StockItemPriceDao;
import com.github.saphyra.apphub.service.custom.villany_atesz.stock.service.price.StockItemPriceFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class CreateStockItemService {
    private final CreateStockItemRequestValidator createStockItemRequestValidator;
    private final StockItemFactory stockItemFactory;
    private final StockItemDao stockItemDao;
    private final StockItemPriceFactory stockItemPriceFactory;
    private final StockItemPriceDao stockItemPriceDao;

    public void create(UUID userId, CreateStockItemRequest request) {
        createStockItemRequestValidator.validate(request);

        StockItem stockItem = stockItemFactory.create(userId, request);
        stockItemDao.save(stockItem);

        StockItemPrice stockItemPrice = stockItemPriceFactory.create(userId, stockItem.getStockItemId(), request.getPrice());
        stockItemPriceDao.save(stockItemPrice);
    }
}
