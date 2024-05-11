package com.github.saphyra.apphub.service.custom.villany_atesz.stock.service.inventory;

import com.github.saphyra.apphub.service.custom.villany_atesz.stock.dao.category.StockCategoryDao;
import com.github.saphyra.apphub.service.custom.villany_atesz.stock.dao.item.StockItem;
import com.github.saphyra.apphub.service.custom.villany_atesz.stock.dao.item.StockItemDao;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class EditStockItemServiceTest {
    private static final UUID STOCK_ITEM_ID = UUID.randomUUID();
    private static final UUID STOCK_CATEGORY_ID = UUID.randomUUID();
    private static final String NAME = "name";
    private static final String SERIAL_NUMBER = "serial-number";
    private static final Integer IN_CAR = 43;
    private static final Integer IN_STORAGE = 64;

    @Mock
    private StockItemDao stockItemDao;

    @Mock
    private StockCategoryDao stockCategoryDao;

    @InjectMocks
    private EditStockItemService underTest;

    @Mock
    private StockItem stockItem;

    @Test
    void editCategory_nullStockCategoryId() {
        ExceptionValidator.validateInvalidParam(() -> underTest.editCategory(STOCK_ITEM_ID, null), "stockCategoryId", "must not be null");
    }

    @Test
    void editCategory() {
        given(stockItemDao.findByIdValidated(STOCK_ITEM_ID)).willReturn(stockItem);

        underTest.editCategory(STOCK_ITEM_ID, STOCK_CATEGORY_ID);

        then(stockCategoryDao).should().findByIdValidated(STOCK_CATEGORY_ID);
        then(stockItem).should().setStockCategoryId(STOCK_CATEGORY_ID);
        then(stockItemDao).should().save(stockItem);
    }

    @Test
    void editName_blankName() {
        ExceptionValidator.validateInvalidParam(() -> underTest.editName(STOCK_ITEM_ID, " "), "name", "must not be null or blank");
    }

    @Test
    void editName() {
        given(stockItemDao.findByIdValidated(STOCK_ITEM_ID)).willReturn(stockItem);

        underTest.editName(STOCK_ITEM_ID, NAME);

        then(stockItem).should().setName(NAME);
        then(stockItemDao).should().save(stockItem);
    }

    @Test
    void editSerialNumber_nullSerialNumber() {
        ExceptionValidator.validateInvalidParam(() -> underTest.editSerialNumber(STOCK_ITEM_ID, null), "serialNumber", "must not be null");
    }

    @Test
    void editSerialNumber() {
        given(stockItemDao.findByIdValidated(STOCK_ITEM_ID)).willReturn(stockItem);

        underTest.editSerialNumber(STOCK_ITEM_ID, SERIAL_NUMBER);

        then(stockItem).should().setSerialNumber(SERIAL_NUMBER);
        then(stockItemDao).should().save(stockItem);
    }

    @Test
    void editInCar_nullInCar() {
        ExceptionValidator.validateInvalidParam(() -> underTest.editInCar(STOCK_ITEM_ID, null), "inCar", "must not be null");
    }

    @Test
    void editInCar() {
        given(stockItemDao.findByIdValidated(STOCK_ITEM_ID)).willReturn(stockItem);

        underTest.editInCar(STOCK_ITEM_ID, IN_CAR);

        then(stockItem).should().setInCar(IN_CAR);
        then(stockItemDao).should().save(stockItem);
    }

    @Test
    void editInStorage_nullInStorage() {
        ExceptionValidator.validateInvalidParam(() -> underTest.editInStorage(STOCK_ITEM_ID, null), "inStorage", "must not be null");
    }

    @Test
    void editInStorage() {
        given(stockItemDao.findByIdValidated(STOCK_ITEM_ID)).willReturn(stockItem);

        underTest.editInStorage(STOCK_ITEM_ID, IN_STORAGE);

        then(stockItem).should().setInStorage(IN_STORAGE);
        then(stockItemDao).should().save(stockItem);
    }

    @Test
    void editInventoried_nullInventoried() {
        ExceptionValidator.validateInvalidParam(() -> underTest.editInventoried(STOCK_ITEM_ID, null), "inventoried", "must not be null");
    }

    @Test
    void editInventoried() {
        given(stockItemDao.findByIdValidated(STOCK_ITEM_ID)).willReturn(stockItem);

        underTest.editInventoried(STOCK_ITEM_ID, true);

        then(stockItem).should().setInventoried(true);
        then(stockItemDao).should().save(stockItem);
    }
}