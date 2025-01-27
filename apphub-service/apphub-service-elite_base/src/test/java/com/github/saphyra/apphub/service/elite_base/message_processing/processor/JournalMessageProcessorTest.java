package com.github.saphyra.apphub.service.elite_base.message_processing.processor;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import com.github.saphyra.apphub.lib.performance_reporting.PerformanceReporter;
import com.github.saphyra.apphub.service.elite_base.message_handling.dao.EdMessage;
import com.github.saphyra.apphub.service.elite_base.message_processing.dao.Allegiance;
import com.github.saphyra.apphub.service.elite_base.message_processing.dao.EconomyEnum;
import com.github.saphyra.apphub.service.elite_base.message_processing.dao.SecurityLevel;
import com.github.saphyra.apphub.service.elite_base.message_processing.dao.StationType;
import com.github.saphyra.apphub.service.elite_base.message_processing.dao.body.Body;
import com.github.saphyra.apphub.service.elite_base.message_processing.dao.body.BodyType;
import com.github.saphyra.apphub.service.elite_base.message_processing.dao.body_data.ReserveLevel;
import com.github.saphyra.apphub.service.elite_base.message_processing.dao.minor_faction.MinorFaction;
import com.github.saphyra.apphub.service.elite_base.message_processing.dao.star_system.StarSystem;
import com.github.saphyra.apphub.service.elite_base.message_processing.dao.star_system.StarType;
import com.github.saphyra.apphub.service.elite_base.message_processing.dao.star_system_data.Power;
import com.github.saphyra.apphub.service.elite_base.message_processing.dao.star_system_data.PowerplayState;
import com.github.saphyra.apphub.service.elite_base.message_processing.saver.BodyDataSaver;
import com.github.saphyra.apphub.service.elite_base.message_processing.saver.BodySaver;
import com.github.saphyra.apphub.service.elite_base.message_processing.saver.MinorFactionSaver;
import com.github.saphyra.apphub.service.elite_base.message_processing.saver.StarSystemDataSaver;
import com.github.saphyra.apphub.service.elite_base.message_processing.saver.StarSystemSaver;
import com.github.saphyra.apphub.service.elite_base.message_processing.structure.Economy;
import com.github.saphyra.apphub.service.elite_base.message_processing.structure.journal.ControllingFaction;
import com.github.saphyra.apphub.service.elite_base.message_processing.structure.journal.EdConflict;
import com.github.saphyra.apphub.service.elite_base.message_processing.structure.journal.Faction;
import com.github.saphyra.apphub.service.elite_base.message_processing.structure.journal.NamePercentPair;
import com.github.saphyra.apphub.service.elite_base.message_processing.structure.journal.Ring;
import com.github.saphyra.apphub.service.elite_base.message_processing.structure.journal.message.CarrierJumpJournalMessage;
import com.github.saphyra.apphub.service.elite_base.message_processing.structure.journal.message.DockedJournalMessage;
import com.github.saphyra.apphub.service.elite_base.message_processing.structure.journal.message.FsdJumpJournalMessage;
import com.github.saphyra.apphub.service.elite_base.message_processing.structure.journal.message.LocationJournalMessage;
import com.github.saphyra.apphub.service.elite_base.message_processing.structure.journal.message.SaaSignalFoundJournalMessage;
import com.github.saphyra.apphub.service.elite_base.message_processing.structure.journal.message.ScanJournalMessage;
import com.github.saphyra.apphub.service.elite_base.message_processing.util.ControllingFactionParser;
import com.github.saphyra.apphub.service.elite_base.message_processing.util.StationSaverUtil;
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
import static org.mockito.Mockito.doAnswer;

