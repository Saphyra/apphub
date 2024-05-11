package com.github.saphyra.apphub.service.custom.villany_atesz.cart.service;

import com.github.saphyra.apphub.api.custom.villany_atesz.model.AddToCartRequest;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.custom.villany_atesz.cart.dao.item.CartItem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class CartItemFactoryTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID CART_ID = UUID.randomUUID();
    private static final UUID STOCK_ITEM_ID = UUID.randomUUID();
    private static final Integer AMOUNT = 43;
    private static final UUID CART_ITEM_ID = UUID.randomUUID();

    @Mock
    private IdGenerator idGenerator;

    @InjectMocks
    private CartItemFactory underTest;

    @Test
    void create() {
        AddToCartRequest request = AddToCartRequest.builder()
            .cartId(CART_ID)
            .stockItemId(STOCK_ITEM_ID)
            .amount(AMOUNT)
            .build();

        given(idGenerator.randomUuid()).willReturn(CART_ITEM_ID);

        assertThat(underTest.create(USER_ID, request))
            .returns(CART_ITEM_ID, CartItem::getCartItemId)
            .returns(USER_ID, CartItem::getUserId)
            .returns(CART_ID, CartItem::getCartId)
            .returns(STOCK_ITEM_ID, CartItem::getStockItemId)
            .returns(AMOUNT, CartItem::getAmount);
    }
}