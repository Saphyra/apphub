package com.github.saphyra.apphub.service.custom.villany_atesz.stock.service.item;

import com.github.saphyra.apphub.api.custom.villany_atesz.model.CreateStockItemRequest;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.custom.villany_atesz.stock.dao.item.StockItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class StockItemFactory {
    private final IdGenerator idGenerator;

    StockItem create(UUID userId, CreateStockItemRequest request) {
        return StockItem.builder()
            .stockItemId(idGenerator.randomUuid())
            .userId(userId)
            .stockCategoryId(request.getStockCategoryId())
            .name(request.getName())
            .serialNumber(request.getSerialNumber())
            .barCode(request.getBarCode())
            .inCar(request.getInCar())
            .inStorage(request.getInStorage())
            .inventoried(false)
            .markedForAcquisition(false)
            .build();
    }
}
