package com.github.saphyra.apphub.service.custom.villany_atesz.stock.service.inventory;

import com.github.saphyra.apphub.api.custom.villany_atesz.model.StockItemInventoryResponse;
import com.github.saphyra.apphub.service.custom.villany_atesz.cart.dao.cart.Cart;
import com.github.saphyra.apphub.service.custom.villany_atesz.cart.dao.cart.CartDao;
import com.github.saphyra.apphub.service.custom.villany_atesz.cart.dao.item.CartItem;
import com.github.saphyra.apphub.service.custom.villany_atesz.cart.dao.item.CartItemDao;
import com.github.saphyra.apphub.service.custom.villany_atesz.stock.dao.item.StockItem;
import com.github.saphyra.apphub.service.custom.villany_atesz.stock.dao.item.StockItemDao;
import com.github.saphyra.apphub.test.common.CustomAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class StockItemInventoryQueryServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID STOCK_ITEM_ID = UUID.randomUUID();
    private static final UUID CART_ID = UUID.randomUUID();
    private static final UUID STOCK_CATEGORY_ID = UUID.randomUUID();
    private static final String NAME = "name";
    private static final String SERIAL_NUMBER = "serial-number";
    private static final Integer IN_CAR = 64;
    private static final Integer IN_STORAGE = 643;
    private static final String BAR_CODE = "bar_code";

    @Mock
    private StockItemDao stockItemDao;

    @Mock
    private CartDao cartDao;

    @Mock
    private CartItemDao cartItemDao;

    @InjectMocks
    private StockItemInventoryQueryService underTest;

    @Mock
    private StockItem stockItem;

    @Mock
    private CartItem cartItem;

    @Mock
    private Cart cart;

    @Test
    void getItems_inCart() {
        given(stockItemDao.getByUserId(USER_ID)).willReturn(List.of(stockItem));
        given(stockItem.getStockItemId()).willReturn(STOCK_ITEM_ID);
        given(cartItemDao.getByStockItemId(STOCK_ITEM_ID)).willReturn(List.of(cartItem));
        given(cartItem.getCartId()).willReturn(CART_ID);
        given(cartDao.findByIdValidated(CART_ID)).willReturn(cart);
        given(cart.isFinalized()).willReturn(false);

        given(stockItem.getStockCategoryId()).willReturn(STOCK_CATEGORY_ID);
        given(stockItem.getName()).willReturn(NAME);
        given(stockItem.getBarCode()).willReturn(BAR_CODE);
        given(stockItem.getSerialNumber()).willReturn(SERIAL_NUMBER);
        given(stockItem.getInCar()).willReturn(IN_CAR);
        given(stockItem.getInStorage()).willReturn(IN_STORAGE);
        given(stockItem.isInventoried()).willReturn(true);

        CustomAssertions.singleListAssertThat(underTest.getItems(USER_ID))
            .returns(STOCK_ITEM_ID, StockItemInventoryResponse::getStockItemId)
            .returns(STOCK_CATEGORY_ID, StockItemInventoryResponse::getStockCategoryId)
            .returns(NAME, StockItemInventoryResponse::getName)
            .returns(SERIAL_NUMBER, StockItemInventoryResponse::getSerialNumber)
            .returns(BAR_CODE, StockItemInventoryResponse::getBarCode)
            .returns(IN_CAR, StockItemInventoryResponse::getInCar)
            .returns(IN_STORAGE, StockItemInventoryResponse::getInStorage)
            .returns(true, StockItemInventoryResponse::getInventoried)
            .returns(true, StockItemInventoryResponse::getInCart);
    }

    @Test
    void getItems_inFinalizedCart() {
        given(stockItemDao.getByUserId(USER_ID)).willReturn(List.of(stockItem));
        given(stockItem.getStockItemId()).willReturn(STOCK_ITEM_ID);
        given(cartItemDao.getByStockItemId(STOCK_ITEM_ID)).willReturn(List.of(cartItem));
        given(cartItem.getCartId()).willReturn(CART_ID);
        given(cartDao.findByIdValidated(CART_ID)).willReturn(cart);
        given(cart.isFinalized()).willReturn(true);

        given(stockItem.getStockCategoryId()).willReturn(STOCK_CATEGORY_ID);
        given(stockItem.getName()).willReturn(NAME);
        given(stockItem.getSerialNumber()).willReturn(SERIAL_NUMBER);
        given(stockItem.getBarCode()).willReturn(BAR_CODE);
        given(stockItem.getInCar()).willReturn(IN_CAR);
        given(stockItem.getInStorage()).willReturn(IN_STORAGE);
        given(stockItem.isInventoried()).willReturn(true);

        CustomAssertions.singleListAssertThat(underTest.getItems(USER_ID))
            .returns(STOCK_ITEM_ID, StockItemInventoryResponse::getStockItemId)
            .returns(STOCK_CATEGORY_ID, StockItemInventoryResponse::getStockCategoryId)
            .returns(NAME, StockItemInventoryResponse::getName)
            .returns(SERIAL_NUMBER, StockItemInventoryResponse::getSerialNumber)
            .returns(BAR_CODE, StockItemInventoryResponse::getBarCode)
            .returns(IN_CAR, StockItemInventoryResponse::getInCar)
            .returns(IN_STORAGE, StockItemInventoryResponse::getInStorage)
            .returns(true, StockItemInventoryResponse::getInventoried)
            .returns(false, StockItemInventoryResponse::getInCart);
    }

    @Test
    void getItems_notInCart() {
        given(stockItemDao.getByUserId(USER_ID)).willReturn(List.of(stockItem));
        given(stockItem.getStockItemId()).willReturn(STOCK_ITEM_ID);
        given(cartItemDao.getByStockItemId(STOCK_ITEM_ID)).willReturn(List.of());

        given(stockItem.getStockCategoryId()).willReturn(STOCK_CATEGORY_ID);
        given(stockItem.getName()).willReturn(NAME);
        given(stockItem.getSerialNumber()).willReturn(SERIAL_NUMBER);
        given(stockItem.getBarCode()).willReturn(BAR_CODE);
        given(stockItem.getInCar()).willReturn(IN_CAR);
        given(stockItem.getInStorage()).willReturn(IN_STORAGE);
        given(stockItem.isInventoried()).willReturn(true);

        CustomAssertions.singleListAssertThat(underTest.getItems(USER_ID))
            .returns(STOCK_ITEM_ID, StockItemInventoryResponse::getStockItemId)
            .returns(STOCK_CATEGORY_ID, StockItemInventoryResponse::getStockCategoryId)
            .returns(NAME, StockItemInventoryResponse::getName)
            .returns(SERIAL_NUMBER, StockItemInventoryResponse::getSerialNumber)
            .returns(BAR_CODE, StockItemInventoryResponse::getBarCode)
            .returns(IN_CAR, StockItemInventoryResponse::getInCar)
            .returns(IN_STORAGE, StockItemInventoryResponse::getInStorage)
            .returns(true, StockItemInventoryResponse::getInventoried)
            .returns(false, StockItemInventoryResponse::getInCart);
    }
}