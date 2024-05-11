package com.github.saphyra.apphub.service.custom.villany_atesz.cart.service;

import com.github.saphyra.apphub.api.custom.villany_atesz.model.AddToCartRequest;
import com.github.saphyra.apphub.service.custom.villany_atesz.cart.dao.cart.CartDao;
import com.github.saphyra.apphub.service.custom.villany_atesz.stock.dao.item.StockItemDao;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class AddToCartRequestValidatorTest {
    private static final UUID STOCK_ITEM_ID = UUID.randomUUID();
    private static final Integer AMOUNT = 43;
    private static final UUID CART_ID = UUID.randomUUID();

    @Mock
    private CartDao cartDao;

    @Mock
    private StockItemDao stockItemDao;

    @InjectMocks
    private AddToCartRequestValidator underTest;

    @Test
    void nullCartId() {
        AddToCartRequest request = AddToCartRequest.builder()
            .cartId(null)
            .stockItemId(STOCK_ITEM_ID)
            .amount(AMOUNT)
            .build();

        Throwable ex = catchThrowable(() -> underTest.validate(request));

        ExceptionValidator.validateInvalidParam(ex, "cartId", "must not be null");
    }

    @Test
    void nullStockItemId() {
        AddToCartRequest request = AddToCartRequest.builder()
            .cartId(CART_ID)
            .stockItemId(null)
            .amount(AMOUNT)
            .build();

        Throwable ex = catchThrowable(() -> underTest.validate(request));

        ExceptionValidator.validateInvalidParam(ex, "stockItemId", "must not be null");
    }

    @Test
    void nullAmount() {
        AddToCartRequest request = AddToCartRequest.builder()
            .cartId(CART_ID)
            .stockItemId(STOCK_ITEM_ID)
            .amount(null)
            .build();

        Throwable ex = catchThrowable(() -> underTest.validate(request));

        ExceptionValidator.validateInvalidParam(ex, "amount", "must not be null");
    }

    @Test
    void zeroAmount() {
        AddToCartRequest request = AddToCartRequest.builder()
            .cartId(CART_ID)
            .stockItemId(STOCK_ITEM_ID)
            .amount(0)
            .build();

        Throwable ex = catchThrowable(() -> underTest.validate(request));

        ExceptionValidator.validateInvalidParam(ex, "amount", "must not be zero");
    }

    @Test
    void valid() {
        AddToCartRequest request = AddToCartRequest.builder()
            .cartId(CART_ID)
            .stockItemId(STOCK_ITEM_ID)
            .amount(AMOUNT)
            .build();

        underTest.validate(request);

        then(cartDao).should().findByIdValidated(CART_ID);
        then(stockItemDao).should().findByIdValidated(STOCK_ITEM_ID);
    }
}