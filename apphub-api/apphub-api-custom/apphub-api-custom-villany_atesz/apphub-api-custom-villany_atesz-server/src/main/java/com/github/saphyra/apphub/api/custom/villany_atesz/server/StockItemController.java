package com.github.saphyra.apphub.api.custom.villany_atesz.server;

import com.github.saphyra.apphub.api.custom.villany_atesz.model.AcquisitionRequest;
import com.github.saphyra.apphub.api.custom.villany_atesz.model.CreateStockItemRequest;
import com.github.saphyra.apphub.api.custom.villany_atesz.model.StockItemAcquisitionResponse;
import com.github.saphyra.apphub.api.custom.villany_atesz.model.StockItemForCategoryResponse;
import com.github.saphyra.apphub.api.custom.villany_atesz.model.StockItemOverviewResponse;
import com.github.saphyra.apphub.api.custom.villany_atesz.model.StockItemResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.lib.common_domain.OneParamResponse;
import com.github.saphyra.apphub.lib.config.common.Endpoints;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;
import java.util.UUID;

public interface StockItemController {
    @PutMapping(Endpoints.VILLANY_ATESZ_CREATE_STOCK_ITEM)
    void createStockItem(@RequestBody CreateStockItemRequest request, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @GetMapping(Endpoints.VILLANY_ATESZ_GET_STOCK_ITEMS)
    List<StockItemOverviewResponse> getStockItems(@RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @GetMapping(Endpoints.VILLANY_ATESZ_GET_STOCK_ITEMS_FOR_CATEGORY)
    List<StockItemForCategoryResponse> getStockItemsForCategory(@PathVariable("stockCategoryId") UUID stockCategoryId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @PostMapping(Endpoints.VILLANY_ATESZ_STOCK_ACQUIRE)
    void acquire(@RequestBody AcquisitionRequest request, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @PostMapping(Endpoints.VILLANY_ATESZ_FIND_STOCK_ITEM_BY_BAR_CODE)
    ResponseEntity<StockItemAcquisitionResponse> findByBarCode(@RequestBody OneParamRequest<String> barCode, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @GetMapping(Endpoints.VILLANY_ATESZ_FIND_BAR_CODE_BY_STOCK_ITEM_ID)
    OneParamResponse<String> findBarCodeByStockItemId(@PathVariable("stockItemId") UUID stockItemId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @GetMapping(Endpoints.VILLANY_ATESZ_GET_STOCK_ITEM)
    StockItemResponse getStockItem(@PathVariable("stockItemId") UUID stockItemId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);
}
