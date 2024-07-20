package com.github.saphyra.apphub.service.custom.villany_atesz.stock.service.item;

import com.github.saphyra.apphub.api.custom.villany_atesz.model.AddToStockRequest;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class AddToStockRequestValidatorTest {
    private static final Integer IN_CAR = 342;
    private static final Integer IN_STORAGE = 6;
    private static final Integer PRICE = 648;
    private static final UUID STOCK_ITEM_ID = UUID.randomUUID();
    private static final String BAR_CODE = "bar-code";

    private final AddToStockRequestValidator underTest = new AddToStockRequestValidator();

    @Test
    void nullStockItemId() {
        AddToStockRequest request = AddToStockRequest.builder()
            .stockItemId(null)
            .inCar(IN_CAR)
            .inStorage(IN_STORAGE)
            .price(PRICE)
            .barCode(BAR_CODE)
            .forceUpdatePrice(true)
            .build();

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(List.of(request)), "stockItemId", "must not be null");
    }

    @Test
    void nullInCar() {
        AddToStockRequest request = AddToStockRequest.builder()
            .stockItemId(STOCK_ITEM_ID)
            .inCar(null)
            .inStorage(IN_STORAGE)
            .price(PRICE)
            .barCode(BAR_CODE)
            .forceUpdatePrice(true)
            .build();

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(List.of(request)), "inCar", "must not be null");
    }

    @Test
    void nullInStorage() {
        AddToStockRequest request = AddToStockRequest.builder()
            .stockItemId(STOCK_ITEM_ID)
            .inCar(IN_CAR)
            .inStorage(null)
            .price(PRICE)
            .barCode(BAR_CODE)
            .forceUpdatePrice(true)
            .build();

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(List.of(request)), "inStorage", "must not be null");
    }

    @Test
    void nullPrice() {
        AddToStockRequest request = AddToStockRequest.builder()
            .stockItemId(STOCK_ITEM_ID)
            .inCar(IN_CAR)
            .inStorage(IN_STORAGE)
            .price(null)
            .barCode(BAR_CODE)
            .forceUpdatePrice(true)
            .build();

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(List.of(request)), "price", "must not be null");
    }

    @Test
    void nullBarCode() {
        AddToStockRequest request = AddToStockRequest.builder()
            .stockItemId(STOCK_ITEM_ID)
            .inCar(IN_CAR)
            .inStorage(IN_STORAGE)
            .price(PRICE)
            .barCode(null)
            .forceUpdatePrice(true)
            .build();

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(List.of(request)), "barCode", "must not be null");
    }

    @Test
    void nullForceUpdatePrice() {
        AddToStockRequest request = AddToStockRequest.builder()
            .stockItemId(STOCK_ITEM_ID)
            .inCar(IN_CAR)
            .inStorage(IN_STORAGE)
            .price(PRICE)
            .barCode(BAR_CODE)
            .forceUpdatePrice(null)
            .build();

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(List.of(request)), "forceUpdatePrice", "must not be null");
    }

    @Test
    void valid() {
        AddToStockRequest request = AddToStockRequest.builder()
            .stockItemId(STOCK_ITEM_ID)
            .inCar(IN_CAR)
            .inStorage(IN_STORAGE)
            .price(PRICE)
            .barCode(BAR_CODE)
            .forceUpdatePrice(true)
            .build();

        underTest.validate(List.of(request));
    }
}