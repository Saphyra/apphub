package com.github.saphyra.apphub.service.custom.villany_atesz.stock.service.acquisition;

import com.github.saphyra.apphub.service.custom.villany_atesz.stock.dao.acquisition.AcquisitionDao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class AcquisitionDateListProviderTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final LocalDate CREATED_AT = LocalDate.now();

    @Mock
    private AcquisitionDao acquisitionDao;

    @InjectMocks
    private AcquisitionDateListProvider underTest;

    @Test
    void getDates() {
        given(acquisitionDao.getDistinctAcquiredAtByUserId(USER_ID)).willReturn(List.of(CREATED_AT));

        assertThat(underTest.getDates(USER_ID)).containsExactly(CREATED_AT.toString());
    }
}