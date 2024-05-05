package com.github.saphyra.apphub.service.custom.villany_atesz.cart.dao.item;

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
class CartItemRepositoryTest {
    private static final String CART_ITEM_ID_1 = "cart-item-id-1";
    private static final String CART_ITEM_ID_2 = "cart-item-id-2";
    private static final String CART_ITEM_ID_3 = "cart-item-id-3";
    private static final String USER_ID_1 = "user-id-1";
    private static final String USER_ID_2 = "user-id-2";
    private static final String CART_ID_1 = "cart-id-1";
    private static final String CART_ID_2 = "cart-id-2";
    private static final String STOCK_ITEM_ID_1 = "stock-item-id-1";
    private static final String STOCK_ITEM_ID_2 = "stock-item-id-2";

    @Autowired
    private CartItemRepository underTest;

    @AfterEach
    public void clear() {
        underTest.deleteAll();
    }

    @Test
    @Transactional
    void deleteByUserId() {
        CartItemEntity entity1 = CartItemEntity.builder()
            .cartItemId(CART_ITEM_ID_1)
            .userId(USER_ID_1)
            .build();
        underTest.save(entity1);

        CartItemEntity entity2 = CartItemEntity.builder()
            .cartItemId(CART_ITEM_ID_2)
            .userId(USER_ID_2)
            .build();
        underTest.save(entity2);

        underTest.deleteByUserId(USER_ID_1);

        assertThat(underTest.findAll()).containsExactly(entity2);
    }

    @Test
    void getByCartId() {
        CartItemEntity entity1 = CartItemEntity.builder()
            .cartItemId(CART_ITEM_ID_1)
            .cartId(CART_ID_1)
            .build();
        underTest.save(entity1);

        CartItemEntity entity2 = CartItemEntity.builder()
            .cartItemId(CART_ITEM_ID_2)
            .cartId(CART_ID_2)
            .build();
        underTest.save(entity2);

        assertThat(underTest.getByCartId(CART_ID_1)).containsExactly(entity1);
    }

    @Test
    void getByCartIdAndStockItemId() {
        CartItemEntity entity1 = CartItemEntity.builder()
            .cartItemId(CART_ITEM_ID_1)
            .cartId(CART_ID_1)
            .stockItemId(STOCK_ITEM_ID_1)
            .build();
        underTest.save(entity1);

        CartItemEntity entity2 = CartItemEntity.builder()
            .cartItemId(CART_ITEM_ID_2)
            .cartId(CART_ID_2)
            .stockItemId(STOCK_ITEM_ID_1)
            .build();
        underTest.save(entity2);

        CartItemEntity entity3 = CartItemEntity.builder()
            .cartItemId(CART_ITEM_ID_3)
            .cartId(CART_ID_1)
            .stockItemId(STOCK_ITEM_ID_2)
            .build();
        underTest.save(entity3);

        assertThat(underTest.getByCartIdAndStockItemId(CART_ID_1, STOCK_ITEM_ID_1)).containsExactly(entity1);
    }

    @Test
    @Transactional
    void deleteByUserIdAndCartId() {
        CartItemEntity entity1 = CartItemEntity.builder()
            .cartItemId(CART_ITEM_ID_1)
            .cartId(CART_ID_1)
            .userId(USER_ID_1)
            .build();
        underTest.save(entity1);

        CartItemEntity entity2 = CartItemEntity.builder()
            .cartItemId(CART_ITEM_ID_2)
            .cartId(CART_ID_2)
            .userId(USER_ID_1)
            .build();
        underTest.save(entity2);

        CartItemEntity entity3 = CartItemEntity.builder()
            .cartItemId(CART_ITEM_ID_3)
            .cartId(CART_ID_1)
            .userId(USER_ID_2)
            .build();
        underTest.save(entity3);

        underTest.deleteByUserIdAndCartId(USER_ID_1, CART_ID_1);

        assertThat(underTest.findAll()).containsExactlyInAnyOrder(entity2, entity3);
    }

    @Test
    @Transactional
    void deleteByCartIdAndStockItemId() {
        CartItemEntity entity1 = CartItemEntity.builder()
            .cartItemId(CART_ITEM_ID_1)
            .cartId(CART_ID_1)
            .stockItemId(STOCK_ITEM_ID_1)
            .build();
        underTest.save(entity1);

        CartItemEntity entity2 = CartItemEntity.builder()
            .cartItemId(CART_ITEM_ID_2)
            .cartId(CART_ID_2)
            .stockItemId(STOCK_ITEM_ID_1)
            .build();
        underTest.save(entity2);

        CartItemEntity entity3 = CartItemEntity.builder()
            .cartItemId(CART_ITEM_ID_3)
            .cartId(CART_ID_1)
            .stockItemId(STOCK_ITEM_ID_2)
            .build();
        underTest.save(entity3);

        underTest.deleteByCartIdAndStockItemId(CART_ID_1, STOCK_ITEM_ID_1);

        assertThat(underTest.findAll()).containsExactlyInAnyOrder(entity2, entity3);
    }
}