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
public class CreateStockItemService {
    private final StockItemValidator stockItemValidator;
    private final StockItemFactory stockItemFactory;
    private final StockItemDao stockItemDao;

    public void create(UUID userId, StockItemRequest request) {
        stockItemValidator.validate(request);

        StockItem stockItem = stockItemFactory.create(userId, request);

        //TODO create price

        stockItemDao.save(stockItem);
    }
}
