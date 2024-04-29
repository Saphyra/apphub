package com.github.saphyra.apphub.service.custom.villany_atesz.stock.dao.item;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

//TODO unit test
interface StockItemRepository extends CrudRepository<StockItemEntity, String> {
    void deleteByUserId(String userId);

    void deleteByUserIdAndStockItemId(String userId, String stockItemId);

    List<StockItemEntity> getByStockCategoryId(String stockCategoryId);

    List<StockItem> getByUserId(String userId);
}
