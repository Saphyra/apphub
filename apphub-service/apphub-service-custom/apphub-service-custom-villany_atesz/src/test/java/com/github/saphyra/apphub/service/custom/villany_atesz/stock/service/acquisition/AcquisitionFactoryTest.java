package com.github.saphyra.apphub.service.custom.villany_atesz.stock.service.acquisition;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.custom.villany_atesz.stock.dao.acquisition.Acquisition;
import com.github.saphyra.apphub.test.common.CustomAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class AcquisitionFactoryTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final LocalDate ACQUIRED_AT = LocalDate.now();
    private static final UUID STOCK_ITEM_ID = UUID.randomUUID();
    private static final Integer AMOUNT = 43;
    private static final UUID ACQUISITION_ID = UUID.randomUUID();

    @Mock
    private IdGenerator idGenerator;

    @InjectMocks
    private AcquisitionFactory underTest;

    @Test
    void zeroValue() {
        assertThat(underTest.create(USER_ID, ACQUIRED_AT, Map.of(STOCK_ITEM_ID, 0))).isEmpty();
    }

    @Test
    void create() {
        given(idGenerator.randomUuid()).willReturn(ACQUISITION_ID);

        CustomAssertions.singleListAssertThat(underTest.create(USER_ID, ACQUIRED_AT, Map.of(STOCK_ITEM_ID, AMOUNT)))
            .returns(ACQUISITION_ID, Acquisition::getAcquisitionId)
            .returns(USER_ID, Acquisition::getUserId)
            .returns(ACQUIRED_AT, Acquisition::getAcquiredAt)
            .returns(STOCK_ITEM_ID, Acquisition::getStockItemId)
            .returns(AMOUNT, Acquisition::getAmount);
    }
}