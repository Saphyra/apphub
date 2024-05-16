package com.github.saphyra.apphub.service.custom.villany_atesz.cart;

import com.github.saphyra.apphub.api.custom.villany_atesz.model.AddToCartRequest;
import com.github.saphyra.apphub.api.custom.villany_atesz.model.CartDeletedResponse;
import com.github.saphyra.apphub.api.custom.villany_atesz.model.CartModifiedResponse;
import com.github.saphyra.apphub.api.custom.villany_atesz.model.CartResponse;
import com.github.saphyra.apphub.api.custom.villany_atesz.model.CartView;
import com.github.saphyra.apphub.api.custom.villany_atesz.model.StockItemOverviewResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.service.custom.villany_atesz.cart.service.AddToCartService;
import com.github.saphyra.apphub.service.custom.villany_atesz.cart.service.CartQueryService;
import com.github.saphyra.apphub.service.custom.villany_atesz.cart.service.CreateCartService;
import com.github.saphyra.apphub.service.custom.villany_atesz.cart.service.DeleteCartService;
import com.github.saphyra.apphub.service.custom.villany_atesz.cart.service.EditMarginService;
import com.github.saphyra.apphub.service.custom.villany_atesz.cart.service.FinalizeCartService;
import com.github.saphyra.apphub.service.custom.villany_atesz.cart.service.RemoveFromCartService;
import com.github.saphyra.apphub.service.custom.villany_atesz.stock.service.item.StockItemQueryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class CartControllerImplTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID CONTACT_ID = UUID.randomUUID();
    private static final UUID CART_ID = UUID.randomUUID();
    private static final UUID STOCK_ITEM_ID = UUID.randomUUID();
    private static final Double MARGIN = 3.5;

    @Mock
    private CreateCartService createCartService;

    @Mock
    private CartQueryService cartQueryService;

    @Mock
    private AddToCartService addToCartService;

    @Mock
    private StockItemQueryService stockItemQueryService;

    @Mock
    private FinalizeCartService finalizeCartService;

    @Mock
    private DeleteCartService deleteCartService;

    @Mock
    private RemoveFromCartService removeFromCartService;

    @Mock
    private EditMarginService editMarginService;

    @InjectMocks
    private CartControllerImpl underTest;

    @Mock
    private AccessTokenHeader accessTokenHeader;

    @Mock
    private CartResponse cartResponse;

    @Mock
    private CartView cartView;

    @Mock
    private AddToCartRequest addToCartRequest;

    @Mock
    private StockItemOverviewResponse stockItemOverviewResponse;

    @BeforeEach
    void setUp() {
        given(accessTokenHeader.getUserId()).willReturn(USER_ID);
    }

    @Test
    void createCart() {
        given(createCartService.create(USER_ID, CONTACT_ID)).willReturn(CART_ID);

        assertThat(underTest.createCart(new OneParamRequest<>(CONTACT_ID), accessTokenHeader))
            .isEqualTo(CART_ID);
    }

    @Test
    void getCarts() {
        given(cartQueryService.getCarts(USER_ID)).willReturn(List.of(cartResponse));

        assertThat(underTest.getCarts(accessTokenHeader)).containsExactly(cartResponse);
    }

    @Test
    void getCart() {
        given(cartQueryService.getCart(CART_ID)).willReturn(cartView);

        assertThat(underTest.getCart(CART_ID, accessTokenHeader)).isEqualTo(cartView);
    }

    @Test
    void addToCart() {
        given(addToCartRequest.getCartId()).willReturn(CART_ID);
        given(cartQueryService.getCart(CART_ID)).willReturn(cartView);
        given(stockItemQueryService.getStockItems(USER_ID)).willReturn(List.of(stockItemOverviewResponse));

        assertThat(underTest.addToCart(addToCartRequest, accessTokenHeader))
            .returns(cartView, CartModifiedResponse::getCart)
            .returns(List.of(stockItemOverviewResponse), CartModifiedResponse::getItems);

        then(addToCartService).should().addToCart(USER_ID, addToCartRequest);
    }

    @Test
    void removeFromCart() {
        given(cartQueryService.getCart(CART_ID)).willReturn(cartView);
        given(stockItemQueryService.getStockItems(USER_ID)).willReturn(List.of(stockItemOverviewResponse));

        assertThat(underTest.removeFromCart(CART_ID, STOCK_ITEM_ID, accessTokenHeader))
            .returns(cartView, CartModifiedResponse::getCart)
            .returns(List.of(stockItemOverviewResponse), CartModifiedResponse::getItems);

        then(removeFromCartService).should().removeFromCart(CART_ID, STOCK_ITEM_ID);
    }

    @Test
    void finalizeCart() {
        given(stockItemQueryService.getStockItems(USER_ID)).willReturn(List.of(stockItemOverviewResponse));
        given(cartQueryService.getCarts(USER_ID)).willReturn(List.of(cartResponse));

        assertThat(underTest.finalizeCart(CART_ID, accessTokenHeader))
            .returns(List.of(cartResponse), CartDeletedResponse::getCarts)
            .returns(List.of(stockItemOverviewResponse), CartDeletedResponse::getItems);

        then(finalizeCartService).should().finalizeCart(CART_ID);
    }

    @Test
    void deleteCart() {
        given(stockItemQueryService.getStockItems(USER_ID)).willReturn(List.of(stockItemOverviewResponse));
        given(cartQueryService.getCarts(USER_ID)).willReturn(List.of(cartResponse));

        assertThat(underTest.deleteCart(CART_ID, accessTokenHeader))
            .returns(List.of(cartResponse), CartDeletedResponse::getCarts)
            .returns(List.of(stockItemOverviewResponse), CartDeletedResponse::getItems);

        then(deleteCartService).should().delete(USER_ID, CART_ID);
    }

    @Test
    void editMargin() {
        underTest.editMargin(new OneParamRequest<>(MARGIN), CART_ID, accessTokenHeader);

        then(editMarginService).should().editMargin(CART_ID, MARGIN);
    }
}