package com.github.saphyra.apphub.service.custom.villany_atesz.stock.dao.item;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

interface StockItemRepository extends CrudRepository<StockItemEntity, String> {
    void deleteByUserId(String userId);

    void deleteByUserIdAndStockItemId(String userId, String stockItemId);

    List<StockItemEntity> getByStockCategoryId(String stockCategoryId);

    List<StockItemEntity> getByUserId(String userId);

    List<StockItemEntity> getByUserIdAndMarkedForAcquisition(String userId, boolean markedForAcquisition);
}
