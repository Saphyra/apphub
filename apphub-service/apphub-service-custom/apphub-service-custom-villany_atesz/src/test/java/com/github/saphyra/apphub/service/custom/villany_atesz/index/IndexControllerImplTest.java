package com.github.saphyra.apphub.service.custom.villany_atesz.index;

import com.github.saphyra.apphub.api.custom.villany_atesz.model.StockItemResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.service.custom.villany_atesz.index.service.StockTotalValueQueryService;
import com.github.saphyra.apphub.service.custom.villany_atesz.stock.service.item.StockItemQueryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class IndexControllerImplTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final int TOTAL_VALUE = 234;

    @Mock
    private StockTotalValueQueryService stockTotalValueQueryService;

    @Mock
    private StockItemQueryService stockItemQueryService;

    @InjectMocks
    private IndexControllerImpl underTest;

    @Mock
    private AccessTokenHeader accessTokenHeader;

    @Mock
    private StockItemResponse stockItemResponse;

    @BeforeEach
    void setUp() {
        given(accessTokenHeader.getUserId()).willReturn(USER_ID);
    }

    @Test
    void setTotalValue() {
        given(stockTotalValueQueryService.getTotalValue(USER_ID)).willReturn(TOTAL_VALUE);

        assertThat(underTest.getTotalValue(accessTokenHeader).getValue()).isEqualTo(TOTAL_VALUE);
    }

    @Test
    void getStockItemsMarkedForAcquisition() {
        given(stockItemQueryService.getStockItemsMarkedForAcquisition(USER_ID)).willReturn(List.of(stockItemResponse));

        assertThat(underTest.getStockItemsMarkedForAcquisition(accessTokenHeader)).containsExactly(stockItemResponse);
    }
}