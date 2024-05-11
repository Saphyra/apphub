package com.github.saphyra.apphub.service.custom.villany_atesz.stock.service.item;

import com.github.saphyra.apphub.service.custom.villany_atesz.cart.dao.item.CartItemDao;
import com.github.saphyra.apphub.service.custom.villany_atesz.stock.dao.item.StockItemDao;
import com.github.saphyra.apphub.service.custom.villany_atesz.stock.dao.price.StockItemPriceDao;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeleteStockItemService {
    private final StockItemDao stockItemDao;
    private final StockItemPriceDao stockItemPriceDao;
    private final CartItemDao cartItemDao;

    public void deleteByStockCategoryId(UUID stockCategoryId) {
        stockItemDao.getByStockCategoryId(stockCategoryId)
            .forEach(stockItem -> delete(stockItem.getUserId(), stockItem.getStockItemId()));
    }

    @Transactional
    public void delete(UUID userId, UUID stockItemId) {
        stockItemDao.deleteByUserIdAndStockItemId(userId, stockItemId);
        stockItemPriceDao.deleteByStockItemId(stockItemId);
        cartItemDao.deleteByStockItemId(stockItemId);
    }
}
