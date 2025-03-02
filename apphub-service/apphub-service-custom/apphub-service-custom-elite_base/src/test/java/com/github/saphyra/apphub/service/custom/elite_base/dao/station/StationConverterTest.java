package com.github.saphyra.apphub.service.custom.elite_base.dao.station;

import com.github.saphyra.apphub.lib.common_util.DateTimeConverter;
import com.github.saphyra.apphub.lib.common_util.LazyLoadedField;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.service.custom.elite_base.dao.station.Station;
import com.github.saphyra.apphub.service.custom.elite_base.dao.station.StationConverter;
import com.github.saphyra.apphub.service.custom.elite_base.dao.station.StationEntity;
import com.github.saphyra.apphub.service.custom.elite_base.dao.station.station_economy.StationEconomy;
import com.github.saphyra.apphub.service.custom.elite_base.dao.station.station_economy.StationEconomyDao;
import com.github.saphyra.apphub.service.custom.elite_base.dao.station.station_economy.StationEconomySyncService;
import com.github.saphyra.apphub.service.custom.elite_base.dao.station.station_service.StationService;
import com.github.saphyra.apphub.service.custom.elite_base.dao.station.station_service.StationServiceDao;
import com.github.saphyra.apphub.service.custom.elite_base.dao.station.station_service.StationServiceEnum;
import com.github.saphyra.apphub.service.custom.elite_base.dao.station.station_service.StationServiceSyncService;
import com.github.saphyra.apphub.service.custom.elite_base.dao.Allegiance;
import com.github.saphyra.apphub.service.custom.elite_base.dao.EconomyEnum;
import com.github.saphyra.apphub.service.custom.elite_base.dao.StationType;
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
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class StationConverterTest {
    private static final UUID ID = UUID.randomUUID();
    private static final UUID STAR_SYSTEM_ID = UUID.randomUUID();
    private static final UUID BODY_ID = UUID.randomUUID();
    private static final UUID CONTROLLING_FACTION_ID = UUID.randomUUID();
    private static final LocalDateTime LAST_UPDATE = LocalDateTime.now();
    private static final String STATION_NAME = "station-name";
    private static final Long MARKET_ID = 13421L;
    private static final String ID_STRING = "id";
    private static final String STAR_SYSTEM_ID_STRING = "star-system-id";
    private static final String BODY_ID_STRING = "body-id";
    private static final String CONTROLLING_FACTION_ID_STRING = "controlling-faction-id";
    private static final String LAST_UPDATE_STRING = "last-update";

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private DateTimeConverter dateTimeConverter;

    @Mock
    private StationEconomySyncService stationEconomySyncService;

    @Mock
    private StationServiceSyncService stationServiceSyncService;

    @Mock
    private StationEconomyDao stationEconomyDao;

    @Mock
    private StationServiceDao stationServiceDao;

    @InjectMocks
    private StationConverter underTest;

    @Mock
    private StationEconomy stationEconomy;

    @Mock
    private StationService stationService;

    @Test
    void convertDomain() {
        Station in = Station.builder()
            .id(ID)
            .lastUpdate(LAST_UPDATE)
            .starSystemId(STAR_SYSTEM_ID)
            .bodyId(BODY_ID)
            .stationName(STATION_NAME)
            .type(StationType.BERNAL)
            .marketId(MARKET_ID)
            .allegiance(Allegiance.ALLIANCE)
            .economy(EconomyEnum.ENGINEER)
            .controllingFactionId(CONTROLLING_FACTION_ID)
            .economies(LazyLoadedField.loaded(List.of(stationEconomy)))
            .services(LazyLoadedField.loaded(List.of(StationServiceEnum.CREW_LOUNGE)))
            .build();

        given(uuidConverter.convertDomain(ID)).willReturn(ID_STRING);
        given(uuidConverter.convertDomain(STAR_SYSTEM_ID)).willReturn(STAR_SYSTEM_ID_STRING);
        given(uuidConverter.convertDomain(BODY_ID)).willReturn(BODY_ID_STRING);
        given(uuidConverter.convertDomain(CONTROLLING_FACTION_ID)).willReturn(CONTROLLING_FACTION_ID_STRING);
        given(dateTimeConverter.convertDomain(LAST_UPDATE)).willReturn(LAST_UPDATE_STRING);

        assertThat(underTest.convertDomain(in))
            .returns(ID_STRING, StationEntity::getId)
            .returns(LAST_UPDATE_STRING, StationEntity::getLastUpdate)
            .returns(STAR_SYSTEM_ID_STRING, StationEntity::getStarSystemId)
            .returns(BODY_ID_STRING, StationEntity::getBodyId)
            .returns(STATION_NAME, StationEntity::getStationName)
            .returns(StationType.BERNAL, StationEntity::getType)
            .returns(MARKET_ID, StationEntity::getMarketId)
            .returns(Allegiance.ALLIANCE, StationEntity::getAllegiance)
            .returns(EconomyEnum.ENGINEER, StationEntity::getEconomy)
            .returns(CONTROLLING_FACTION_ID_STRING, StationEntity::getControllingFactionId);

        then(stationServiceSyncService).should().sync(ID, List.of(StationServiceEnum.CREW_LOUNGE));
        then(stationEconomySyncService).should().sync(ID, List.of(stationEconomy));
    }

    @Test
    void convertEntity() {
        StationEntity in = StationEntity.builder()
            .id(ID_STRING)
            .lastUpdate(LAST_UPDATE_STRING)
            .starSystemId(STAR_SYSTEM_ID_STRING)
            .bodyId(BODY_ID_STRING)
            .stationName(STATION_NAME)
            .type(StationType.BERNAL)
            .marketId(MARKET_ID)
            .allegiance(Allegiance.ALLIANCE)
            .economy(EconomyEnum.ENGINEER)
            .controllingFactionId(CONTROLLING_FACTION_ID_STRING)
            .build();

        given(uuidConverter.convertEntity(ID_STRING)).willReturn(ID);
        given(uuidConverter.convertEntity(STAR_SYSTEM_ID_STRING)).willReturn(STAR_SYSTEM_ID);
        given(uuidConverter.convertEntity(BODY_ID_STRING)).willReturn(BODY_ID);
        given(uuidConverter.convertEntity(CONTROLLING_FACTION_ID_STRING)).willReturn(CONTROLLING_FACTION_ID);
        given(dateTimeConverter.convertToLocalDateTime(LAST_UPDATE_STRING)).willReturn(LAST_UPDATE);

        given(stationServiceDao.getByStationId(ID)).willReturn(List.of(stationService));
        given(stationEconomyDao.getByStationId(ID)).willReturn(List.of(stationEconomy));
        given(stationService.getService()).willReturn(StationServiceEnum.CREW_LOUNGE);

        assertThat(underTest.convertEntity(in))
            .returns(ID, Station::getId)
            .returns(LAST_UPDATE, Station::getLastUpdate)
            .returns(STAR_SYSTEM_ID, Station::getStarSystemId)
            .returns(BODY_ID, Station::getBodyId)
            .returns(STATION_NAME, Station::getStationName)
            .returns(StationType.BERNAL, Station::getType)
            .returns(MARKET_ID, Station::getMarketId)
            .returns(Allegiance.ALLIANCE, Station::getAllegiance)
            .returns(EconomyEnum.ENGINEER, Station::getEconomy)
            .returns(CONTROLLING_FACTION_ID, Station::getControllingFactionId)
            .returns(List.of(StationServiceEnum.CREW_LOUNGE), Station::getServices)
            .returns(List.of(stationEconomy), Station::getEconomies);
    }
}