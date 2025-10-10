package com.github.saphyra.apphub.service.custom.elite_base.dao.station;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.custom.elite_base.dao.Allegiance;
import com.github.saphyra.apphub.service.custom.elite_base.dao.EconomyEnum;
import com.github.saphyra.apphub.service.custom.elite_base.dao.StationType;
import com.github.saphyra.apphub.service.custom.elite_base.dao.station.station_economy.StationEconomy;
import com.github.saphyra.apphub.service.custom.elite_base.dao.station.station_economy.StationEconomyFactory;
import com.github.saphyra.apphub.service.custom.elite_base.dao.station.station_service.StationServiceEnum;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.structure.Economy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class StationFactoryTest {
    private static final UUID STATION_ID = UUID.randomUUID();
    private static final UUID STAR_SYSTEM_ID = UUID.randomUUID();
    private static final UUID BODY_ID = UUID.randomUUID();
    private static final UUID CONTROLLING_FACTION_ID = UUID.randomUUID();
    private static final LocalDateTime LAST_UPDATE = LocalDateTime.now();
    private static final String STATION_NAME = "station-name";
    private static final Long MARKET_ID = 34L;

    @Mock
    private IdGenerator idGenerator;

    @Mock
    private StationEconomyFactory stationEconomyFactory;

    @InjectMocks
    private StationFactory underTest;

    @Mock
    private StationEconomy stationEconomy;

    @Mock
    private Economy economy;

    @Test
    void create() {
        given(idGenerator.randomUuid()).willReturn(STATION_ID);
        given(stationEconomyFactory.create(STATION_ID, economy)).willReturn(stationEconomy);

        assertThat(underTest.create(
            LAST_UPDATE,
            STAR_SYSTEM_ID,
            BODY_ID,
            STATION_NAME,
            StationType.SURFACE_STATION,
            MARKET_ID,
            Allegiance.ALLIANCE,
            EconomyEnum.AGRICULTURE,
            List.of(StationServiceEnum.CREW_LOUNGE),
            List.of(economy),
            CONTROLLING_FACTION_ID
        ))
            .returns(STATION_ID, Station::getId)
            .returns(LAST_UPDATE, Station::getLastUpdate)
            .returns(STAR_SYSTEM_ID, Station::getStarSystemId)
            .returns(BODY_ID, Station::getBodyId)
            .returns(STATION_NAME, Station::getStationName)
            .returns(StationType.SURFACE_STATION, Station::getType)
            .returns(MARKET_ID, Station::getMarketId)
            .returns(Allegiance.ALLIANCE, Station::getAllegiance)
            .returns(EconomyEnum.AGRICULTURE, Station::getEconomy)
            .returns(List.of(StationServiceEnum.CREW_LOUNGE), Station::getServices)
            .returns(List.of(stationEconomy), Station::getEconomies)
            .returns(CONTROLLING_FACTION_ID, Station::getControllingFactionId);
    }
}