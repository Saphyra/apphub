package com.github.saphyra.apphub.service.custom.villany_atesz.cart.service;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.service.custom.villany_atesz.cart.dao.cart.Cart;
import com.github.saphyra.apphub.service.custom.villany_atesz.cart.dao.cart.CartDao;
import com.github.saphyra.apphub.service.custom.villany_atesz.cart.dao.item.CartItem;
import com.github.saphyra.apphub.service.custom.villany_atesz.cart.dao.item.CartItemDao;
import com.github.saphyra.apphub.service.custom.villany_atesz.stock.dao.item.StockItem;
import com.github.saphyra.apphub.service.custom.villany_atesz.stock.dao.item.StockItemDao;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class izeCartServiceTest {
    private static final UUID CART_ID = UUID.randomUUID();
    private static final Integer CART_ITEM_AMOUNT = 23;
    private static final UUID STOCK_ITEM_ID = UUID.randomUUID();
    private static final Integer IN_CAR = 53;

    @Mock
    private CartDao cartDao;

    @Mock
    private CartItemDao cartItemDao;

    @Mock
    private StockItemDao stockItemDao;

    @InjectMocks
    private FinalizeCartService underTest;

    @Mock
    private Cart cart;

    @Mock
    private CartItem cartItem;

    @Mock
    private StockItem stockItem;

    @Test
    void alreadyFinalized() {
        given(cartDao.findByIdValidated(CART_ID)).willReturn(cart);
        given(cart.isFinalized()).willReturn(true);

        Throwable ex = catchThrowable(() -> underTest.finalizeCart(CART_ID));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.FORBIDDEN, ErrorCode.FORBIDDEN_OPERATION);
    }

    @Test
    void finalizeCart() {
        given(cartDao.findByIdValidated(CART_ID)).willReturn(cart);
        given(cart.isFinalized()).willReturn(false);
        given(cartItemDao.getByCartId(CART_ID)).willReturn(List.of(cartItem));
        given(cartItem.getAmount()).willReturn(CART_ITEM_AMOUNT);
        given(cartItem.getStockItemId()).willReturn(STOCK_ITEM_ID);
        given(stockItemDao.findByIdValidated(STOCK_ITEM_ID)).willReturn(stockItem);
        given(stockItem.getInCar()).willReturn(IN_CAR);

        underTest.finalizeCart(CART_ID);

        then(cart).should().setFinalized(true);
        then(cartDao).should().save(cart);
        then(stockItem).should().setInCar(IN_CAR - CART_ITEM_AMOUNT);
        then(stockItemDao).should().save(stockItem);
    }
}