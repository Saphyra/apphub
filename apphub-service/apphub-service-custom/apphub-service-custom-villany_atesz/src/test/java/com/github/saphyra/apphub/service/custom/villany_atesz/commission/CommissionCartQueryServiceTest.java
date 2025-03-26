package com.github.saphyra.apphub.service.custom.villany_atesz.commission;

import com.github.saphyra.apphub.api.custom.villany_atesz.model.CommissionCartResponse;
import com.github.saphyra.apphub.service.custom.villany_atesz.cart.dao.cart.Cart;
import com.github.saphyra.apphub.service.custom.villany_atesz.cart.dao.cart.CartDao;
import com.github.saphyra.apphub.service.custom.villany_atesz.cart.dao.item.CartItem;
import com.github.saphyra.apphub.service.custom.villany_atesz.cart.dao.item.CartItemDao;
import com.github.saphyra.apphub.service.custom.villany_atesz.cart.service.CartQueryService;
import com.github.saphyra.apphub.service.custom.villany_atesz.contacts.dao.Contact;
import com.github.saphyra.apphub.service.custom.villany_atesz.contacts.dao.ContactDao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class CommissionCartQueryServiceTest {
    private static final UUID CART_ID = UUID.randomUUID();
    private static final UUID CONTACT_ID = UUID.randomUUID();
    private static final String CONTACT_NAME = "contact-name";
    private static final Double MARGIN = 23d;
    private static final Integer CART_COST = 235;

    @Mock
    private CartDao cartDao;

    @Mock
    private ContactDao contactDao;

    @Mock
    private CartItemDao cartItemDao;

    @Mock
    private CartQueryService cartQueryService;

    @InjectMocks
    private CommissionCartQueryService underTest;

    @Mock
    private Cart cart;

    @Mock
    private Contact contact;

    @Mock
    private CartItem cartItem;

    @Test
    void getCart() {
        given(cartDao.findByIdValidated(CART_ID)).willReturn(cart);
        given(cart.getContactId()).willReturn(CONTACT_ID);
        given(contactDao.findByIdValidated(CONTACT_ID)).willReturn(contact);
        given(contact.getName()).willReturn(CONTACT_NAME);
        given(cart.getMargin()).willReturn(MARGIN);
        given(cartItemDao.getByCartId(CART_ID)).willReturn(List.of(cartItem));
        given(cartQueryService.getTotalPrice(List.of(cartItem))).willReturn(CART_COST);

        assertThat(underTest.getCart(CART_ID))
            .returns(CART_ID, CommissionCartResponse::getCartId)
            .returns(CONTACT_NAME, CommissionCartResponse::getContactName)
            .returns(CART_COST, CommissionCartResponse::getCartCost)
            .returns(MARGIN, CommissionCartResponse::getMargin);

    }
}