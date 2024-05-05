package com.github.saphyra.apphub.service.custom.villany_atesz.cart.service;

import com.github.saphyra.apphub.service.custom.villany_atesz.cart.dao.cart.CartDao;
import com.github.saphyra.apphub.service.custom.villany_atesz.cart.dao.item.CartItemDao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class DeleteCartServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID CART_ID = UUID.randomUUID();

    @Mock
    private CartDao cartDao;

    @Mock
    private CartItemDao cartItemDao;

    @InjectMocks
    private DeleteCartService underTest;

    @Test
    void delete() {
        underTest.delete(USER_ID, CART_ID);

        then(cartDao).should().deleteByUserIdAndCartId(USER_ID, CART_ID);
        then(cartItemDao).should().deleteByUserIdAndCartId(USER_ID, CART_ID);
    }
}