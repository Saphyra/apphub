package com.github.saphyra.apphub.api.custom.villany_atesz.server;

import com.github.saphyra.apphub.api.custom.villany_atesz.model.StockItemRequest;
import com.github.saphyra.apphub.api.custom.villany_atesz.model.StockItemResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.config.common.Endpoints;
import org.springframework.web.bind.annotation.DeleteMapping;
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
    void createStockItem(@RequestBody StockItemRequest request, @RequestHeader(Constants.ACCESS_TOKEN_HEADER)AccessTokenHeader accessTokenHeader);

    @PostMapping(Endpoints.VILLANY_ATESZ_EDIT_STOCK_ITEM)
    List<StockItemResponse> editStockItem(@RequestBody StockItemRequest request, @PathVariable("stockItemId") UUID stockItemId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER)AccessTokenHeader accessTokenHeader);

    @DeleteMapping(Endpoints.VILLANY_ATESZ_DELETE_STOCK_ITEM)
    List<StockItemResponse> deleteStockItem(@PathVariable("stockItemId") UUID stockItemId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER)AccessTokenHeader accessTokenHeader);

    @GetMapping(Endpoints.VILLANY_ATESZ_GET_STOCK_ITEMS)
    List<StockItemResponse> getStockItems(@RequestHeader(Constants.ACCESS_TOKEN_HEADER)AccessTokenHeader accessTokenHeader);
}
