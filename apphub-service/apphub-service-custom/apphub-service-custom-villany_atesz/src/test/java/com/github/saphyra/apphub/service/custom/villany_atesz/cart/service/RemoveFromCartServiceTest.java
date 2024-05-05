package com.github.saphyra.apphub.service.custom.villany_atesz.cart.service;

import com.github.saphyra.apphub.service.custom.villany_atesz.cart.dao.item.CartItemDao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class RemoveFromCartServiceTest {
    private static final UUID CART_ID = UUID.randomUUID();
    private static final UUID STOCK_ITEM_ID = UUID.randomUUID();

    @Mock
    private CartItemDao cartItemDao;

    @InjectMocks
    private RemoveFromCartService underTest;

    @Test
    void removeFromCart() {
        underTest.removeFromCart(CART_ID, STOCK_ITEM_ID);

        then(cartItemDao).should().deleteByCartIdAndStockItemId(CART_ID, STOCK_ITEM_ID);
    }
}