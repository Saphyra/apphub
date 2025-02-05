package com.github.saphyra.apphub.service.custom.elite_base.message_processing.saver;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.dao.minor_faction.MinorFaction;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.dao.station.Station;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.dao.station.StationDao;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.dao.station.StationFactory;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.dao.station.station_economy.StationEconomy;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.dao.station.station_economy.StationEconomyFactory;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.dao.station.station_service.StationServiceEnum;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.dao.Allegiance;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.dao.EconomyEnum;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.dao.FactionStateEnum;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.dao.StationType;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.structure.Economy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class StationSaverTest {
    private static final LocalDateTime LAST_UPDATE = LocalDateTime.now();
    private static final UUID BODY_ID = UUID.randomUUID();
    private static final String STATION_NAME = "station-name";
    private static final Long MARKET_ID = 342L;
    private static final String CONTROLLING_FACTION_NAME = "controlling-faction-name";
    private static final UUID STAR_SYSTEM_ID = UUID.randomUUID();
    private static final UUID STATION_ID = UUID.randomUUID();
    private static final UUID CONTROLLING_FACTION_ID = UUID.randomUUID();

    @Mock
    private StationDao stationDao;

    @Mock
    private StationFactory stationFactory;

    @Mock
    private StationEconomyFactory stationEconomyFactory;

    @Mock
    private IdGenerator idGenerator;

    @Mock
    private MinorFactionSaver minorFactionSaver;

    @InjectMocks
    private StationSaver underTest;

    @Mock
    private Station station;

    @Mock
    private MinorFaction minorFaction;

    @Mock
    private StationEconomy stationEconomy;

    @Mock
    private Economy economy;

    @Test
    void nullMarketIdAndStarSystemId() {
        assertThat(catchThrowable(() -> underTest.save(
            LAST_UPDATE,
            null,
            BODY_ID,
            STATION_NAME,
            StationType.SURFACE_STATION,
            null,
            Allegiance.ALLIANCE,
            EconomyEnum.AGRICULTURE,
            null,
            null,
            CONTROLLING_FACTION_NAME,
            FactionStateEnum.BLIGHT
        ))).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void nullMarketIdAndStationName() {
        assertThat(catchThrowable(() -> underTest.save(
            LAST_UPDATE,
            STAR_SYSTEM_ID,
            BODY_ID,
            null,
            StationType.SURFACE_STATION,
            null,
            Allegiance.ALLIANCE,
            EconomyEnum.AGRICULTURE,
            null,
            null,
            CONTROLLING_FACTION_NAME,
            FactionStateEnum.BLIGHT
        ))).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void carrierEconomy() {
        assertThat(catchThrowable(() -> underTest.save(
            LAST_UPDATE,
            STAR_SYSTEM_ID,
            BODY_ID,
            STATION_NAME,
            StationType.SURFACE_STATION,
            null,
            Allegiance.ALLIANCE,
            EconomyEnum.CARRIER,
            null,
            null,
            CONTROLLING_FACTION_NAME,
            FactionStateEnum.BLIGHT
        ))).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void carrierStationType() {
        assertThat(catchThrowable(() -> underTest.save(
            LAST_UPDATE,
            STAR_SYSTEM_ID,
            BODY_ID,
            STATION_NAME,
            StationType.FLEET_CARRIER,
            null,
            Allegiance.ALLIANCE,
            EconomyEnum.AGRICULTURE,
            null,
            null,
            CONTROLLING_FACTION_NAME,
            FactionStateEnum.BLIGHT
        ))).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void save_new() {
        String[] services = new String[]{StationServiceEnum.BARTENDER.getValue()};
        Economy[] economies = {economy};

        given(stationDao.findByMarketId(MARKET_ID)).willReturn(Optional.empty());
        given(stationDao.findByStarSystemIdAndStationName(STAR_SYSTEM_ID, STATION_NAME)).willReturn(Optional.empty());
        given(idGenerator.randomUuid()).willReturn(STATION_ID);
        given(minorFactionSaver.save(LAST_UPDATE, CONTROLLING_FACTION_NAME, FactionStateEnum.BLIGHT)).willReturn(minorFaction);
        given(minorFaction.getId()).willReturn(CONTROLLING_FACTION_ID);
        given(stationFactory.create(
            STATION_ID,
            LAST_UPDATE,
            STAR_SYSTEM_ID,
            BODY_ID,
            STATION_NAME,
            StationType.SURFACE_STATION,
            MARKET_ID,
            Allegiance.ALLIANCE,
            EconomyEnum.AGRICULTURE,
            List.of(StationServiceEnum.BARTENDER),
            List.of(stationEconomy),
            CONTROLLING_FACTION_ID
        ))
            .willReturn(station);
        given(stationEconomyFactory.create(STATION_ID, economy)).willReturn(stationEconomy);
        given(station.getLastUpdate()).willReturn(LAST_UPDATE.plusSeconds(1));

        assertThat(underTest.save(
            LAST_UPDATE,
            STAR_SYSTEM_ID,
            BODY_ID,
            STATION_NAME,
            StationType.SURFACE_STATION,
            MARKET_ID,
            Allegiance.ALLIANCE,
            EconomyEnum.AGRICULTURE,
            services,
            economies,
            CONTROLLING_FACTION_NAME,
            FactionStateEnum.BLIGHT
        )).isEqualTo(station);

        then(station).should(times(0)).setLastUpdate(any());
        then(station).should(times(0)).setBodyId(any());
        then(station).should(times(0)).setAllegiance(any());
        then(station).should(times(0)).setEconomy(any());
        then(station).should(times(0)).setServices(any());
        then(station).should(times(0)).setEconomies(any());
        then(station).should(times(0)).setType(any());
        then(station).should(times(0)).setControllingFactionId(any());
        then(stationDao).should().save(station);
    }

    @Test
    void save_existing() {
        String[] services = new String[]{StationServiceEnum.BARTENDER.getValue()};
        Economy[] economies = {economy};

        given(stationDao.findByMarketId(MARKET_ID)).willReturn(Optional.of(station));
        given(stationDao.findByStarSystemIdAndStationName(STAR_SYSTEM_ID, STATION_NAME)).willReturn(Optional.empty());
        given(idGenerator.randomUuid()).willReturn(STATION_ID);
        given(minorFactionSaver.save(LAST_UPDATE, CONTROLLING_FACTION_NAME, FactionStateEnum.BLIGHT)).willReturn(minorFaction);
        given(minorFaction.getId()).willReturn(CONTROLLING_FACTION_ID);
        given(stationEconomyFactory.create(STATION_ID, economy)).willReturn(stationEconomy);
        given(station.getLastUpdate()).willReturn(LAST_UPDATE.minusSeconds(1));

        assertThat(underTest.save(
            LAST_UPDATE,
            STAR_SYSTEM_ID,
            BODY_ID,
            STATION_NAME,
            StationType.SURFACE_STATION,
            MARKET_ID,
            Allegiance.ALLIANCE,
            EconomyEnum.AGRICULTURE,
            services,
            economies,
            CONTROLLING_FACTION_NAME,
            FactionStateEnum.BLIGHT
        )).isEqualTo(station);

        then(station).should().setLastUpdate(LAST_UPDATE);
        then(station).should().setBodyId(BODY_ID);
        then(station).should().setAllegiance(Allegiance.ALLIANCE);
        then(station).should().setEconomy(EconomyEnum.AGRICULTURE);
        then(station).should().setServices(any());
        then(station).should().setEconomies(any());
        then(station).should().setType(StationType.SURFACE_STATION);
        then(station).should().setControllingFactionId(CONTROLLING_FACTION_ID);
        then(stationDao).should().save(station);
    }
}