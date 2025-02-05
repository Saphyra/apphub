package com.github.saphyra.apphub.service.elite_base.message_processing.dao.station.station_service;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
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
class StationServiceDaoTest {
    private static final UUID STATION_ID = UUID.randomUUID();
    private static final String STATION_ID_STRING = "station-id";

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private StationServiceConverter converter;

    @Mock
    private StationServiceRepository repository;

    @InjectMocks
    private StationServiceDao underTest;

    @Mock
    private StationService domain;

    @Mock
    private StationServiceEntity entity;

    @Test
    void getByStationId() {
        given(uuidConverter.convertDomain(STATION_ID)).willReturn(STATION_ID_STRING);
        given(repository.getByStationId(STATION_ID_STRING)).willReturn(List.of(entity));
        given(converter.convertEntity(List.of(entity))).willReturn(List.of(domain));

        assertThat(underTest.getByStationId(STATION_ID)).containsExactly(domain);
    }
}