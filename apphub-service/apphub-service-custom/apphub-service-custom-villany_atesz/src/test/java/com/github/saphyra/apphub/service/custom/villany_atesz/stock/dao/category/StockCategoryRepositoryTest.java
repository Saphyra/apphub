package com.github.saphyra.apphub.service.custom.villany_atesz.stock.dao.category;

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
class StockCategoryRepositoryTest {
    private static final String STOCK_CATEGORY_ID_1 = "stock-category-id-1";
    private static final String STOCK_CATEGORY_ID_2 = "stock-category-id-2";
    private static final String USER_ID_1 = "user-id-1";
    private static final String USER_ID_2 = "user-id-2";

    @Autowired
    private StockCategoryRepository underTest;

    @AfterEach
    void clear() {
        underTest.deleteAll();
    }

    @Test
    @Transactional
    void deleteByUserId() {
        StockCategoryEntity entity1 = StockCategoryEntity.builder()
            .stockCategoryId(STOCK_CATEGORY_ID_1)
            .userId(USER_ID_1)
            .build();
        underTest.save(entity1);

        StockCategoryEntity entity2 = StockCategoryEntity.builder()
            .stockCategoryId(STOCK_CATEGORY_ID_2)
            .userId(USER_ID_2)
            .build();
        underTest.save(entity2);

        underTest.deleteByUserId(USER_ID_1);

        assertThat(underTest.findAll()).containsExactly(entity2);
    }

    @Test
    @Transactional
    void deleteByUserIdAndStockCategoryId() {
        StockCategoryEntity entity1 = StockCategoryEntity.builder()
            .stockCategoryId(STOCK_CATEGORY_ID_1)
            .userId(USER_ID_1)
            .build();
        underTest.save(entity1);

        StockCategoryEntity entity2 = StockCategoryEntity.builder()
            .stockCategoryId(STOCK_CATEGORY_ID_2)
            .userId(USER_ID_2)
            .build();
        underTest.save(entity2);

        underTest.deleteByUserIdAndStockCategoryId(USER_ID_1, STOCK_CATEGORY_ID_1);

        assertThat(underTest.findAll()).containsExactly(entity2);
    }

    @Test
    void getByUserId() {
        StockCategoryEntity entity1 = StockCategoryEntity.builder()
            .stockCategoryId(STOCK_CATEGORY_ID_1)
            .userId(USER_ID_1)
            .build();
        underTest.save(entity1);

        StockCategoryEntity entity2 = StockCategoryEntity.builder()
            .stockCategoryId(STOCK_CATEGORY_ID_2)
            .userId(USER_ID_2)
            .build();
        underTest.save(entity2);

        assertThat(underTest.getByUserId(USER_ID_1)).containsExactly(entity1);
    }
}