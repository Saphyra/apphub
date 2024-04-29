package com.github.saphyra.apphub.service.custom.villany_atesz.stock;

import com.github.saphyra.apphub.api.custom.villany_atesz.model.StockItemRequest;
import com.github.saphyra.apphub.api.custom.villany_atesz.model.StockItemResponse;
import com.github.saphyra.apphub.api.custom.villany_atesz.server.StockItemController;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.service.custom.villany_atesz.stock.service.item.CreateStockItemService;
import com.github.saphyra.apphub.service.custom.villany_atesz.stock.service.item.DeleteStockItemService;
import com.github.saphyra.apphub.service.custom.villany_atesz.stock.service.item.EditStockItemService;
import com.github.saphyra.apphub.service.custom.villany_atesz.stock.service.item.StockItemQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class StockItemControllerImpl implements StockItemController {
    private final CreateStockItemService createStockItemService;
    private final EditStockItemService editStockItemService;
    private final DeleteStockItemService deleteStockItemService;
    private final StockItemQueryService stockItemQueryService;

    @Override
    public void createStockItem(StockItemRequest request, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to create a stockItem.", accessTokenHeader.getUserId());

        createStockItemService.create(accessTokenHeader.getUserId(), request);
    }

    @Override
    public List<StockItemResponse> editStockItem(StockItemRequest request, UUID stockItemId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to edit stockItem {}", accessTokenHeader.getUserId(), stockItemId);

        editStockItemService.edit(stockItemId, request);

        return getStockItems(accessTokenHeader);
    }

    @Override
    public List<StockItemResponse> deleteStockItem(UUID stockItemId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to delete stockItem {}", accessTokenHeader.getUserId(), stockItemId);

        deleteStockItemService.delete(accessTokenHeader.getUserId(), stockItemId);

        return getStockItems(accessTokenHeader);
    }

    @Override
    public List<StockItemResponse> getStockItems(AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to know their stockItems", accessTokenHeader.getUserId());

        return stockItemQueryService.getStockItems(accessTokenHeader.getUserId());
    }
}
