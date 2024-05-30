package com.github.saphyra.apphub.service.custom.villany_atesz.index;

import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.service.custom.villany_atesz.index.service.StockTotalValueQueryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class IndexControllerImplTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final int TOTAL_VALUE = 234;

    @Mock
    private StockTotalValueQueryService stockTotalValueQueryService;

    @InjectMocks
    private IndexControllerImpl underTest;

    @Mock
    private AccessTokenHeader accessTokenHeader;

    @BeforeEach
    void setUp() {
        given(accessTokenHeader.getUserId()).willReturn(USER_ID);
    }

    @Test
    void setTotalValue() {
        given(stockTotalValueQueryService.getTotalValue(USER_ID)).willReturn(TOTAL_VALUE);

        assertThat(underTest.getTotalValue(accessTokenHeader).getValue()).isEqualTo(TOTAL_VALUE);
    }
}