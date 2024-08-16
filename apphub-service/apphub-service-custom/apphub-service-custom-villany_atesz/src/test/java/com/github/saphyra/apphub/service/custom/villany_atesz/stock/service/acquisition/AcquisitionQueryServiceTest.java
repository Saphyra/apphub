package com.github.saphyra.apphub.service.custom.villany_atesz.stock.service.acquisition;

import com.github.saphyra.apphub.api.custom.villany_atesz.model.AcquisitionResponse;
import com.github.saphyra.apphub.service.custom.villany_atesz.stock.dao.acquisition.Acquisition;
import com.github.saphyra.apphub.service.custom.villany_atesz.stock.dao.acquisition.AcquisitionDao;
import com.github.saphyra.apphub.service.custom.villany_atesz.stock.dao.item.StockItem;
import com.github.saphyra.apphub.service.custom.villany_atesz.stock.dao.item.StockItemDao;
import com.github.saphyra.apphub.test.common.CustomAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class AcquisitionQueryServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final LocalDate ACQUIRED_AT = LocalDate.now();
    private static final UUID ACQUISITION_ID = UUID.randomUUID();
    private static final UUID STOCK_ITEM_ID = UUID.randomUUID();
    private static final int AMOUNT = 4;
    private static final String STOCK_ITEM_NAME = "stock-item-name";

    @Mock
    private AcquisitionDao acquisitionDao;

    @Mock
    private StockItemDao stockItemDao;

    @InjectMocks
    private AcquisitionQueryService underTest;

    @Mock
    private StockItem stockItem;

    @Test
    void getAcquisitionsOnDay() {
        Acquisition acquisition = Acquisition.builder()
            .acquisitionId(ACQUISITION_ID)
            .stockItemId(STOCK_ITEM_ID)
            .amount(AMOUNT)
            .build();
        given(acquisitionDao.getByAcquiredAtAndUserId(ACQUIRED_AT, USER_ID)).willReturn(List.of(acquisition));
        given(stockItemDao.findByIdValidated(STOCK_ITEM_ID)).willReturn(stockItem);
        given(stockItem.getName()).willReturn(STOCK_ITEM_NAME);

        CustomAssertions.singleListAssertThat(underTest.getAcquisitionsOnDay(USER_ID, ACQUIRED_AT))
            .returns(ACQUISITION_ID, AcquisitionResponse::getAcquisitionId)
            .returns(STOCK_ITEM_ID, AcquisitionResponse::getStockItemId)
            .returns(STOCK_ITEM_NAME, AcquisitionResponse::getStockItemName)
            .returns(AMOUNT, AcquisitionResponse::getAmount);
    }
}