@ExtendWith(MockitoExtension.class)
class JournalMessageProcessorTest {
    private static final String MESSAGE = "message";
    private static final LocalDateTime TIMESTAMP = LocalDateTime.now();
    private static final Long STAR_ID = 23423L;
    private static final String STAR_NAME = "star-name";
    private static final Double[] STAR_POSITION = new Double[]{234.34};
    private static final UUID STAR_SYSTEM_ID = UUID.randomUUID();
    private static final Long BODY_ID = 23432L;
    private static final String BODY_NAME = "body-name";
    private static final Double DISTANCE_FROM_STAR = 23423.324;
    private static final UUID INTERNAL_BODY_ID = UUID.randomUUID();
    private static final String MATERIAL = "material";
    private static final Double PERCENT = 23423.23;
    private static final Double SURFACE_GRAVITY = 234.34;
    private static final Long POPULATION = 2342L;
    private static final String SERVICE = "service";
    private static final Long MARKET_ID = 243545L;
    private static final String STATION_NAME = "station-name";

    @Mock
    private ObjectMapperWrapper objectMapperWrapper;

    @Mock
    private StarSystemSaver starSystemSaver;

    @Mock
    private BodySaver bodySaver;

    @Mock
    private BodyDataSaver bodyDataSaver;

    @Mock
    private StarSystemDataSaver starSystemDataSaver;

    @Mock
    private MinorFactionSaver minorFactionSaver;

    @Mock
    private StationSaverUtil stationSaverUtil;

    @Mock
    private ControllingFactionParser controllingFactionParser;

    @Mock
    private PerformanceReporter performanceReporter;

    @InjectMocks
    private JournalMessageProcessor underTest;

    @Mock
    private EdMessage edMessage;

    @Mock
    private JsonNode jsonNode;

    @Mock
    private StarSystem starSystem;

    @Mock
    private Body body;

    @Mock
    private Ring ring;

    @Mock
    private Faction faction;

    @Mock
    private MinorFaction minorFaction;

    @Mock
    private ControllingFaction controllingFaction;

    @Mock
    private EdConflict edConflict;

    @Mock
    private Economy economy;

    @Test
    void canProcess() {
        given(edMessage.getSchemaRef()).willReturn(SchemaRefs.JOURNAL);

        assertThat(underTest.canProcess(edMessage)).isTrue();
    }

