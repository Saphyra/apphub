package com.github.saphyra.apphub.service.custom.villany_atesz.stock.service.item;

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
class MoveStockServiceTest {
    private static final UUID STOCK_ITEM_ID = UUID.randomUUID();
    private static final Integer AMOUNT = 24;
    private static final Integer IN_CAR = 35;
    private static final Integer IN_STORAGE = 86;

    @Mock
    private StockItemDao stockItemDao;

    @InjectMocks
    private MoveStockService underTest;

    @Mock
    private StockItem stockItem;

    @Test
    void moveToCar_zeroAmount() {
        ExceptionValidator.validateInvalidParam(() -> underTest.moveToCar(STOCK_ITEM_ID, 0), "amount", "must not be zero");
    }

    @Test
    void moveToCar() {
        given(stockItemDao.findByIdValidated(STOCK_ITEM_ID)).willReturn(stockItem);
        given(stockItem.getInCar()).willReturn(IN_CAR);
        given(stockItem.getInStorage()).willReturn(IN_STORAGE);

        underTest.moveToCar(STOCK_ITEM_ID, AMOUNT);

        then(stockItem).should().setInCar(IN_CAR + AMOUNT);
        then(stockItem).should().setInStorage(IN_STORAGE - AMOUNT);
        then(stockItemDao).should().save(stockItem);
    }

    @Test
    void moveToStorage() {
        given(stockItemDao.findByIdValidated(STOCK_ITEM_ID)).willReturn(stockItem);
        given(stockItem.getInCar()).willReturn(IN_CAR);
        given(stockItem.getInStorage()).willReturn(IN_STORAGE);

        underTest.moveToStorage(STOCK_ITEM_ID, AMOUNT);

        then(stockItem).should().setInCar(IN_CAR - AMOUNT);
        then(stockItem).should().setInStorage(IN_STORAGE + AMOUNT);
        then(stockItemDao).should().save(stockItem);
    }
}