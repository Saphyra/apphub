package com.github.saphyra.apphub.service.custom.elite_base.message_processing.dao.station;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class StationDaoTest {
    private static final UUID STAR_SYSTEM_ID = UUID.randomUUID();
    private static final String STATION_NAME = "station-name";
    private static final String STAR_SYSTEM_ID_STRING = "star-system-id";
    private static final Long MARKET_ID = 2343L;

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private StationConverter converter;

    @Mock
    private StationRepository repository;

    @InjectMocks
    private StationDao underTest;

    @Mock
    private Station domain;

    @Mock
    private StationEntity entity;

    @Test
    void findByStarSystemIdAndStationName() {
        given(uuidConverter.convertDomain(STAR_SYSTEM_ID)).willReturn(STAR_SYSTEM_ID_STRING);
        given(repository.findByStarSystemIdAndStationName(STAR_SYSTEM_ID_STRING, STATION_NAME)).willReturn(Optional.of(entity));
        given(converter.convertEntity(Optional.of(entity))).willReturn(Optional.of(domain));

        assertThat(underTest.findByStarSystemIdAndStationName(STAR_SYSTEM_ID, STATION_NAME)).contains(domain);
    }

    @Test
    void findByMarketId() {
        given(repository.findByMarketId(MARKET_ID)).willReturn(Optional.of(entity));
        given(converter.convertEntity(Optional.of(entity))).willReturn(Optional.of(domain));

        assertThat(underTest.findByMarketId(MARKET_ID)).contains(domain);
    }
}