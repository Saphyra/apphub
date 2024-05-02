package com.github.saphyra.apphub.service.custom.villany_atesz.stock;

import com.github.saphyra.apphub.api.custom.villany_atesz.model.AddToStockRequest;
import com.github.saphyra.apphub.api.custom.villany_atesz.model.StockItemForCategoryResponse;
import com.github.saphyra.apphub.api.custom.villany_atesz.model.StockItemRequest;
import com.github.saphyra.apphub.api.custom.villany_atesz.model.StockItemOverviewResponse;
import com.github.saphyra.apphub.api.custom.villany_atesz.server.StockItemController;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.service.custom.villany_atesz.stock.service.item.AcquisitionService;
import com.github.saphyra.apphub.service.custom.villany_atesz.stock.service.item.CreateStockItemService;
import com.github.saphyra.apphub.service.custom.villany_atesz.stock.service.item.DeleteStockItemService;
import com.github.saphyra.apphub.service.custom.villany_atesz.stock.service.item.MoveStockService;
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
    private final DeleteStockItemService deleteStockItemService;
    private final StockItemQueryService stockItemQueryService;
    private final AcquisitionService acquisitionService;
    private final MoveStockService moveStockService;

    @Override
    public void createStockItem(StockItemRequest request, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to create a stockItem.", accessTokenHeader.getUserId());

        createStockItemService.create(accessTokenHeader.getUserId(), request);
    }

    @Override
    public List<StockItemOverviewResponse> deleteStockItem(UUID stockItemId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to delete stockItem {}", accessTokenHeader.getUserId(), stockItemId);

        deleteStockItemService.delete(accessTokenHeader.getUserId(), stockItemId);

        return getStockItems(accessTokenHeader);
    }

    @Override
    public List<StockItemOverviewResponse> getStockItems(AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to know their stockItems", accessTokenHeader.getUserId());

        return stockItemQueryService.getStockItems(accessTokenHeader.getUserId());
    }

    @Override
    public List<StockItemForCategoryResponse> getStockItemsForCategory(UUID stockCategoryId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to know their items belong to category {}", accessTokenHeader.getUserId(), stockCategoryId);

        return stockItemQueryService.getForCategory(stockCategoryId);
    }

    @Override
    public void acquire(List<AddToStockRequest> request, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to add {} items to the stock.", accessTokenHeader.getUserId(), request.size());

        acquisitionService.acquire(request);
    }

    @Override
    public List<StockItemOverviewResponse> moveStockToCar(OneParamRequest<Integer> amount, UUID stockItemId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to move stock from storage to car for stockItem {}", accessTokenHeader.getUserId(), stockItemId);

        moveStockService.moveToCar(stockItemId, amount.getValue());

        return getStockItems(accessTokenHeader);

    }

    @Override
    public List<StockItemOverviewResponse> moveStockToStorage(OneParamRequest<Integer> amount, UUID stockItemId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to move stock from storage to storage for stockItem {}", accessTokenHeader.getUserId(), stockItemId);

        moveStockService.moveToStorage(stockItemId, amount.getValue());

        return getStockItems(accessTokenHeader);
    }
}
