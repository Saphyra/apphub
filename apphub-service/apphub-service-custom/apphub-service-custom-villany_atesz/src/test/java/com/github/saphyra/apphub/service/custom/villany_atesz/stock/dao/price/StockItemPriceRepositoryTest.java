package com.github.saphyra.apphub.service.custom.villany_atesz.stock.dao.price;

import com.github.saphyra.apphub.test.common.repository.RepositoryTestConfiguration;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = RepositoryTestConfiguration.class)
class StockItemPriceRepositoryTest {
    private static final String STOCK_ITEM_PRICE_ID_1 = "stock-item-price-id-1";
    private static final String STOCK_ITEM_PRICE_ID_2 = "stock-item-price-id-2";
    private static final String USER_ID_1 = "user-id-1";
    private static final String USER_ID_2 = "user-id-2";
    private static final String STOCK_ITEM_ID_1 = "stock-item-id-1";
    private static final String STOCK_ITEM_ID_2 = "stock-item-id-2";

    @Autowired
    private StockItemPriceRepository underTest;

    @AfterEach
    void clear() {
        underTest.deleteAll();
    }

    @Test
    @Transactional
    void deleteByUserId() {
        StockItemPriceEntity entity1 = StockItemPriceEntity.builder()
            .stockItemPriceId(STOCK_ITEM_PRICE_ID_1)
            .userId(USER_ID_1)
            .build();
        underTest.save(entity1);

        StockItemPriceEntity entity2 = StockItemPriceEntity.builder()
            .stockItemPriceId(STOCK_ITEM_PRICE_ID_2)
            .userId(USER_ID_2)
            .build();
        underTest.save(entity2);

        underTest.deleteByUserId(USER_ID_1);

        assertThat(underTest.findAll()).containsExactly(entity2);
    }

    @Test
    @Transactional
    void deleteByStockItemId() {
        StockItemPriceEntity entity1 = StockItemPriceEntity.builder()
            .stockItemPriceId(STOCK_ITEM_PRICE_ID_1)
            .stockItemId(STOCK_ITEM_ID_1)
            .build();
        underTest.save(entity1);

        StockItemPriceEntity entity2 = StockItemPriceEntity.builder()
            .stockItemPriceId(STOCK_ITEM_PRICE_ID_2)
            .stockItemId(STOCK_ITEM_ID_2)
            .build();
        underTest.save(entity2);

        underTest.deleteByStockItemId(STOCK_ITEM_ID_1);

        assertThat(underTest.findAll()).containsExactly(entity2);
    }

    @Test
    void getByStockItemId() {
        StockItemPriceEntity entity1 = StockItemPriceEntity.builder()
            .stockItemPriceId(STOCK_ITEM_PRICE_ID_1)
            .stockItemId(STOCK_ITEM_ID_1)
            .build();
        underTest.save(entity1);

        StockItemPriceEntity entity2 = StockItemPriceEntity.builder()
            .stockItemPriceId(STOCK_ITEM_PRICE_ID_2)
            .stockItemId(STOCK_ITEM_ID_2)
            .build();
        underTest.save(entity2);

        assertThat(underTest.getByStockItemId(STOCK_ITEM_ID_1)).containsExactly(entity1);
    }
}