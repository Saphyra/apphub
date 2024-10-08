package com.github.saphyra.apphub.api.custom.villany_atesz.server;

import com.github.saphyra.apphub.api.custom.villany_atesz.model.StockCategoryModel;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.config.common.endpoints.VillanyAteszEndpoints;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;
import java.util.UUID;

public interface StockCategoryController {
    @PutMapping(VillanyAteszEndpoints.VILLANY_ATESZ_CREATE_STOCK_CATEGORY)
    List<StockCategoryModel> createStockCategory(@RequestBody StockCategoryModel request, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @PostMapping(VillanyAteszEndpoints.VILLANY_ATESZ_EDIT_STOCK_CATEGORY)
    List<StockCategoryModel> editStockCategory(@RequestBody StockCategoryModel request, @PathVariable("stockCategoryId") UUID stockCategoryId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @DeleteMapping(VillanyAteszEndpoints.VILLANY_ATESZ_DELETE_STOCK_CATEGORY)
    List<StockCategoryModel> deleteStockCategory(@PathVariable("stockCategoryId") UUID stockCategoryId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @GetMapping(VillanyAteszEndpoints.VILLANY_ATESZ_GET_STOCK_CATEGORIES)
    List<StockCategoryModel> getStockCategories(@RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);
}
