package com.github.saphyra.apphub.service.custom.villany_atesz.cart.service;

import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.custom.villany_atesz.cart.dao.cart.Cart;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class CartFactoryTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID CONTACT_ID = UUID.randomUUID();
    private static final UUID CART_ID = UUID.randomUUID();
    private static final LocalDate CURRENT_DATE = LocalDate.now();

    @Mock
    private IdGenerator idGenerator;

    @Mock
    private DateTimeUtil dateTimeUtil;

    @InjectMocks
    private CartFactory underTest;

    @Test
    void create() {
        given(idGenerator.randomUuid()).willReturn(CART_ID);
        given(dateTimeUtil.getCurrentDate()).willReturn(CURRENT_DATE);

        assertThat(underTest.create(USER_ID, CONTACT_ID))
            .returns(CART_ID, Cart::getCartId)
            .returns(USER_ID, Cart::getUserId)
            .returns(CONTACT_ID, Cart::getContactId)
            .returns(CURRENT_DATE, Cart::getCreatedAt)
            .returns(1.0, Cart::getMargin)
            .returns(false, Cart::isFinalized);
    }
}