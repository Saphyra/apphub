package com.github.saphyra.apphub.service.custom.villany_atesz.stock.service.category;

import com.github.saphyra.apphub.service.custom.villany_atesz.stock.dao.category.StockCategoryDao;
import com.github.saphyra.apphub.service.custom.villany_atesz.stock.service.item.DeleteStockItemService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeleteStockCategoryService {
    private final StockCategoryDao stockCategoryDao;
    private final DeleteStockItemService deleteStockItemService;
    private final StockCategoryModelCache stockCategoryModelCache;

    @Transactional
    public void delete(UUID userId, UUID stockCategoryId) {
        stockCategoryDao.deleteByUserIdAndStockCategoryId(userId, stockCategoryId);

        deleteStockItemService.deleteByStockCategoryId(stockCategoryId);

        stockCategoryModelCache.invalidate(stockCategoryId);
    }
}
