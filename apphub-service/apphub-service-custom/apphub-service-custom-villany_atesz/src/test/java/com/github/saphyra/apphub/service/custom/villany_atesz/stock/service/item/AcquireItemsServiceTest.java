package com.github.saphyra.apphub.service.custom.villany_atesz.stock.service.item;

import com.github.saphyra.apphub.api.custom.villany_atesz.model.AcquisitionRequest;
import com.github.saphyra.apphub.api.custom.villany_atesz.model.AddToStockRequest;
import com.github.saphyra.apphub.service.custom.villany_atesz.stock.dao.item.StockItem;
import com.github.saphyra.apphub.service.custom.villany_atesz.stock.dao.item.StockItemDao;
import com.github.saphyra.apphub.service.custom.villany_atesz.stock.dao.price.StockItemPrice;
import com.github.saphyra.apphub.service.custom.villany_atesz.stock.dao.price.StockItemPriceDao;
import com.github.saphyra.apphub.service.custom.villany_atesz.stock.service.acquisition.AcquisitionService;
import com.github.saphyra.apphub.service.custom.villany_atesz.stock.service.price.StockItemPriceFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class AcquireItemsServiceTest {
    private static final UUID STOCK_ITEM_ID = UUID.randomUUID();
    private static final Integer ORIGINAL_IN_CAR = 564;
    private static final Integer REQUEST_IN_CAR = 75;
    private static final Integer ORIGINAL_IN_STORAGE = 6;
    private static final Integer REQUEST_IN_STORAGE = 64;
    private static final UUID USER_ID = UUID.randomUUID();
    private static final Integer PRICE = 678;
    private static final String BAR_CODE = "bar-code";
    private static final LocalDate ACQUIRED_AT = LocalDate.now();

    @Mock
    private AddToStockRequestValidator addToStockRequestValidator;

    @Mock
    private StockItemDao stockItemDao;

    @Mock
    private StockItemPriceDao stockItemPriceDao;

    @Mock
    private StockItemPriceFactory stockItemPriceFactory;

    @Mock
    private AcquisitionService acquisitionService;

    @InjectMocks
    private AcquireItemsService underTest;

    @Mock
    private StockItem stockItem;

    @Mock
    private StockItemPrice stockItemPrice;

    @Test
    void acquire() {
        AddToStockRequest item = AddToStockRequest.builder()
            .stockItemId(STOCK_ITEM_ID)
            .inCar(REQUEST_IN_CAR)
            .inStorage(REQUEST_IN_STORAGE)
            .price(PRICE)
            .barCode(BAR_CODE)
            .forceUpdatePrice(false)
            .build();

        given(stockItemDao.findByIdValidated(STOCK_ITEM_ID)).willReturn(stockItem);
        given(stockItem.getInCar()).willReturn(ORIGINAL_IN_CAR);
        given(stockItem.getInStorage()).willReturn(ORIGINAL_IN_STORAGE);
        given(stockItem.getUserId()).willReturn(USER_ID);

        given(stockItemPriceFactory.create(USER_ID, STOCK_ITEM_ID, PRICE)).willReturn(stockItemPrice);

        AcquisitionRequest request = AcquisitionRequest.builder().items(List.of(item)).acquiredAt(ACQUIRED_AT).build();

        underTest.acquire(USER_ID, request);

        then(addToStockRequestValidator).should().validate(request);
        then(stockItem).should().setInCar(ORIGINAL_IN_CAR + REQUEST_IN_CAR);
        then(stockItem).should().setInStorage(ORIGINAL_IN_STORAGE + REQUEST_IN_STORAGE);
        then(stockItem).should().setBarCode(BAR_CODE);
        then(stockItemDao).should().save(stockItem);
        then(acquisitionService).should().createAcquisitions(USER_ID, request);
        then(stockItem).should().setMarkedForAcquisition(false);
        then(stockItemPriceDao).should().save(stockItemPrice);
        then(stockItemPriceDao).shouldHaveNoMoreInteractions();
    }

    @Test
    void forceUpdatePrice() {
        AddToStockRequest item = AddToStockRequest.builder()
            .stockItemId(STOCK_ITEM_ID)
            .inCar(REQUEST_IN_CAR)
            .inStorage(REQUEST_IN_STORAGE)
            .price(PRICE)
            .barCode(BAR_CODE)
            .forceUpdatePrice(true)
            .build();

        given(stockItemDao.findByIdValidated(STOCK_ITEM_ID)).willReturn(stockItem);
        given(stockItem.getInCar()).willReturn(ORIGINAL_IN_CAR);
        given(stockItem.getInStorage()).willReturn(ORIGINAL_IN_STORAGE);
        given(stockItem.getUserId()).willReturn(USER_ID);

        given(stockItemPriceFactory.create(USER_ID, STOCK_ITEM_ID, PRICE)).willReturn(stockItemPrice);
        AcquisitionRequest request = AcquisitionRequest.builder().items(List.of(item)).acquiredAt(ACQUIRED_AT).build();

        underTest.acquire(USER_ID, request);

        then(addToStockRequestValidator).should().validate(request);
        then(stockItem).should().setInCar(ORIGINAL_IN_CAR + REQUEST_IN_CAR);
        then(stockItem).should().setInStorage(ORIGINAL_IN_STORAGE + REQUEST_IN_STORAGE);
        then(stockItem).should().setBarCode(BAR_CODE);
        then(stockItemDao).should().save(stockItem);
        then(acquisitionService).should().createAcquisitions(USER_ID, request);
        then(stockItemPriceDao).should().deleteByStockItemId(STOCK_ITEM_ID);
        then(stockItemPriceDao).should().save(stockItemPrice);
        then(stockItem).should().setMarkedForAcquisition(false);
    }
}