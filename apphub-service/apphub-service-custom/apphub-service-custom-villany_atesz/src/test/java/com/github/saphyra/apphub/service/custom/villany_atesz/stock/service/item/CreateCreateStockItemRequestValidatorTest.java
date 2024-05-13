package com.github.saphyra.apphub.service.custom.villany_atesz.stock.service.item;

import com.github.saphyra.apphub.api.custom.villany_atesz.model.CreateStockItemRequest;
import com.github.saphyra.apphub.service.custom.villany_atesz.stock.dao.category.StockCategoryDao;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
public class CreateCreateStockItemRequestValidatorTest {
    private static final String NAME = "name";
    private static final String SERIAL_NUMBER = "serial-number";
    private static final Integer IN_CAR = 35;
    private static final Integer IN_STORAGE = 654;
    private static final Integer PRICE = 465;
    private static final UUID STOCK_CATEGORY_ID = UUID.randomUUID();
    private static final String BAR_CODE = "bar-code";

    @Mock
    private StockCategoryDao stockCategoryDao;

    @InjectMocks
    private CreateStockItemRequestValidator underTest;

    @Test
    void nullStockCategoryId() {
        CreateStockItemRequest request = CreateStockItemRequest.builder()
            .stockCategoryId(null)
            .name(NAME)
            .serialNumber(SERIAL_NUMBER)
            .barCode(BAR_CODE)
            .inCar(IN_CAR)
            .inStorage(IN_STORAGE)
            .price(PRICE)
            .build();

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(request), "stockCategoryId", "must not be null");
    }

    @Test
    void blankName() {
        CreateStockItemRequest request = CreateStockItemRequest.builder()
            .stockCategoryId(STOCK_CATEGORY_ID)
            .name(" ")
            .serialNumber(SERIAL_NUMBER)
            .barCode(BAR_CODE)
            .inCar(IN_CAR)
            .inStorage(IN_STORAGE)
            .price(PRICE)
            .build();

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(request), "name", "must not be null or blank");
    }

    @Test
    void nullSerialNumber() {
        CreateStockItemRequest request = CreateStockItemRequest.builder()
            .stockCategoryId(STOCK_CATEGORY_ID)
            .name(NAME)
            .serialNumber(null)
            .barCode(BAR_CODE)
            .inCar(IN_CAR)
            .inStorage(IN_STORAGE)
            .price(PRICE)
            .build();

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(request), "serialNumber", "must not be null");
    }

    @Test
    void nullBarCode() {
        CreateStockItemRequest request = CreateStockItemRequest.builder()
            .stockCategoryId(STOCK_CATEGORY_ID)
            .name(NAME)
            .serialNumber(SERIAL_NUMBER)
            .barCode(null)
            .inCar(IN_CAR)
            .inStorage(IN_STORAGE)
            .price(PRICE)
            .build();

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(request), "barCode", "must not be null");
    }

    @Test
    void nullInCar() {
        CreateStockItemRequest request = CreateStockItemRequest.builder()
            .stockCategoryId(STOCK_CATEGORY_ID)
            .name(NAME)
            .serialNumber(SERIAL_NUMBER)
            .barCode(BAR_CODE)
            .inCar(null)
            .inStorage(IN_STORAGE)
            .price(PRICE)
            .build();

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(request), "inCar", "must not be null");
    }

    @Test
    void nullInStorage() {
        CreateStockItemRequest request = CreateStockItemRequest.builder()
            .stockCategoryId(STOCK_CATEGORY_ID)
            .name(NAME)
            .serialNumber(SERIAL_NUMBER)
            .barCode(BAR_CODE)
            .inCar(IN_CAR)
            .inStorage(null)
            .price(PRICE)
            .build();

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(request), "inStorage", "must not be null");
    }

    @Test
    void nullPrice() {
        CreateStockItemRequest request = CreateStockItemRequest.builder()
            .stockCategoryId(STOCK_CATEGORY_ID)
            .name(NAME)
            .serialNumber(SERIAL_NUMBER)
            .barCode(BAR_CODE)
            .inCar(IN_CAR)
            .inStorage(IN_STORAGE)
            .price(null)
            .build();

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(request), "price", "must not be null");
    }

    @Test
    void valid() {
        CreateStockItemRequest request = CreateStockItemRequest.builder()
            .stockCategoryId(STOCK_CATEGORY_ID)
            .name(NAME)
            .serialNumber(SERIAL_NUMBER)
            .barCode(BAR_CODE)
            .inCar(IN_CAR)
            .inStorage(IN_STORAGE)
            .price(PRICE)
            .build();

        underTest.validate(request);

        then(stockCategoryDao).should().findByIdValidated(STOCK_CATEGORY_ID);
    }
}