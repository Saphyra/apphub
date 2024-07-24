package com.github.saphyra.apphub.service.custom.villany_atesz.stock;

import com.github.saphyra.apphub.api.custom.villany_atesz.model.AcquisitionRequest;
import com.github.saphyra.apphub.api.custom.villany_atesz.model.CreateStockItemRequest;
import com.github.saphyra.apphub.api.custom.villany_atesz.model.StockItemAcquisitionResponse;
import com.github.saphyra.apphub.api.custom.villany_atesz.model.StockItemForCategoryResponse;
import com.github.saphyra.apphub.api.custom.villany_atesz.model.StockItemOverviewResponse;
import com.github.saphyra.apphub.api.custom.villany_atesz.model.StockItemResponse;
import com.github.saphyra.apphub.api.custom.villany_atesz.server.StockItemController;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.lib.common_domain.OneParamResponse;
import com.github.saphyra.apphub.service.custom.villany_atesz.stock.service.item.AcquireItemsService;
import com.github.saphyra.apphub.service.custom.villany_atesz.stock.service.item.CreateStockItemService;
import com.github.saphyra.apphub.service.custom.villany_atesz.stock.service.item.StockItemQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
class StockItemControllerImpl implements StockItemController {
    private final CreateStockItemService createStockItemService;
    private final StockItemQueryService stockItemQueryService;
    private final AcquireItemsService acquireItemsService;

    @Override
    public void createStockItem(CreateStockItemRequest request, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to create a stockItem.", accessTokenHeader.getUserId());

        createStockItemService.create(accessTokenHeader.getUserId(), request);
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
    public void acquire(AcquisitionRequest request, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to add items to the stock.", accessTokenHeader.getUserId());

        acquireItemsService.acquire(accessTokenHeader.getUserId(), request);
    }

    @Override
    public ResponseEntity<StockItemAcquisitionResponse> findByBarCode(OneParamRequest<String> barCode, AccessTokenHeader accessTokenHeader) {
        return ResponseEntity.of(stockItemQueryService.findByBarCode(accessTokenHeader.getUserId(), barCode.getValue()));
    }

    @Override
    public OneParamResponse<String> findBarCodeByStockItemId(UUID stockItemId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to know the barCode of stockItem {}", accessTokenHeader.getUserId(), stockItemId);

        return new OneParamResponse<>(stockItemQueryService.findBarCodeByStockItemId(stockItemId));
    }

    @Override
    public StockItemResponse getStockItem(UUID stockItemId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to query stockItem {}", accessTokenHeader.getUserId(), stockItemId);

        return stockItemQueryService.findByStockItemId(stockItemId);
    }
}
