package com.github.saphyra.apphub.service.custom.elite_base.dao.station.station_service;

import com.github.saphyra.apphub.service.custom.elite_base.dao.station.station_service.StationService;
import com.github.saphyra.apphub.service.custom.elite_base.dao.station.station_service.StationServiceEnum;
import com.github.saphyra.apphub.service.custom.elite_base.dao.station.station_service.StationServiceFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class StationServiceFactoryTest {
    private static final UUID STATION_ID = UUID.randomUUID();

    @InjectMocks
    private StationServiceFactory underTest;

    @Test
    void create() {
        assertThat(underTest.create(STATION_ID, StationServiceEnum.BARTENDER))
            .returns(STATION_ID, StationService::getStationId)
            .returns(StationServiceEnum.BARTENDER, StationService::getService);
    }
}