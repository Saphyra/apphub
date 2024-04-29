package com.github.saphyra.apphub.service.custom.villany_atesz.stock;

import com.github.saphyra.apphub.api.custom.villany_atesz.model.StockCategoryModel;
import com.github.saphyra.apphub.api.custom.villany_atesz.server.StockCategoryController;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.service.custom.villany_atesz.stock.service.category.CreateStockCategoryService;
import com.github.saphyra.apphub.service.custom.villany_atesz.stock.service.category.DeleteStockCategoryService;
import com.github.saphyra.apphub.service.custom.villany_atesz.stock.service.category.EditStockCategoryService;
import com.github.saphyra.apphub.service.custom.villany_atesz.stock.service.category.StockCategoryQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class StockCategoryControllerImpl implements StockCategoryController {
    private final CreateStockCategoryService createStockCategoryService;
    private final EditStockCategoryService editStockCategoryService;
    private final DeleteStockCategoryService deleteStockCategoryService;
    private final StockCategoryQueryService stockCategoryQueryService;

    @Override
    public List<StockCategoryModel> createStockCategory(StockCategoryModel request, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to create a stockCategory.", accessTokenHeader.getUserId());

        createStockCategoryService.create(accessTokenHeader.getUserId(), request);

        return getStockCategories(accessTokenHeader);
    }

    @Override
    public List<StockCategoryModel> editStockCategory(StockCategoryModel request, UUID stockCategoryId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to edit stockCategory {}", accessTokenHeader.getUserId(), stockCategoryId);

        editStockCategoryService.edit(stockCategoryId, request);

        return getStockCategories(accessTokenHeader);
    }

    @Override
    public List<StockCategoryModel> deleteStockCategory(UUID stockCategoryId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to delete stockCategory {}", accessTokenHeader.getUserId(), stockCategoryId);

        deleteStockCategoryService.delete(accessTokenHeader.getUserId(), stockCategoryId);

        return getStockCategories(accessTokenHeader);
    }

    @Override
    public List<StockCategoryModel> getStockCategories(AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to query their stockCategories.", accessTokenHeader.getUserId());

        return stockCategoryQueryService.getStockCategories(accessTokenHeader.getUserId());
    }
}
