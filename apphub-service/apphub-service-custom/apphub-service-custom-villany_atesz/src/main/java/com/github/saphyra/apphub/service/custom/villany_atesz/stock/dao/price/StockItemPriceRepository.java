package com.github.saphyra.apphub.service.custom.villany_atesz.stock.dao.price;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

//TODO unit test
interface StockItemPriceRepository extends CrudRepository<StockItemPriceEntity, String> {
    void deleteByUserId(String userId);

    void deleteByStockItemId(String stockItemId);

    List<StockItemPriceEntity> getByStockItemId(String stockItemId);
}
