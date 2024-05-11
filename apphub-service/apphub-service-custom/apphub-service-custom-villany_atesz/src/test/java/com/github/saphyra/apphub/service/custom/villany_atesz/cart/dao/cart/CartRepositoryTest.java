package com.github.saphyra.apphub.service.custom.villany_atesz.cart.dao.cart;

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
class CartRepositoryTest {
    private static final String CART_ID_1 = "cart-id-1";
    private static final String CART_ID_2 = "cart-id-2";
    private static final String CART_ID_3 = "cart-id-3";
    private static final String USER_ID_1 = "user-id-1";
    private static final String USER_ID_2 = "user-id-2";
    private static final String CONTACT_ID_1 = "contact-id-1";
    private static final String CONTACT_ID_2 = "contact-id-2";

    @Autowired
    private CartRepository underTest;

    @AfterEach
    public void clear() {
        underTest.deleteAll();
    }

    @Test
    @Transactional
    void deleteByUserId() {
        CartEntity entity1 = CartEntity.builder()
            .cartId(CART_ID_1)
            .userId(USER_ID_1)
            .build();
        underTest.save(entity1);

        CartEntity entity2 = CartEntity.builder()
            .cartId(CART_ID_2)
            .userId(USER_ID_2)
            .build();
        underTest.save(entity2);

        underTest.deleteByUserId(USER_ID_1);

        assertThat(underTest.findAll()).containsExactly(entity2);
    }

    @Test
    void getByUserId() {
        CartEntity entity1 = CartEntity.builder()
            .cartId(CART_ID_1)
            .userId(USER_ID_1)
            .build();
        underTest.save(entity1);

        CartEntity entity2 = CartEntity.builder()
            .cartId(CART_ID_2)
            .userId(USER_ID_2)
            .build();
        underTest.save(entity2);

        assertThat(underTest.getByUserId(USER_ID_1)).containsExactly(entity1);
    }

    @Test
    @Transactional
    void deleteByUserIdAndCartId() {
        CartEntity entity1 = CartEntity.builder()
            .cartId(CART_ID_1)
            .userId(USER_ID_1)
            .build();
        underTest.save(entity1);

        CartEntity entity2 = CartEntity.builder()
            .cartId(CART_ID_2)
            .userId(USER_ID_2)
            .build();
        underTest.save(entity2);

        underTest.deleteByUserIdAndCartId(USER_ID_1, CART_ID_1);

        assertThat(underTest.findAll()).containsExactly(entity2);
    }

    @Test
    void getByUserIdAndContactId() {
        CartEntity entity1 = CartEntity.builder()
            .cartId(CART_ID_1)
            .userId(USER_ID_1)
            .contactId(CONTACT_ID_1)
            .build();
        underTest.save(entity1);

        CartEntity entity2 = CartEntity.builder()
            .cartId(CART_ID_2)
            .userId(USER_ID_1)
            .contactId(CONTACT_ID_2)
            .build();
        underTest.save(entity2);

        CartEntity entity3 = CartEntity.builder()
            .cartId(CART_ID_3)
            .userId(USER_ID_1)
            .contactId(CONTACT_ID_2)
            .build();
        underTest.save(entity3);

        assertThat(underTest.getByUserIdAndContactId(USER_ID_1, CONTACT_ID_1)).containsExactly(entity1);
    }
}