package com.github.saphyra.apphub.service.feature.elite_base.service.powerplay.merit_farm.mining.nakato_kaine;

import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBean;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBeenTestUtils;
import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.service.feature.elite_base.dao.item.trading.commodity.Commodity;
import com.github.saphyra.apphub.service.feature.elite_base.dao.item.trading.commodity.CommodityDao;
import com.github.saphyra.apphub.service.feature.elite_base.dao.station.Station;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class OfferServiceTest {
    private static final UUID STAR_SYSTEM_ID = UUID.randomUUID();
    private static final UUID STATION_ID = UUID.randomUUID();

    @Mock
    private CommodityDao commodityDao;

    @Spy
    private final ExecutorServiceBean executorServiceBean = ExecutorServiceBeenTestUtils.create(mock(ErrorReporterService.class));

    @InjectMocks
    private OfferService offerService;

    @Mock
    private Station station;

    @Mock
    private Commodity commodity;

    @Test
    void getOffers_zeroDemand() {
        given(station.getStarSystemId()).willReturn(STAR_SYSTEM_ID);
        given(station.getId()).willReturn(STATION_ID);
        given(commodityDao.getByIds(anyList())).willReturn(List.of(commodity));
        given(commodity.getDemand()).willReturn(0);

        assertThat(offerService.getOffers(List.of(station), null)).isEmpty();

        ArgumentCaptor<List<BiWrapper<UUID, String>>> idsCaptor = ArgumentCaptor.forClass(List.class);
        then(commodityDao).should().getByIds(idsCaptor.capture());
        assertThat(idsCaptor.getValue()).containsExactly(
            new BiWrapper<>(STATION_ID, "painite"),
            new BiWrapper<>(STATION_ID, "platinum")
        );

    }

    @Test
    void getOffers() {
        Commodity zeroDemandCommodity = mock(Commodity.class);

        given(station.getStarSystemId()).willReturn(STAR_SYSTEM_ID);
        given(station.getId()).willReturn(STATION_ID);
        given(commodityDao.getByIds(anyList())).willReturn(List.of(commodity, zeroDemandCommodity));
        given(commodity.getDemand()).willReturn(5);
        given(zeroDemandCommodity.getDemand()).willReturn(0);

        assertThat(offerService.getOffers(List.of(station), null)).containsExactly(commodity);
    }
}