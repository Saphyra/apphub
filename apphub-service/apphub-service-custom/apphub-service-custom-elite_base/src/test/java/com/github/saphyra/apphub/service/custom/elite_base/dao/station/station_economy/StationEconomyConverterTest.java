package com.github.saphyra.apphub.service.custom.elite_base.dao.station.station_economy;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.service.custom.elite_base.dao.EconomyEnum;
import com.github.saphyra.apphub.service.custom.elite_base.dao.station.station_economy.StationEconomy;
import com.github.saphyra.apphub.service.custom.elite_base.dao.station.station_economy.StationEconomyConverter;
import com.github.saphyra.apphub.service.custom.elite_base.dao.station.station_economy.StationEconomyEntity;
import com.github.saphyra.apphub.service.custom.elite_base.dao.station.station_economy.StationEconomyEntityId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class StationEconomyConverterTest {
    private static final Double PROPORTION = 3214.34;
    private static final UUID STATION_ID = UUID.randomUUID();
    private static final String STATION_ID_STRING = "station-id";

    @Mock
    private UuidConverter uuidConverter;

    @InjectMocks
    private StationEconomyConverter underTest;

    @Test
    void convertDomain() {
        StationEconomy domain = StationEconomy.builder()
            .stationId(STATION_ID)
            .economy(EconomyEnum.AGRICULTURE)
            .proportion(PROPORTION)
            .build();

        given(uuidConverter.convertDomain(STATION_ID)).willReturn(STATION_ID_STRING);

        assertThat(underTest.convertDomain(domain))
            .returns(STATION_ID_STRING, stationEconomyEntity -> stationEconomyEntity.getId().getStationId())
            .returns(EconomyEnum.AGRICULTURE, stationEconomyEntity -> stationEconomyEntity.getId().getEconomy())
            .returns(PROPORTION, StationEconomyEntity::getProportion);
    }

    @Test
    void convertEntity() {
        StationEconomyEntity in = StationEconomyEntity.builder()
            .id(StationEconomyEntityId.builder()
                .stationId(STATION_ID_STRING)
                .economy(EconomyEnum.AGRICULTURE)
                .build())
            .proportion(PROPORTION)
            .build();

        given(uuidConverter.convertEntity(STATION_ID_STRING)).willReturn(STATION_ID);

        assertThat(underTest.convertEntity(in))
            .returns(STATION_ID, StationEconomy::getStationId)
            .returns(EconomyEnum.AGRICULTURE, StationEconomy::getEconomy)
            .returns(PROPORTION, StationEconomy::getProportion);
    }
}