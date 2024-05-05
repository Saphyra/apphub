package com.github.saphyra.apphub.service.custom.villany_atesz.cart.service;

import com.github.saphyra.apphub.api.custom.villany_atesz.model.AddToCartRequest;
import com.github.saphyra.apphub.service.custom.villany_atesz.cart.dao.item.CartItem;
import com.github.saphyra.apphub.service.custom.villany_atesz.cart.dao.item.CartItemDao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class AddToCartServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();

    @Mock
    private AddToCartRequestValidator addToCartRequestValidator;

    @Mock
    private CartItemFactory cartItemFactory;

    @Mock
    private CartItemDao cartItemDao;

    @InjectMocks
    private AddToCartService underTest;

    @Mock
    private AddToCartRequest request;

    @Mock
    private CartItem cartItem;

    @Test
    void addToCart() {
        given(cartItemFactory.create(USER_ID, request)).willReturn(cartItem);

        underTest.addToCart(USER_ID, request);

        then(addToCartRequestValidator).should().validate(request);
        then(cartItemDao).should().save(cartItem);
    }
}