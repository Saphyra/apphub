package com.github.saphyra.apphub.service.custom.villany_atesz.stock.service.item;

import com.github.saphyra.apphub.service.custom.villany_atesz.stock.dao.item.StockItemDao;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class DeleteStockItemService {
    private final StockItemDao stockItemDao;

    public void deleteByStockCategoryId(UUID stockCategoryId) {
        stockItemDao.getByStockCategoryId(stockCategoryId)
            .forEach(stockItem -> delete(stockItem.getUserId(), stockItem.getStockItemId()));
    }

    @Transactional
    public void delete(UUID userId, UUID stockItemId) {
        stockItemDao.deleteByUserIdAndStockItemId(userId, stockItemId);

        //TODO Remove item for related carts
    }
}
