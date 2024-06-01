package com.github.saphyra.apphub.service.custom.villany_atesz.stock.service.item;

import com.github.saphyra.apphub.service.custom.villany_atesz.stock.dao.item.StockItem;
import com.github.saphyra.apphub.service.custom.villany_atesz.stock.dao.item.StockItemDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class ResetInventoriedService {
    private final StockItemDao stockItemDao;

    public void resetInventoried(UUID userId) {
        List<StockItem> items = stockItemDao.getByUserId(userId);

        items.forEach(stockItem -> stockItem.setInventoried(false));

        stockItemDao.saveAll(items);
    }
}
