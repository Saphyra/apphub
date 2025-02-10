package com.github.saphyra.apphub.service.custom.elite_base.dao.station.station_service;

import com.github.saphyra.apphub.service.custom.elite_base.dao.station.station_service.StationService;
import com.github.saphyra.apphub.service.custom.elite_base.dao.station.station_service.StationServiceDao;
import com.github.saphyra.apphub.service.custom.elite_base.dao.station.station_service.StationServiceEnum;
import com.github.saphyra.apphub.service.custom.elite_base.dao.station.station_service.StationServiceFactory;
import com.github.saphyra.apphub.service.custom.elite_base.dao.station.station_service.StationServiceSyncService;
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
class StationServiceSyncServiceTest {
    private static final UUID STATION_ID = UUID.randomUUID();

    @Mock
    private StationServiceDao stationServiceDao;

    @Mock
    private StationServiceFactory stationServiceFactory;

    @InjectMocks
    private StationServiceSyncService underTest;

    @Mock
    private StationService newService;

    @Mock
    private StationService existingService;

    @Mock
    private StationService deprecatedService;

    @Test
    void sync_null() {
        underTest.sync(STATION_ID, null);

        then(stationServiceDao).shouldHaveNoInteractions();
    }

    @Test
    void sync() {
        given(stationServiceDao.getByStationId(STATION_ID)).willReturn(List.of(existingService, deprecatedService));

        given(stationServiceFactory.create(STATION_ID, StationServiceEnum.BARTENDER)).willReturn(existingService);
        given(stationServiceFactory.create(STATION_ID, StationServiceEnum.CREW_LOUNGE)).willReturn(newService);

        underTest.sync(STATION_ID, List.of(StationServiceEnum.CREW_LOUNGE, StationServiceEnum.BARTENDER));

        then(stationServiceDao).should().deleteAll(List.of(deprecatedService));
        then(stationServiceDao).should().saveAll(List.of(newService));
    }
}