package com.github.saphyra.apphub.service.custom.villany_atesz.stock.service.acquisition;

import com.github.saphyra.apphub.api.custom.villany_atesz.model.AcquisitionRequest;
import com.github.saphyra.apphub.api.custom.villany_atesz.model.AddToStockRequest;
import com.github.saphyra.apphub.service.custom.villany_atesz.stock.dao.acquisition.Acquisition;
import com.github.saphyra.apphub.service.custom.villany_atesz.stock.dao.acquisition.AcquisitionDao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class AcquisitionServiceTest {
    private static final LocalDate ACQUIRED_AT = LocalDate.now();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final Integer IN_CAR = 42;
    private static final Integer IN_STORAGE = 234;
    private static final UUID STOCK_ITEM_ID = UUID.randomUUID();

    @Mock
    private AcquisitionDao acquisitionDao;

    @Mock
    private AcquisitionFactory acquisitionFactory;

    @InjectMocks
    private AcquisitionService underTest;

    @Mock
    private AddToStockRequest addToStockRequest;

    @Mock
    private Acquisition acquisition;

    @Test
    void createAcquisitions() {
        AcquisitionRequest request = AcquisitionRequest.builder()
            .items(List.of(addToStockRequest))
            .acquiredAt(ACQUIRED_AT)
            .build();

        given(addToStockRequest.getInCar()).willReturn(IN_CAR);
        given(addToStockRequest.getInStorage()).willReturn(IN_STORAGE);
        given(addToStockRequest.getStockItemId()).willReturn(STOCK_ITEM_ID);
        given(acquisitionFactory.create(USER_ID, ACQUIRED_AT, Map.of(STOCK_ITEM_ID, IN_CAR + IN_STORAGE))).willReturn(List.of(acquisition));

        underTest.createAcquisitions(USER_ID, request);

        then(acquisitionDao).should().saveAll(List.of(acquisition));
    }
}