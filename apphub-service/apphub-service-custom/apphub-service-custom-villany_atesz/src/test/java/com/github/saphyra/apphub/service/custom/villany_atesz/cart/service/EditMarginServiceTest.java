package com.github.saphyra.apphub.service.custom.villany_atesz.cart.service;

import com.github.saphyra.apphub.service.custom.villany_atesz.cart.dao.cart.Cart;
import com.github.saphyra.apphub.service.custom.villany_atesz.cart.dao.cart.CartDao;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class EditMarginServiceTest {
    private static final UUID CART_ID = UUID.randomUUID();
    private static final Double MARGIN = 3.6;

    @Mock
    private CartDao cartDao;

    @InjectMocks
    private EditMarginService underTest;

    @Mock
    private Cart cart;

    @Test
    void nullMargin() {
        ExceptionValidator.validateInvalidParam(() -> underTest.editMargin(CART_ID, null), "margin", "must not be null");
    }

    @Test
    void negativeMargin() {
        ExceptionValidator.validateInvalidParam(() -> underTest.editMargin(CART_ID, -1d), "margin", "too low");
    }

    @Test
    void editMargin() {
        given(cartDao.findByIdValidated(CART_ID)).willReturn(cart);

        underTest.editMargin(CART_ID, MARGIN);

        then(cart).should().setMargin(MARGIN);
        then(cartDao).should().save(cart);
    }
}