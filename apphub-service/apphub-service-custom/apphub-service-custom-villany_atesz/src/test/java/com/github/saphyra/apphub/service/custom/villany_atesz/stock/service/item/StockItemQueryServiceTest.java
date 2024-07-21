package com.github.saphyra.apphub.service.custom.villany_atesz.stock.service.item;

import com.github.saphyra.apphub.api.custom.villany_atesz.model.StockCategoryModel;
import com.github.saphyra.apphub.api.custom.villany_atesz.model.StockItemAcquisitionResponse;
import com.github.saphyra.apphub.api.custom.villany_atesz.model.StockItemForCategoryResponse;
import com.github.saphyra.apphub.api.custom.villany_atesz.model.StockItemOverviewResponse;
import com.github.saphyra.apphub.api.custom.villany_atesz.model.StockItemResponse;
import com.github.saphyra.apphub.service.custom.villany_atesz.cart.dao.cart.Cart;
import com.github.saphyra.apphub.service.custom.villany_atesz.cart.dao.cart.CartDao;
import com.github.saphyra.apphub.service.custom.villany_atesz.cart.dao.item.CartItem;
import com.github.saphyra.apphub.service.custom.villany_atesz.cart.dao.item.CartItemDao;
import com.github.saphyra.apphub.service.custom.villany_atesz.stock.dao.item.StockItem;
import com.github.saphyra.apphub.service.custom.villany_atesz.stock.dao.item.StockItemDao;
import com.github.saphyra.apphub.service.custom.villany_atesz.stock.dao.price.StockItemPrice;
import com.github.saphyra.apphub.service.custom.villany_atesz.stock.dao.price.StockItemPriceDao;
import com.github.saphyra.apphub.service.custom.villany_atesz.stock.service.category.StockCategoryQueryService;
import com.github.saphyra.apphub.test.common.CustomAssertions;
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
public class StockItemQueryServiceTest {
    private static final UUID STOCK_CATEGORY_ID = UUID.randomUUID();
    private static final UUID STOCK_ITEM_ID = UUID.randomUUID();
    private static final String NAME = "name";
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String SERIAL_NUMBER = "serial-number";
    private static final Integer IN_CAR = 64;
    private static final UUID FINALIZED_CART_ID = UUID.randomUUID();
    private static final UUID OPEN_CART_ID = UUID.randomUUID();
    private static final Integer CART_ITEM_AMOUNT = 5647;
    private static final Integer IN_STORAGE = 35;
    private static final Integer PRICE = 475;
    private static final String BAR_CODE = "bar-code";

    @Mock
    private StockItemDao stockItemDao;

    @Mock
    private StockCategoryQueryService stockCategoryQueryService;

    @Mock
    private StockItemPriceDao stockItemPriceDao;

    @Mock
    private CartItemDao cartItemDao;

    @Mock
    private CartDao cartDao;

    @InjectMocks
    private StockItemQueryService underTest;

    @Mock
    private StockItem stockItem;

    @Mock
    private StockCategoryModel stockCategoryModel;

    @Mock
    private CartItem cartItem;

    @Mock
    private Cart finalizedCart;

    @Mock
    private Cart openCart;

    @Mock
    private StockItemPrice stockItemPrice;

    @Test
    void getForCategory() {
        given(stockItemDao.getByStockCategoryId(STOCK_CATEGORY_ID)).willReturn(List.of(stockItem));
        given(stockItem.getStockItemId()).willReturn(STOCK_ITEM_ID);
        given(stockItem.getName()).willReturn(NAME);

        CustomAssertions.singleListAssertThat(underTest.getForCategory(STOCK_CATEGORY_ID))
            .returns(STOCK_ITEM_ID, StockItemForCategoryResponse::getStockItemId)
            .returns(NAME, StockItemForCategoryResponse::getName);
    }

    @Test
    void getStockItems() {
        given(stockItemDao.getByUserId(USER_ID)).willReturn(List.of(stockItem));
        given(stockItem.getStockItemId()).willReturn(STOCK_ITEM_ID);
        given(stockItem.getStockCategoryId()).willReturn(STOCK_CATEGORY_ID);
        given(stockCategoryQueryService.findByStockCategoryId(STOCK_CATEGORY_ID)).willReturn(stockCategoryModel);
        given(stockItem.getName()).willReturn(NAME);
        given(stockItem.getSerialNumber()).willReturn(SERIAL_NUMBER);
        given(stockItem.getInCar()).willReturn(IN_CAR);
        given(stockItem.getInStorage()).willReturn(IN_STORAGE);
        given(cartItemDao.getByStockItemId(STOCK_ITEM_ID)).willReturn(List.of(cartItem, cartItem));
        given(cartItem.getCartId())
            .willReturn(FINALIZED_CART_ID)
            .willReturn(OPEN_CART_ID);
        given(cartDao.findByIdValidated(OPEN_CART_ID)).willReturn(openCart);
        given(openCart.isFinalized()).willReturn(false);
        given(cartDao.findByIdValidated(FINALIZED_CART_ID)).willReturn(finalizedCart);
        given(finalizedCart.isFinalized()).willReturn(true);
        given(cartItem.getAmount()).willReturn(CART_ITEM_AMOUNT);
        given(stockItemPriceDao.getByStockItemId(STOCK_ITEM_ID)).willReturn(List.of(stockItemPrice, stockItemPrice));
        given(stockItemPrice.getPrice())
            .willReturn(PRICE - 10)
            .willReturn(PRICE);
        given(stockItem.getBarCode()).willReturn(BAR_CODE);

        CustomAssertions.singleListAssertThat(underTest.getStockItems(USER_ID))
            .returns(STOCK_ITEM_ID, StockItemOverviewResponse::getStockItemId)
            .returns(stockCategoryModel, StockItemOverviewResponse::getCategory)
            .returns(NAME, StockItemOverviewResponse::getName)
            .returns(SERIAL_NUMBER, StockItemOverviewResponse::getSerialNumber)
            .returns(BAR_CODE, StockItemOverviewResponse::getBarCode)
            .returns(IN_CAR, StockItemOverviewResponse::getInCar)
            .returns(CART_ITEM_AMOUNT, StockItemOverviewResponse::getInCart)
            .returns(IN_STORAGE, StockItemOverviewResponse::getInStorage)
            .returns(PRICE, StockItemOverviewResponse::getPrice);
    }

