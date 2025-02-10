package com.github.saphyra.apphub.service.custom.elite_base.dao.station.station_service;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.service.custom.elite_base.dao.station.station_service.StationService;
import com.github.saphyra.apphub.service.custom.elite_base.dao.station.station_service.StationServiceConverter;
import com.github.saphyra.apphub.service.custom.elite_base.dao.station.station_service.StationServiceEntity;
import com.github.saphyra.apphub.service.custom.elite_base.dao.station.station_service.StationServiceEnum;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class StationServiceConverterTest {
    private static final UUID STATION_ID = UUID.randomUUID();
    private static final String STATION_ID_STRING = "station-id";

    @Mock
    private UuidConverter uuidConverter;

    @InjectMocks
    private StationServiceConverter underTest;

    @Test
    void convertDomain() {
        StationService in = StationService.builder()
            .stationId(STATION_ID)
            .service(StationServiceEnum.BARTENDER)
            .build();

        given(uuidConverter.convertDomain(STATION_ID)).willReturn(STATION_ID_STRING);

        assertThat(underTest.convertDomain(in))
            .returns(STATION_ID_STRING, StationServiceEntity::getStationId)
            .returns(StationServiceEnum.BARTENDER, StationServiceEntity::getService);
    }

    @Test
    void convertEntity() {
        StationServiceEntity in = StationServiceEntity.builder()
            .stationId(STATION_ID_STRING)
            .service(StationServiceEnum.BARTENDER)
            .build();

        given(uuidConverter.convertEntity(STATION_ID_STRING)).willReturn(STATION_ID);

        assertThat(underTest.convertEntity(in))
            .returns(STATION_ID, StationService::getStationId)
            .returns(StationServiceEnum.BARTENDER, StationService::getService);
    }
}