    @Test
    void processMessage_unhandledEvent() {
        given(edMessage.getMessage()).willReturn(MESSAGE);
        given(objectMapperWrapper.readTree(MESSAGE)).willReturn(jsonNode);
        given(jsonNode.get("event")).willReturn(jsonNode);
        given(jsonNode.asText()).willReturn("asdfaf");
        doAnswer(invocation -> {
            invocation.getArgument(0, Runnable.class).run();
            return null;
        }).when(performanceReporter).wrap(any(Runnable.class), any(), any());

        assertThat(catchThrowable(() -> underTest.processMessage(edMessage))).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void processMessage_scan() {
        given(edMessage.getMessage()).willReturn(MESSAGE);
        given(objectMapperWrapper.readTree(MESSAGE)).willReturn(jsonNode);
        given(jsonNode.get("event")).willReturn(jsonNode);
        given(jsonNode.asText()).willReturn("Scan");

        NamePercentPair[] materials = new NamePercentPair[]{new NamePercentPair(MATERIAL, PERCENT)};
        Ring[] rings = new Ring[]{ring};

        ScanJournalMessage scanJournalMessage = new ScanJournalMessage();
        scanJournalMessage.setTimestamp(TIMESTAMP);
        scanJournalMessage.setStarId(STAR_ID);
        scanJournalMessage.setStarName(STAR_NAME);
        scanJournalMessage.setStarPosition(STAR_POSITION);
        scanJournalMessage.setStarType("a");
        scanJournalMessage.setBodyId(BODY_ID);
        scanJournalMessage.setBodyName(BODY_NAME);
        scanJournalMessage.setDistanceFromStar(DISTANCE_FROM_STAR);
        scanJournalMessage.setLandable(true);
        scanJournalMessage.setSurfaceGravity(SURFACE_GRAVITY);
        scanJournalMessage.setMaterials(materials);
        scanJournalMessage.setRings(rings);
        scanJournalMessage.setReserveLevel(ReserveLevel.PRISTINE.getValue());

        given(objectMapperWrapper.readValue(MESSAGE, ScanJournalMessage.class)).willReturn(scanJournalMessage);
        given(starSystemSaver.save(TIMESTAMP, STAR_ID, STAR_NAME, STAR_POSITION, StarType.A)).willReturn(starSystem);
        given(starSystem.getId()).willReturn(STAR_SYSTEM_ID);
        given(bodySaver.save(TIMESTAMP, STAR_SYSTEM_ID, BodyType.PLANET, BODY_ID, BODY_NAME, DISTANCE_FROM_STAR)).willReturn(body);
        given(body.getId()).willReturn(INTERNAL_BODY_ID);
        doAnswer(invocation -> {
            invocation.getArgument(0, Runnable.class).run();
            return null;
        }).when(performanceReporter).wrap(any(Runnable.class), any(), any());

        underTest.processMessage(edMessage);

        then(bodyDataSaver).should().save(INTERNAL_BODY_ID, TIMESTAMP, true, SURFACE_GRAVITY, ReserveLevel.PRISTINE, true, materials, rings);
    }

    @Test
    void processMessage_fsdJump() {
        given(edMessage.getMessage()).willReturn(MESSAGE);
        given(objectMapperWrapper.readTree(MESSAGE)).willReturn(jsonNode);
        given(jsonNode.get("event")).willReturn(jsonNode);
        given(jsonNode.asText()).willReturn("FSDJump");

        Faction[] factions = new Faction[]{faction};
        EdConflict[] conflicts = new EdConflict[]{edConflict};

        FsdJumpJournalMessage fsdJumpJournalMessage = new FsdJumpJournalMessage();
        fsdJumpJournalMessage.setTimestamp(TIMESTAMP);
        fsdJumpJournalMessage.setStarId(STAR_ID);
        fsdJumpJournalMessage.setStarName(STAR_NAME);
        fsdJumpJournalMessage.setStarPosition(STAR_POSITION);
        fsdJumpJournalMessage.setBodyId(BODY_ID);
        fsdJumpJournalMessage.setBodyName(BODY_NAME);
        fsdJumpJournalMessage.setFactions(factions);
        fsdJumpJournalMessage.setPopulation(POPULATION);
        fsdJumpJournalMessage.setAllegiance(Allegiance.ALLIANCE.getValue());
        fsdJumpJournalMessage.setEconomy(EconomyEnum.AGRICULTURE.getValue());
        fsdJumpJournalMessage.setSecondEconomy(EconomyEnum.COLONY.getValue());
        fsdJumpJournalMessage.setSecurityLevel(SecurityLevel.ANARCHY.getValue());
        fsdJumpJournalMessage.setControllingPower(Power.AISLING_DUVAL.getValue());
        fsdJumpJournalMessage.setPowerplayState(PowerplayState.FORTIFIED.getValue());
        fsdJumpJournalMessage.setFactions(factions);
        fsdJumpJournalMessage.setControllingFaction(controllingFaction);
        fsdJumpJournalMessage.setPowers(new String[]{Power.NAKATO_KAINE.getValue()});
        fsdJumpJournalMessage.setConflicts(conflicts);
        doAnswer(invocation -> {
            invocation.getArgument(0, Runnable.class).run();
            return null;
        }).when(performanceReporter).wrap(any(Runnable.class), any(), any());

        given(objectMapperWrapper.readValue(MESSAGE, FsdJumpJournalMessage.class)).willReturn(fsdJumpJournalMessage);
        given(starSystemSaver.save(TIMESTAMP, STAR_ID, STAR_NAME, STAR_POSITION)).willReturn(starSystem);
        given(starSystem.getId()).willReturn(STAR_SYSTEM_ID);
        given(minorFactionSaver.save(TIMESTAMP, factions)).willReturn(List.of(minorFaction));
        given(controllingFactionParser.parse(controllingFaction)).willReturn(controllingFaction);

        underTest.processMessage(edMessage);

        then(bodySaver).should().save(TIMESTAMP, STAR_SYSTEM_ID, BodyType.STAR, BODY_ID, BODY_NAME);
        then(starSystemDataSaver).should().save(
            STAR_SYSTEM_ID,
            TIMESTAMP,
            POPULATION,
            Allegiance.ALLIANCE,
            EconomyEnum.AGRICULTURE,
            EconomyEnum.COLONY,
            SecurityLevel.ANARCHY,
            Power.AISLING_DUVAL,
            PowerplayState.FORTIFIED,
            List.of(minorFaction),
            controllingFaction,
            List.of(Power.NAKATO_KAINE),
            conflicts
        );
    }

    @Test
    void processMessage_fsdJump_nullBodyIdentifiers() {
        given(edMessage.getMessage()).willReturn(MESSAGE);
        given(objectMapperWrapper.readTree(MESSAGE)).willReturn(jsonNode);
        given(jsonNode.get("event")).willReturn(jsonNode);
        given(jsonNode.asText()).willReturn("FSDJump");

        Faction[] factions = new Faction[]{faction};
        EdConflict[] conflicts = new EdConflict[]{edConflict};

        FsdJumpJournalMessage fsdJumpJournalMessage = new FsdJumpJournalMessage();
        fsdJumpJournalMessage.setTimestamp(TIMESTAMP);
        fsdJumpJournalMessage.setStarId(STAR_ID);
        fsdJumpJournalMessage.setStarName(STAR_NAME);
        fsdJumpJournalMessage.setStarPosition(STAR_POSITION);
        fsdJumpJournalMessage.setBodyId(null);
        fsdJumpJournalMessage.setBodyName(null);
        fsdJumpJournalMessage.setFactions(factions);
        fsdJumpJournalMessage.setPopulation(POPULATION);
        fsdJumpJournalMessage.setAllegiance(Allegiance.ALLIANCE.getValue());
        fsdJumpJournalMessage.setEconomy(EconomyEnum.AGRICULTURE.getValue());
        fsdJumpJournalMessage.setSecondEconomy(EconomyEnum.COLONY.getValue());
        fsdJumpJournalMessage.setSecurityLevel(SecurityLevel.ANARCHY.getValue());
        fsdJumpJournalMessage.setControllingPower(Power.AISLING_DUVAL.getValue());
        fsdJumpJournalMessage.setPowerplayState(PowerplayState.FORTIFIED.getValue());
        fsdJumpJournalMessage.setFactions(factions);
        fsdJumpJournalMessage.setControllingFaction(controllingFaction);
        fsdJumpJournalMessage.setPowers(new String[]{Power.NAKATO_KAINE.getValue()});
        fsdJumpJournalMessage.setConflicts(conflicts);
        doAnswer(invocation -> {
            invocation.getArgument(0, Runnable.class).run();
            return null;
        }).when(performanceReporter).wrap(any(Runnable.class), any(), any());

        given(objectMapperWrapper.readValue(MESSAGE, FsdJumpJournalMessage.class)).willReturn(fsdJumpJournalMessage);
        given(starSystemSaver.save(TIMESTAMP, STAR_ID, STAR_NAME, STAR_POSITION)).willReturn(starSystem);
        given(starSystem.getId()).willReturn(STAR_SYSTEM_ID);
        given(minorFactionSaver.save(TIMESTAMP, factions)).willReturn(List.of(minorFaction));
        given(controllingFactionParser.parse(controllingFaction)).willReturn(controllingFaction);

        underTest.processMessage(edMessage);

        then(bodySaver).shouldHaveNoInteractions();
        then(starSystemDataSaver).should().save(
            STAR_SYSTEM_ID,
            TIMESTAMP,
            POPULATION,
            Allegiance.ALLIANCE,
            EconomyEnum.AGRICULTURE,
            EconomyEnum.COLONY,
            SecurityLevel.ANARCHY,
            Power.AISLING_DUVAL,
            PowerplayState.FORTIFIED,
            List.of(minorFaction),
            controllingFaction,
            List.of(Power.NAKATO_KAINE),
            conflicts
        );
    }

    @Test
    void processMessage_docked() {
        given(edMessage.getMessage()).willReturn(MESSAGE);
        given(objectMapperWrapper.readTree(MESSAGE)).willReturn(jsonNode);
        given(jsonNode.get("event")).willReturn(jsonNode);
        given(jsonNode.asText()).willReturn("Docked");

        String[] services = new String[]{SERVICE};
        Economy[] economies = new Economy[]{economy};

        DockedJournalMessage dockedJournalMessage = new DockedJournalMessage();
        dockedJournalMessage.setTimestamp(TIMESTAMP);
        dockedJournalMessage.setStarId(STAR_ID);
        dockedJournalMessage.setStarName(STAR_NAME);
        dockedJournalMessage.setStarPosition(STAR_POSITION);
        dockedJournalMessage.setBodyId(BODY_ID);
        dockedJournalMessage.setBodyName(BODY_NAME);
        dockedJournalMessage.setAllegiance(Allegiance.ALLIANCE.getValue());
        dockedJournalMessage.setEconomy(EconomyEnum.AGRICULTURE.getValue());
        dockedJournalMessage.setControllingFaction(controllingFaction);
        dockedJournalMessage.setDistanceFromStar(DISTANCE_FROM_STAR);
        dockedJournalMessage.setStationType(StationType.SURFACE_STATION.getValue());
        dockedJournalMessage.setMarketId(MARKET_ID);
        dockedJournalMessage.setStationName(STATION_NAME);
        dockedJournalMessage.setStationServices(services);
        dockedJournalMessage.setEconomies(economies);
        dockedJournalMessage.setBodyType("Planet");

        given(objectMapperWrapper.readValue(MESSAGE, DockedJournalMessage.class)).willReturn(dockedJournalMessage);
        given(starSystemSaver.save(TIMESTAMP, STAR_ID, STAR_NAME, STAR_POSITION)).willReturn(starSystem);
        given(starSystem.getId()).willReturn(STAR_SYSTEM_ID);
        given(bodySaver.saveOptional(TIMESTAMP, STAR_SYSTEM_ID, BodyType.PLANET, BODY_ID, BODY_NAME, DISTANCE_FROM_STAR)).willReturn(Optional.of(body));
        given(body.getId()).willReturn(INTERNAL_BODY_ID);
        doAnswer(invocation -> {
            invocation.getArgument(0, Runnable.class).run();
            return null;
        }).when(performanceReporter).wrap(any(Runnable.class), any(), any());

        underTest.processMessage(edMessage);

        then(stationSaverUtil).should().saveStationOrFleetCarrier(
            TIMESTAMP,
            STAR_SYSTEM_ID,
            INTERNAL_BODY_ID,
            StationType.SURFACE_STATION.getValue(),
            MARKET_ID,
            STATION_NAME,
            Allegiance.ALLIANCE,
            EconomyEnum.AGRICULTURE,
            services,
            economies,
            null,
            controllingFaction
        );
    }

    @Test
    void processMessage_carrierJump() {
        given(edMessage.getMessage()).willReturn(MESSAGE);
        given(objectMapperWrapper.readTree(MESSAGE)).willReturn(jsonNode);
        given(jsonNode.get("event")).willReturn(jsonNode);
        given(jsonNode.asText()).willReturn("CarrierJump");

        Faction[] factions = new Faction[]{faction};
        EdConflict[] conflicts = new EdConflict[]{edConflict};

        CarrierJumpJournalMessage carrierJumpJournalMessage = new CarrierJumpJournalMessage();
        carrierJumpJournalMessage.setTimestamp(TIMESTAMP);
        carrierJumpJournalMessage.setStarId(STAR_ID);
        carrierJumpJournalMessage.setStarName(STAR_NAME);
        carrierJumpJournalMessage.setStarPosition(STAR_POSITION);
        carrierJumpJournalMessage.setBodyId(BODY_ID);
        carrierJumpJournalMessage.setBodyName(BODY_NAME);
        carrierJumpJournalMessage.setBodyType("Star");
        carrierJumpJournalMessage.setFactions(factions);
        carrierJumpJournalMessage.setPopulation(POPULATION);
        carrierJumpJournalMessage.setAllegiance(Allegiance.ALLIANCE.getValue());
        carrierJumpJournalMessage.setEconomy(EconomyEnum.AGRICULTURE.getValue());
        carrierJumpJournalMessage.setSecondEconomy(EconomyEnum.COLONY.getValue());
        carrierJumpJournalMessage.setSecurityLevel(SecurityLevel.ANARCHY.getValue());
        carrierJumpJournalMessage.setControllingPower(Power.AISLING_DUVAL.getValue());
        carrierJumpJournalMessage.setPowerplayState(PowerplayState.FORTIFIED.getValue());
        carrierJumpJournalMessage.setFactions(factions);
        carrierJumpJournalMessage.setControllingFaction(controllingFaction);
        carrierJumpJournalMessage.setPowers(new String[]{Power.NAKATO_KAINE.getValue()});
        carrierJumpJournalMessage.setConflicts(conflicts);

        given(objectMapperWrapper.readValue(MESSAGE, CarrierJumpJournalMessage.class)).willReturn(carrierJumpJournalMessage);
        given(starSystemSaver.save(TIMESTAMP, STAR_ID, STAR_NAME, STAR_POSITION)).willReturn(starSystem);
        given(starSystem.getId()).willReturn(STAR_SYSTEM_ID);
        given(minorFactionSaver.save(TIMESTAMP, factions)).willReturn(List.of(minorFaction));
        doAnswer(invocation -> {
            invocation.getArgument(0, Runnable.class).run();
            return null;
        }).when(performanceReporter).wrap(any(Runnable.class), any(), any());

        underTest.processMessage(edMessage);

        then(bodySaver).should().save(TIMESTAMP, STAR_SYSTEM_ID, BodyType.STAR, BODY_ID, BODY_NAME);
        then(starSystemDataSaver).should().save(
            STAR_SYSTEM_ID,
            TIMESTAMP,
            POPULATION,
            Allegiance.ALLIANCE,
            EconomyEnum.AGRICULTURE,
            EconomyEnum.COLONY,
            SecurityLevel.ANARCHY,
            Power.AISLING_DUVAL,
            PowerplayState.FORTIFIED,
            List.of(minorFaction),
            controllingFaction,
            List.of(Power.NAKATO_KAINE),
            conflicts
        );
    }

    @Test
    void processMessage_location() {
        given(edMessage.getMessage()).willReturn(MESSAGE);
        given(objectMapperWrapper.readTree(MESSAGE)).willReturn(jsonNode);
        given(jsonNode.get("event")).willReturn(jsonNode);
        given(jsonNode.asText()).willReturn("Location");

        Faction[] factions = new Faction[]{faction};
        EdConflict[] conflicts = new EdConflict[]{edConflict};

        LocationJournalMessage locationJournalMessage = new LocationJournalMessage();
        locationJournalMessage.setTimestamp(TIMESTAMP);
        locationJournalMessage.setStarId(STAR_ID);
        locationJournalMessage.setStarName(STAR_NAME);
        locationJournalMessage.setStarPosition(STAR_POSITION);
        locationJournalMessage.setBodyId(BODY_ID);
        locationJournalMessage.setBodyName(BODY_NAME);
        locationJournalMessage.setFactions(factions);
        locationJournalMessage.setPopulation(POPULATION);
        locationJournalMessage.setAllegiance(Allegiance.ALLIANCE.getValue());
        locationJournalMessage.setEconomy(EconomyEnum.AGRICULTURE.getValue());
        locationJournalMessage.setSecondEconomy(EconomyEnum.COLONY.getValue());
        locationJournalMessage.setSecurityLevel(SecurityLevel.ANARCHY.getValue());
        locationJournalMessage.setControllingPower(Power.AISLING_DUVAL.getValue());
        locationJournalMessage.setPowerplayState(PowerplayState.FORTIFIED.getValue());
        locationJournalMessage.setFactions(factions);
        locationJournalMessage.setControllingFaction(controllingFaction);
        locationJournalMessage.setPowers(new String[]{Power.NAKATO_KAINE.getValue()});
        locationJournalMessage.setConflicts(conflicts);
        locationJournalMessage.setBodyType("Star");
        locationJournalMessage.setDistanceFromStar(DISTANCE_FROM_STAR);

        given(objectMapperWrapper.readValue(MESSAGE, LocationJournalMessage.class)).willReturn(locationJournalMessage);
        given(starSystemSaver.save(TIMESTAMP, STAR_ID, STAR_NAME, STAR_POSITION)).willReturn(starSystem);
        given(starSystem.getId()).willReturn(STAR_SYSTEM_ID);
        given(minorFactionSaver.save(TIMESTAMP, factions)).willReturn(List.of(minorFaction));
        given(controllingFactionParser.parse(controllingFaction)).willReturn(controllingFaction);
        doAnswer(invocation -> {
            invocation.getArgument(0, Runnable.class).run();
            return null;
        }).when(performanceReporter).wrap(any(Runnable.class), any(), any());

        underTest.processMessage(edMessage);

        then(bodySaver).should().save(TIMESTAMP, STAR_SYSTEM_ID, BodyType.STAR, BODY_ID, BODY_NAME, DISTANCE_FROM_STAR);
        then(starSystemDataSaver).should().save(
            STAR_SYSTEM_ID,
            TIMESTAMP,
            POPULATION,
            Allegiance.ALLIANCE,
            EconomyEnum.AGRICULTURE,
            EconomyEnum.COLONY,
            SecurityLevel.ANARCHY,
            Power.AISLING_DUVAL,
            PowerplayState.FORTIFIED,
            List.of(minorFaction),
            controllingFaction,
            List.of(Power.NAKATO_KAINE),
            conflicts
        );
        then(starSystemDataSaver).should().save(
            STAR_SYSTEM_ID,
            TIMESTAMP,
            POPULATION,
            Allegiance.ALLIANCE,
            EconomyEnum.AGRICULTURE,
            EconomyEnum.COLONY,
            SecurityLevel.ANARCHY,
            Power.AISLING_DUVAL,
            PowerplayState.FORTIFIED,
            List.of(minorFaction),
            controllingFaction,
            List.of(Power.NAKATO_KAINE),
            conflicts
        );
    }

    @Test
    void saaSignalsFound() {
        given(edMessage.getMessage()).willReturn(MESSAGE);
        given(objectMapperWrapper.readTree(MESSAGE)).willReturn(jsonNode);
        given(jsonNode.get("event")).willReturn(jsonNode);
        given(jsonNode.asText()).willReturn("SAASignalsFound");

        SaaSignalFoundJournalMessage saaSignalFoundJournalMessage = new SaaSignalFoundJournalMessage();
        saaSignalFoundJournalMessage.setTimestamp(TIMESTAMP);
        saaSignalFoundJournalMessage.setStarId(STAR_ID);
        saaSignalFoundJournalMessage.setStarName(STAR_NAME);
        saaSignalFoundJournalMessage.setStarPosition(STAR_POSITION);

        given(objectMapperWrapper.readValue(MESSAGE, SaaSignalFoundJournalMessage.class)).willReturn(saaSignalFoundJournalMessage);
        doAnswer(invocation -> {
            invocation.getArgument(0, Runnable.class).run();
            return null;
        }).when(performanceReporter).wrap(any(Runnable.class), any(), any());

        underTest.processMessage(edMessage);

        then(starSystemSaver).should().save(TIMESTAMP, STAR_ID, STAR_NAME, STAR_POSITION);
    }
}