    @Test
    void findByBarCode() {
        given(stockItemDao.getByUserId(USER_ID)).willReturn(List.of(stockItem));
        given(stockItem.getStockCategoryId()).willReturn(STOCK_CATEGORY_ID);
        given(stockItem.getStockItemId()).willReturn(STOCK_ITEM_ID);
        given(stockItem.getBarCode()).willReturn(BAR_CODE);

        CustomAssertions.optionalAssertThat(underTest.findByBarCode(USER_ID, BAR_CODE))
            .returns(STOCK_CATEGORY_ID, StockItemAcquisitionResponse::getStockCategoryId)
            .returns(STOCK_ITEM_ID, StockItemAcquisitionResponse::getStockItemId)
            .returns(BAR_CODE, StockItemAcquisitionResponse::getBarCode);
    }

    @Test
    void findBarCodeByStockItemId() {
        given(stockItemDao.findByIdValidated(STOCK_ITEM_ID)).willReturn(stockItem);
        given(stockItem.getBarCode()).willReturn(BAR_CODE);

        assertThat(underTest.findBarCodeByStockItemId(STOCK_ITEM_ID))
            .isEqualTo(BAR_CODE);
    }

    @Test
    void findByStockItemId() {
        given(stockItemDao.findByIdValidated(STOCK_ITEM_ID)).willReturn(stockItem);

        given(stockItem.getStockItemId()).willReturn(STOCK_ITEM_ID);
        given(stockItem.getStockCategoryId()).willReturn(STOCK_CATEGORY_ID);
        given(stockItem.getName()).willReturn(NAME);
        given(stockItem.getSerialNumber()).willReturn(SERIAL_NUMBER);
        given(stockItem.getBarCode()).willReturn(BAR_CODE);
        given(stockItem.getInCar()).willReturn(IN_CAR);
        given(stockItem.getInStorage()).willReturn(IN_STORAGE);
        given(stockItem.isInventoried()).willReturn(true);
        given(stockItem.isMarkedForAcquisition()).willReturn(true);

        assertThat(underTest.findByStockItemId(STOCK_ITEM_ID))
            .returns(STOCK_ITEM_ID, StockItemResponse::getStockItemId)
            .returns(STOCK_CATEGORY_ID, StockItemResponse::getStockCategoryId)
            .returns(NAME, StockItemResponse::getName)
            .returns(SERIAL_NUMBER, StockItemResponse::getSerialNumber)
            .returns(BAR_CODE, StockItemResponse::getBarCode)
            .returns(IN_CAR, StockItemResponse::getInCar)
            .returns(IN_STORAGE, StockItemResponse::getInStorage)
            .returns(true, StockItemResponse::getInventoried)
            .returns(true, StockItemResponse::getMarkedForAcquisition);
    }

    @Test
    void getStockItemsMarkedForAcquisition() {
        given(stockItemDao.getByUserIdAndMarkedForAcquisition(USER_ID)).willReturn(List.of(stockItem));

        given(stockItem.getStockItemId()).willReturn(STOCK_ITEM_ID);
        given(stockItem.getStockCategoryId()).willReturn(STOCK_CATEGORY_ID);
        given(stockItem.getName()).willReturn(NAME);
        given(stockItem.getSerialNumber()).willReturn(SERIAL_NUMBER);
        given(stockItem.getBarCode()).willReturn(BAR_CODE);
        given(stockItem.getInCar()).willReturn(IN_CAR);
        given(stockItem.getInStorage()).willReturn(IN_STORAGE);
        given(stockItem.isInventoried()).willReturn(true);
        given(stockItem.isMarkedForAcquisition()).willReturn(true);

        CustomAssertions.singleListAssertThat(underTest.getStockItemsMarkedForAcquisition(USER_ID))
            .returns(STOCK_ITEM_ID, StockItemResponse::getStockItemId)
            .returns(STOCK_CATEGORY_ID, StockItemResponse::getStockCategoryId)
            .returns(NAME, StockItemResponse::getName)
            .returns(SERIAL_NUMBER, StockItemResponse::getSerialNumber)
            .returns(BAR_CODE, StockItemResponse::getBarCode)
            .returns(IN_CAR, StockItemResponse::getInCar)
            .returns(IN_STORAGE, StockItemResponse::getInStorage)
            .returns(true, StockItemResponse::getInventoried)
            .returns(true, StockItemResponse::getMarkedForAcquisition);
    }
}