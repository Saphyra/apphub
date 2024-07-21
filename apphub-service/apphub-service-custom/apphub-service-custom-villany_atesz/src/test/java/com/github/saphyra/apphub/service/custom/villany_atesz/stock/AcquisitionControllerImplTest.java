package com.github.saphyra.apphub.service.custom.villany_atesz.stock;

import com.github.saphyra.apphub.api.custom.villany_atesz.model.AcquisitionResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.service.custom.villany_atesz.stock.service.acquisition.AcquisitionDateListProvider;
import com.github.saphyra.apphub.service.custom.villany_atesz.stock.service.acquisition.AcquisitionQueryService;
import org.junit.jupiter.api.BeforeEach;
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
class AcquisitionControllerImplTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final LocalDate ACQUIRED_AT = LocalDate.now();

    @Mock
    private AcquisitionDateListProvider acquisitionDateListProvider;

    @Mock
    private AcquisitionQueryService acquisitionQueryService;

    @InjectMocks
    private AcquisitionControllerImpl underTest;

    @Mock
    private AccessTokenHeader accessTokenHeader;

    @Mock
    private AcquisitionResponse acquisitionResponse;

    @BeforeEach
    void setUp() {
        given(accessTokenHeader.getUserId()).willReturn(USER_ID);
    }

    @Test
    void getDates() {
        given(acquisitionDateListProvider.getDates(USER_ID)).willReturn(List.of(ACQUIRED_AT.toString()));

        assertThat(underTest.getDates(accessTokenHeader)).containsExactly(ACQUIRED_AT.toString());
    }

    @Test
    void getAcquisitionsOnDay() {
        given(acquisitionQueryService.getAcquisitionsOnDay(USER_ID, ACQUIRED_AT)).willReturn(List.of(acquisitionResponse));

        assertThat(underTest.getAcquisitionsOnDay(ACQUIRED_AT, accessTokenHeader)).containsExactly(acquisitionResponse);
    }
}