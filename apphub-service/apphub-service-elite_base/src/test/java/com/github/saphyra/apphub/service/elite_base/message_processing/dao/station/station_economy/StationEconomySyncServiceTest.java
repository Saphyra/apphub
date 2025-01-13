package com.github.saphyra.apphub.service.elite_base.message_processing.dao.station.station_economy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class StationEconomySyncServiceTest {
    private static final UUID STATION_ID = UUID.randomUUID();

    @Mock
    private StationEconomyDao stationEconomyDao;

    @InjectMocks
    private StationEconomySyncService underTest;

    @Mock
    private StationEconomy existingEconomy;

    @Mock
    private StationEconomy newEconomy;

    @Mock
    private StationEconomy deprecatedEconomy;

    @Test
    void sync_null() {
        underTest.sync(STATION_ID, null);

        then(stationEconomyDao).shouldHaveNoInteractions();
    }

    @Test
    void sync() {
        given(stationEconomyDao.getByStationId(STATION_ID)).willReturn(List.of(existingEconomy, deprecatedEconomy));

        underTest.sync(STATION_ID, List.of(existingEconomy, newEconomy));

        then(stationEconomyDao).should().deleteAll(List.of(deprecatedEconomy));
        then(stationEconomyDao).should().saveAll(List.of(newEconomy));
    }
}