package com.github.saphyra.apphub.service.custom.villany_atesz.cart.service;

import com.github.saphyra.apphub.api.custom.villany_atesz.model.CartItemView;
import com.github.saphyra.apphub.api.custom.villany_atesz.model.CartResponse;
import com.github.saphyra.apphub.api.custom.villany_atesz.model.CartView;
import com.github.saphyra.apphub.api.custom.villany_atesz.model.ContactModel;
import com.github.saphyra.apphub.service.custom.villany_atesz.cart.dao.cart.Cart;
import com.github.saphyra.apphub.service.custom.villany_atesz.cart.dao.cart.CartDao;
import com.github.saphyra.apphub.service.custom.villany_atesz.cart.dao.item.CartItem;
import com.github.saphyra.apphub.service.custom.villany_atesz.cart.dao.item.CartItemDao;
import com.github.saphyra.apphub.service.custom.villany_atesz.contacts.service.ContactQueryService;
import com.github.saphyra.apphub.service.custom.villany_atesz.stock.dao.item.StockItem;
import com.github.saphyra.apphub.service.custom.villany_atesz.stock.dao.item.StockItemDao;
import com.github.saphyra.apphub.service.custom.villany_atesz.stock.service.price.StockItemPriceQueryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static com.github.saphyra.apphub.test.common.CustomAssertions.singleListAssertThat;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class CartQueryServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID CART_ID = UUID.randomUUID();
    private static final UUID CONTACT_ID = UUID.randomUUID();
    private static final UUID STOCK_ITEM_ID = UUID.randomUUID();
    private static final Integer PRICE_PER_ITEM = 32;
    private static final Integer AMOUNT = 53;
    private static final String STOCK_ITEM_NAME = "stock-item-name";
    private static final Double MARGIN = 3.5;

    @Mock
    private CartDao cartDao;

    @Mock
    private ContactQueryService contactQueryService;

    @Mock
    private CartItemDao cartItemDao;

    @Mock
    private StockItemPriceQueryService stockItemPriceQueryService;

    @Mock
    private StockItemDao stockItemDao;

    @InjectMocks
    private CartQueryService underTest;

    @Mock
    private Cart cart;

    @Mock
    private ContactModel contactModel;

    @Mock
    private CartItem cartItem;

    @Mock
    private StockItem stockItem;

    @Test
    void getCarts() {
        given(cartDao.getByUserId(USER_ID)).willReturn(List.of(cart));
        given(cart.getCartId()).willReturn(CART_ID);
        given(cart.getContactId()).willReturn(CONTACT_ID);
        given(cart.isFinalized()).willReturn(false);
        given(contactQueryService.getContact(CONTACT_ID)).willReturn(contactModel);

        singleListAssertThat(underTest.getCarts(USER_ID))
            .returns(CART_ID, CartResponse::getCartId)
            .returns(contactModel, CartResponse::getContact);
    }

    @Test
    void getCarts_filterFinalized() {
        given(cartDao.getByUserId(USER_ID)).willReturn(List.of(cart));
        given(cart.isFinalized()).willReturn(true);

        assertThat(underTest.getCarts(USER_ID)).isEmpty();
    }

    @Test
    void getCart() {
        given(cartDao.findByIdValidated(CART_ID)).willReturn(cart);
        given(cartItemDao.getByCartId(CART_ID)).willReturn(List.of(cartItem));
        given(contactQueryService.getContact(CONTACT_ID)).willReturn(contactModel);
        given(cart.getContactId()).willReturn(CONTACT_ID);
        given(cartItem.getStockItemId()).willReturn(STOCK_ITEM_ID);
        given(stockItemDao.findByIdValidated(STOCK_ITEM_ID)).willReturn(stockItem);
        given(stockItemPriceQueryService.getPriceOf(STOCK_ITEM_ID)).willReturn(PRICE_PER_ITEM);
        given(cartItem.getAmount()).willReturn(AMOUNT);
        given(stockItem.getName()).willReturn(STOCK_ITEM_NAME);
        given(cart.getMargin()).willReturn(MARGIN);

        assertThat(underTest.getCart(CART_ID))
            .returns(CART_ID, CartView::getCartId)
            .returns(contactModel, CartView::getContact)
            .returns(PRICE_PER_ITEM * AMOUNT, CartView::getTotalPrice)
            .returns(MARGIN, CartView::getMargin)
            .extracting(cartView -> cartView.getItems().get(0))
            .returns(STOCK_ITEM_ID, CartItemView::getStockItemId)
            .returns(STOCK_ITEM_NAME, CartItemView::getName)
            .returns(AMOUNT, CartItemView::getAmount);
    }
}