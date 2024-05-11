package com.github.saphyra.apphub.service.custom.villany_atesz.stock.dao.item;

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
class StockItemRepositoryTest {
    private static final String STOCK_ITEM_ID_1 = "stock-item-id-1";
    private static final String STOCK_ITEM_ID_2 = "stock-item-id-2";
    private static final String USER_ID_1 = "user-id-1";
    private static final String USER_ID_2 = "user-id-2";
    private static final String STOCK_CATEGORY_ID_1 = "stock-category-id-1";
    private static final String STOCK_CATEGORY_ID_2 = "stock-category-id-2";

    @Autowired
    private StockItemRepository underTest;

    @AfterEach
    void clear() {
        underTest.deleteAll();
    }

    @Test
    @Transactional
    void deleteByUserId() {
        StockItemEntity entity1 = StockItemEntity.builder()
            .stockItemId(STOCK_ITEM_ID_1)
            .userId(USER_ID_1)
            .build();
        underTest.save(entity1);

        StockItemEntity entity2 = StockItemEntity.builder()
            .stockItemId(STOCK_ITEM_ID_2)
            .userId(USER_ID_2)
            .build();
        underTest.save(entity2);

        underTest.deleteByUserId(USER_ID_1);

        assertThat(underTest.findAll()).containsExactly(entity2);
    }

    @Test
    @Transactional
    void deleteByUserIdAndStockItemId() {
        StockItemEntity entity1 = StockItemEntity.builder()
            .stockItemId(STOCK_ITEM_ID_1)
            .userId(USER_ID_1)
            .build();
        underTest.save(entity1);

        StockItemEntity entity2 = StockItemEntity.builder()
            .stockItemId(STOCK_ITEM_ID_2)
            .userId(USER_ID_2)
            .build();
        underTest.save(entity2);

        underTest.deleteByUserIdAndStockItemId(USER_ID_1, STOCK_ITEM_ID_1);

        assertThat(underTest.findAll()).containsExactly(entity2);
    }

    @Test
    void getByStockCategoryId() {
        StockItemEntity entity1 = StockItemEntity.builder()
            .stockItemId(STOCK_ITEM_ID_1)
            .stockCategoryId(STOCK_CATEGORY_ID_1)
            .build();
        underTest.save(entity1);

        StockItemEntity entity2 = StockItemEntity.builder()
            .stockItemId(STOCK_ITEM_ID_2)
            .stockCategoryId(STOCK_CATEGORY_ID_2)
            .build();
        underTest.save(entity2);

        assertThat(underTest.getByStockCategoryId(STOCK_CATEGORY_ID_1)).containsExactly(entity1);
    }

    @Test
    @Transactional
    void getByUserId() {
        StockItemEntity entity1 = StockItemEntity.builder()
            .stockItemId(STOCK_ITEM_ID_1)
            .userId(USER_ID_1)
            .build();
        underTest.save(entity1);

        StockItemEntity entity2 = StockItemEntity.builder()
            .stockItemId(STOCK_ITEM_ID_2)
            .userId(USER_ID_2)
            .build();
        underTest.save(entity2);

        assertThat(underTest.getByUserId(USER_ID_1)).containsExactly(entity1);
    }
}