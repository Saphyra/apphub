package com.github.saphyra.apphub.api.custom.villany_atesz.server;

import com.github.saphyra.apphub.api.custom.villany_atesz.model.StockItemInventoryResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.lib.config.common.Endpoints;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;
import java.util.UUID;

//TODO API test
public interface StockInventoryController {
    @GetMapping(Endpoints.VILLANY_ATESZ_STOCK_INVENTORY_GET_ITEMS)
    List<StockItemInventoryResponse> getItemsForInventory(@RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @PostMapping(Endpoints.VILLANY_ATESZ_STOCK_INVENTORY_EDIT_CATEGORY)
    void editCategory(@RequestBody OneParamRequest<UUID> category, @PathVariable("stockItemId") UUID stockItemId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @PostMapping(Endpoints.VILLANY_ATESZ_STOCK_INVENTORY_EDIT_INVENTORIED)
    void editInventoried(@RequestBody OneParamRequest<Boolean> inventoried, @PathVariable("stockItemId") UUID stockItemId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @PostMapping(Endpoints.VILLANY_ATESZ_STOCK_INVENTORY_EDIT_NAME)
    void editName(@RequestBody OneParamRequest<String> name, @PathVariable("stockItemId") UUID stockItemId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @PostMapping(Endpoints.VILLANY_ATESZ_STOCK_INVENTORY_EDIT_SERIAL_NUMBER)
    void editSerialNumber(@RequestBody OneParamRequest<String> serialNumber, @PathVariable("stockItemId") UUID stockItemId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @PostMapping(Endpoints.VILLANY_ATESZ_STOCK_INVENTORY_EDIT_IN_CAR)
    void editInCar(@RequestBody OneParamRequest<Integer> inCar, @PathVariable("stockItemId") UUID stockItemId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @PostMapping(Endpoints.VILLANY_ATESZ_STOCK_INVENTORY_EDIT_IN_STORAGE)
    void editInStorage(@RequestBody OneParamRequest<Integer> inStorage, @PathVariable("stockItemId") UUID stockItemId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);
}
