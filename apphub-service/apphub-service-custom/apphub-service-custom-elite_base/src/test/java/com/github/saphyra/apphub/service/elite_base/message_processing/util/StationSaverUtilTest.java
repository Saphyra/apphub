package com.github.saphyra.apphub.service.elite_base.message_processing.util;

import com.github.saphyra.apphub.service.elite_base.message_processing.dao.Allegiance;
import com.github.saphyra.apphub.service.elite_base.message_processing.dao.EconomyEnum;
import com.github.saphyra.apphub.service.elite_base.message_processing.dao.FactionStateEnum;
import com.github.saphyra.apphub.service.elite_base.message_processing.dao.StationType;
import com.github.saphyra.apphub.service.elite_base.message_processing.dao.commodity.CommodityLocation;
import com.github.saphyra.apphub.service.elite_base.message_processing.dao.fleet_carrier.FleetCarrier;
import com.github.saphyra.apphub.service.elite_base.message_processing.dao.fleet_carrier.FleetCarrierDockingAccess;
import com.github.saphyra.apphub.service.elite_base.message_processing.dao.station.Station;
import com.github.saphyra.apphub.service.elite_base.message_processing.saver.FleetCarrierSaver;
import com.github.saphyra.apphub.service.elite_base.message_processing.saver.StationSaver;
import com.github.saphyra.apphub.service.elite_base.message_processing.structure.Economy;
import com.github.saphyra.apphub.service.elite_base.message_processing.structure.journal.ControllingFaction;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class StationSaverUtilTest {
    private static final LocalDateTime LAST_UPDATE = LocalDateTime.now();
    private static final UUID STAR_SYSTEM_ID = UUID.randomUUID();
    private static final UUID BODY_ID = UUID.randomUUID();
    private static final Long MARKET_ID = 312421L;
    private static final String STATION_NAME = "station-name";
    private static final String STATION_SERVICE = "service";
    private static final UUID EXTERNAL_REFERENCE = UUID.randomUUID();
    private static final String CONTROLLING_FACTION_NAME = "controlling-faction-name";

    @Mock
    private CommodityLocationFetcher commodityLocationFetcher;

    @Mock
    private FleetCarrierSaver fleetCarrierSaver;

    @Mock
    private StationSaver stationSaver;

    @InjectMocks
    private StationSaverUtil underTest;

    @Mock
    private Economy economy;

    @Mock
    private ControllingFaction controllingFaction;

    @Mock
    private FleetCarrier fleetCarrier;

    @Mock
    private Station station;

    @Test
    void fleetCarrierByStationType() {
        given(fleetCarrierSaver.save(LAST_UPDATE, STAR_SYSTEM_ID, STATION_NAME, null, FleetCarrierDockingAccess.ALL, MARKET_ID)).willReturn(fleetCarrier);
        given(fleetCarrier.getId()).willReturn(EXTERNAL_REFERENCE);

        assertThat(underTest.saveStationOrFleetCarrier(
            LAST_UPDATE,
            STAR_SYSTEM_ID,
            BODY_ID,
            "FleetCarrier",
            MARKET_ID,
            STATION_NAME,
            Allegiance.ALLIANCE,
            EconomyEnum.CARRIER,
            new String[]{STATION_SERVICE},
            new Economy[]{economy},
            "all",
            controllingFaction
        ))
            .returns(CommodityLocation.FLEET_CARRIER, StationSaveResult::getCommodityLocation)
            .returns(EXTERNAL_REFERENCE, StationSaveResult::getExternalReference);
    }

    @Test
    void stationByStationType() {
        Economy[] economies = {economy};
        String[] services = {STATION_SERVICE};
        given(controllingFaction.getFactionName()).willReturn(CONTROLLING_FACTION_NAME);
        given(controllingFaction.getState()).willReturn(FactionStateEnum.BLIGHT.name());
        given(stationSaver.save(
            LAST_UPDATE,
            STAR_SYSTEM_ID,
            BODY_ID,
            STATION_NAME,
            StationType.CORIOLIS,
            MARKET_ID,
            Allegiance.ALLIANCE,
            EconomyEnum.AGRICULTURE,
            services,
            economies,
            CONTROLLING_FACTION_NAME,
            FactionStateEnum.BLIGHT
        ))
            .willReturn(station);
        given(station.getId()).willReturn(EXTERNAL_REFERENCE);

        assertThat(underTest.saveStationOrFleetCarrier(
            LAST_UPDATE,
            STAR_SYSTEM_ID,
            BODY_ID,
            "Coriolis",
            MARKET_ID,
            STATION_NAME,
            Allegiance.ALLIANCE,
            EconomyEnum.AGRICULTURE,
            services,
            economies,
            "all",
            controllingFaction
        ))
            .returns(CommodityLocation.STATION, StationSaveResult::getCommodityLocation)
            .returns(EXTERNAL_REFERENCE, StationSaveResult::getExternalReference);
    }

    @Test
    void fleetCarrierByEconomy() {
        given(fleetCarrierSaver.save(LAST_UPDATE, STAR_SYSTEM_ID, STATION_NAME, null, FleetCarrierDockingAccess.ALL, MARKET_ID)).willReturn(fleetCarrier);
        given(fleetCarrier.getId()).willReturn(EXTERNAL_REFERENCE);

        assertThat(underTest.saveStationOrFleetCarrier(
            LAST_UPDATE,
            STAR_SYSTEM_ID,
            BODY_ID,
            null,
            MARKET_ID,
            STATION_NAME,
            Allegiance.ALLIANCE,
            EconomyEnum.CARRIER,
            new String[]{STATION_SERVICE},
            new Economy[]{economy},
            "all",
            controllingFaction
        ))
            .returns(CommodityLocation.FLEET_CARRIER, StationSaveResult::getCommodityLocation)
            .returns(EXTERNAL_REFERENCE, StationSaveResult::getExternalReference);
    }

    @Test
    void stationByEconomy() {
        Economy[] economies = {economy};
        String[] services = {STATION_SERVICE};
        given(controllingFaction.getFactionName()).willReturn(CONTROLLING_FACTION_NAME);
        given(controllingFaction.getState()).willReturn(FactionStateEnum.BLIGHT.name());
        given(stationSaver.save(
            LAST_UPDATE,
            STAR_SYSTEM_ID,
            BODY_ID,
            STATION_NAME,
            null,
            MARKET_ID,
            Allegiance.ALLIANCE,
            EconomyEnum.AGRICULTURE,
            services,
            economies,
            CONTROLLING_FACTION_NAME,
            FactionStateEnum.BLIGHT
        ))
            .willReturn(station);
        given(station.getId()).willReturn(EXTERNAL_REFERENCE);

        assertThat(underTest.saveStationOrFleetCarrier(
            LAST_UPDATE,
            STAR_SYSTEM_ID,
            BODY_ID,
            null,
            MARKET_ID,
            STATION_NAME,
            Allegiance.ALLIANCE,
            EconomyEnum.AGRICULTURE,
            services,
            economies,
            "all",
            controllingFaction
        ))
            .returns(CommodityLocation.STATION, StationSaveResult::getCommodityLocation)
            .returns(EXTERNAL_REFERENCE, StationSaveResult::getExternalReference);
    }

    @Test
    void fleetCarrierByEconomies() {
        given(fleetCarrierSaver.save(LAST_UPDATE, STAR_SYSTEM_ID, STATION_NAME, null, FleetCarrierDockingAccess.ALL, MARKET_ID)).willReturn(fleetCarrier);
        given(fleetCarrier.getId()).willReturn(EXTERNAL_REFERENCE);
        given(economy.getName()).willReturn("Carrier");

        assertThat(underTest.saveStationOrFleetCarrier(
            LAST_UPDATE,
            STAR_SYSTEM_ID,
            BODY_ID,
            null,
            MARKET_ID,
            STATION_NAME,
            Allegiance.ALLIANCE,
            null,
            new String[]{STATION_SERVICE},
            new Economy[]{economy},
            "all",
            controllingFaction
        ))
            .returns(CommodityLocation.FLEET_CARRIER, StationSaveResult::getCommodityLocation)
            .returns(EXTERNAL_REFERENCE, StationSaveResult::getExternalReference);
    }

    @Test
    void stationByEconomies() {
        Economy[] economies = {economy};
        String[] services = {STATION_SERVICE};
        given(economy.getName()).willReturn("Agri");
        given(controllingFaction.getFactionName()).willReturn(CONTROLLING_FACTION_NAME);
        given(controllingFaction.getState()).willReturn(FactionStateEnum.BLIGHT.name());
        given(stationSaver.save(
            LAST_UPDATE,
            STAR_SYSTEM_ID,
            BODY_ID,
            STATION_NAME,
            null,
            MARKET_ID,
            Allegiance.ALLIANCE,
            null,
            services,
            economies,
            CONTROLLING_FACTION_NAME,
            FactionStateEnum.BLIGHT
        ))
            .willReturn(station);
        given(station.getId()).willReturn(EXTERNAL_REFERENCE);

        assertThat(underTest.saveStationOrFleetCarrier(
            LAST_UPDATE,
            STAR_SYSTEM_ID,
            BODY_ID,
            null,
            MARKET_ID,
            STATION_NAME,
            Allegiance.ALLIANCE,
            null,
            services,
            economies,
            "all",
            controllingFaction
        ))
            .returns(CommodityLocation.STATION, StationSaveResult::getCommodityLocation)
            .returns(EXTERNAL_REFERENCE, StationSaveResult::getExternalReference);
    }

    @Test
    void stationByMarketId() {
        String[] services = {STATION_SERVICE};
        given(controllingFaction.getFactionName()).willReturn(CONTROLLING_FACTION_NAME);
        given(controllingFaction.getState()).willReturn(FactionStateEnum.BLIGHT.name());
        given(stationSaver.save(
            LAST_UPDATE,
            STAR_SYSTEM_ID,
            BODY_ID,
            STATION_NAME,
            null,
            MARKET_ID,
            Allegiance.ALLIANCE,
            null,
            services,
            null,
            CONTROLLING_FACTION_NAME,
            FactionStateEnum.BLIGHT
        ))
            .willReturn(station);
        given(station.getId()).willReturn(EXTERNAL_REFERENCE);
        given(commodityLocationFetcher.fetchCommodityLocationByMarketId(MARKET_ID)).willReturn(CommodityLocation.STATION);

        assertThat(underTest.saveStationOrFleetCarrier(
            LAST_UPDATE,
            STAR_SYSTEM_ID,
            BODY_ID,
            null,
            MARKET_ID,
            STATION_NAME,
            Allegiance.ALLIANCE,
            null,
            services,
            null,
            "all",
            controllingFaction
        ))
            .returns(CommodityLocation.STATION, StationSaveResult::getCommodityLocation)
            .returns(EXTERNAL_REFERENCE, StationSaveResult::getExternalReference);
    }

    @Test
    void unknown() {
        String[] services = {STATION_SERVICE};
        given(commodityLocationFetcher.fetchCommodityLocationByMarketId(MARKET_ID)).willReturn(CommodityLocation.UNKNOWN);

        assertThat(underTest.saveStationOrFleetCarrier(
            LAST_UPDATE,
            STAR_SYSTEM_ID,
            BODY_ID,
            null,
            MARKET_ID,
            STATION_NAME,
            Allegiance.ALLIANCE,
            null,
            services,
            null,
            "all",
            controllingFaction
        ))
            .returns(CommodityLocation.UNKNOWN, StationSaveResult::getCommodityLocation)
            .returns(null, StationSaveResult::getExternalReference);
    }
}