package com.github.saphyra.apphub.service.custom.elite_base.dao.station.station_economy;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.service.custom.elite_base.dao.station.station_economy.StationEconomy;
import com.github.saphyra.apphub.service.custom.elite_base.dao.station.station_economy.StationEconomyConverter;
import com.github.saphyra.apphub.service.custom.elite_base.dao.station.station_economy.StationEconomyDao;
import com.github.saphyra.apphub.service.custom.elite_base.dao.station.station_economy.StationEconomyEntity;
import com.github.saphyra.apphub.service.custom.elite_base.dao.station.station_economy.StationEconomyRepository;
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
class StationEconomyDaoTest {
    private static final UUID STATION_ID = UUID.randomUUID();
    private static final String STATION_ID_STRING = "station-id";

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private StationEconomyConverter converter;

    @Mock
    private StationEconomyRepository repository;

    @InjectMocks
    private StationEconomyDao underTest;

    @Mock
    private StationEconomy domain;

    @Mock
    private StationEconomyEntity entity;

    @Test
    void getByStationId() {
        given(uuidConverter.convertDomain(STATION_ID)).willReturn(STATION_ID_STRING);
        given(repository.getByIdStationId(STATION_ID_STRING)).willReturn(List.of(entity));
        given(converter.convertEntity(List.of(entity))).willReturn(List.of(domain));

        assertThat(underTest.getByStationId(STATION_ID)).containsExactly(domain);
    }
}