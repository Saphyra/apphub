package com.github.saphyra.apphub.service.custom.villany_atesz.stock.service.item;

import com.github.saphyra.apphub.api.custom.villany_atesz.model.AcquisitionRequest;
import com.github.saphyra.apphub.api.custom.villany_atesz.model.AddToStockRequest;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class AddToStockRequestValidatorTest {
    private static final Integer IN_CAR = 342;
    private static final Integer IN_STORAGE = 6;
    private static final Integer PRICE = 648;
    private static final UUID STOCK_ITEM_ID = UUID.randomUUID();
    private static final String BAR_CODE = "bar-code";
    private static final LocalDate ACQUIRED_AT = LocalDate.now();

    private final AddToStockRequestValidator underTest = new AddToStockRequestValidator();

    @Test
    void nullItems() {
        AcquisitionRequest acquisitionRequest = AcquisitionRequest.builder()
            .items(null)
            .acquiredAt(ACQUIRED_AT)
            .build();

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(acquisitionRequest), "items", "must not be null");
    }

    @Test
    void nullAcquiredAt() {
        AddToStockRequest item = AddToStockRequest.builder()
            .stockItemId(STOCK_ITEM_ID)
            .inCar(IN_CAR)
            .inStorage(IN_STORAGE)
            .price(PRICE)
            .barCode(BAR_CODE)
            .forceUpdatePrice(true)
            .build();

        AcquisitionRequest acquisitionRequest = AcquisitionRequest.builder()
            .items(List.of(item))
            .acquiredAt(null)
            .build();

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(acquisitionRequest), "acquiredAt", "must not be null");
    }

    @Test
    void nullStockItemId() {
        AddToStockRequest item = AddToStockRequest.builder()
            .stockItemId(null)
            .inCar(IN_CAR)
            .inStorage(IN_STORAGE)
            .price(PRICE)
            .barCode(BAR_CODE)
            .forceUpdatePrice(true)
            .build();

        AcquisitionRequest acquisitionRequest = AcquisitionRequest.builder()
            .items(List.of(item))
            .acquiredAt(ACQUIRED_AT)
            .build();

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(acquisitionRequest), "stockItemId", "must not be null");
    }

    @Test
    void nullInCar() {
        AddToStockRequest item = AddToStockRequest.builder()
            .stockItemId(STOCK_ITEM_ID)
            .inCar(null)
            .inStorage(IN_STORAGE)
            .price(PRICE)
            .barCode(BAR_CODE)
            .forceUpdatePrice(true)
            .build();

        AcquisitionRequest acquisitionRequest = AcquisitionRequest.builder()
            .items(List.of(item))
            .acquiredAt(ACQUIRED_AT)
            .build();

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(acquisitionRequest), "inCar", "must not be null");
    }

    @Test
    void nullInStorage() {
        AddToStockRequest item = AddToStockRequest.builder()
            .stockItemId(STOCK_ITEM_ID)
            .inCar(IN_CAR)
            .inStorage(null)
            .price(PRICE)
            .barCode(BAR_CODE)
            .forceUpdatePrice(true)
            .build();

        AcquisitionRequest acquisitionRequest = AcquisitionRequest.builder()
            .items(List.of(item))
            .acquiredAt(ACQUIRED_AT)
            .build();

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(acquisitionRequest), "inStorage", "must not be null");
    }

    @Test
    void nullPrice() {
        AddToStockRequest item = AddToStockRequest.builder()
            .stockItemId(STOCK_ITEM_ID)
            .inCar(IN_CAR)
            .inStorage(IN_STORAGE)
            .price(null)
            .barCode(BAR_CODE)
            .forceUpdatePrice(true)
            .build();

        AcquisitionRequest acquisitionRequest = AcquisitionRequest.builder()
            .items(List.of(item))
            .acquiredAt(ACQUIRED_AT)
            .build();

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(acquisitionRequest), "price", "must not be null");
    }

    @Test
    void nullBarCode() {
        AddToStockRequest item = AddToStockRequest.builder()
            .stockItemId(STOCK_ITEM_ID)
            .inCar(IN_CAR)
            .inStorage(IN_STORAGE)
            .price(PRICE)
            .barCode(null)
            .forceUpdatePrice(true)
            .build();

        AcquisitionRequest acquisitionRequest = AcquisitionRequest.builder()
            .items(List.of(item))
            .acquiredAt(ACQUIRED_AT)
            .build();

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(acquisitionRequest), "barCode", "must not be null");
    }

    @Test
    void nullForceUpdatePrice() {
        AddToStockRequest item = AddToStockRequest.builder()
            .stockItemId(STOCK_ITEM_ID)
            .inCar(IN_CAR)
            .inStorage(IN_STORAGE)
            .price(PRICE)
            .barCode(BAR_CODE)
            .forceUpdatePrice(null)
            .build();

        AcquisitionRequest acquisitionRequest = AcquisitionRequest.builder()
            .items(List.of(item))
            .acquiredAt(ACQUIRED_AT)
            .build();

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(acquisitionRequest), "forceUpdatePrice", "must not be null");
    }

    @Test
    void valid() {
        AddToStockRequest item = AddToStockRequest.builder()
            .stockItemId(STOCK_ITEM_ID)
            .inCar(IN_CAR)
            .inStorage(IN_STORAGE)
            .price(PRICE)
            .barCode(BAR_CODE)
            .forceUpdatePrice(true)
            .build();

        AcquisitionRequest acquisitionRequest = AcquisitionRequest.builder()
            .items(List.of(item))
            .acquiredAt(ACQUIRED_AT)
            .build();

        underTest.validate(acquisitionRequest);
    }
}