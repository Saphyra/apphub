package com.github.saphyra.apphub.service.custom.villany_atesz.stock.service.item;

import com.github.saphyra.apphub.api.custom.villany_atesz.model.StockItemResponse;
import com.github.saphyra.apphub.service.custom.villany_atesz.stock.dao.item.StockItem;
import com.github.saphyra.apphub.service.custom.villany_atesz.stock.dao.item.StockItemDao;
import com.github.saphyra.apphub.service.custom.villany_atesz.stock.service.category.StockCategoryQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class StockItemQueryService {
    private final StockItemDao stockItemDao;
    private final StockCategoryQueryService stockCategoryQueryService;

    public List<StockItemResponse> getStockItems(UUID userId) {
        return stockItemDao.getByUserId(userId)
            .stream()
            .map(this::convert)
            .toList();
    }

    private StockItemResponse convert(StockItem stockItem) {
        return StockItemResponse.builder()
            .stockItemId(stockItem.getStockItemId())
            .category(stockCategoryQueryService.findByStockCategoryId(stockItem.getStockCategoryId()))
            .name(stockItem.getName())
            .serialNumber(stockItem.getSerialNumber())
            .inCar(stockItem.getInCar())
            .inStorage(stockItem.getInStorage())
            .build();
    }
}
