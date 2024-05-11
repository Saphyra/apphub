package com.github.saphyra.apphub.service.custom.villany_atesz.stock.dao.category;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

interface StockCategoryRepository extends CrudRepository<StockCategoryEntity, String> {
    void deleteByUserId(String userId);

    void deleteByUserIdAndStockCategoryId(String userId, String stockCategoryId);

    List<StockCategoryEntity> getByUserId(String userId);
}
