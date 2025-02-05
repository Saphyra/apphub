package com.github.saphyra.apphub.service.custom.elite_base.message_processing.processor;

import com.github.saphyra.apphub.api.admin_panel.model.model.performance_reporting.PerformanceReportingTopic;
import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import com.github.saphyra.apphub.lib.performance_reporting.PerformanceReporter;
import com.github.saphyra.apphub.service.custom.elite_base.common.PerformanceReportingKey;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.dao.body.Body;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.dao.body.BodyType;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.dao.body_data.ReserveLevel;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.dao.minor_faction.MinorFaction;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.dao.star_system.StarSystem;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.dao.star_system.StarType;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.dao.star_system_data.Power;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.dao.star_system_data.PowerplayState;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.structure.journal.message.CarrierJumpJournalMessage;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.structure.journal.message.DockedJournalMessage;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.structure.journal.message.FsdJumpJournalMessage;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.structure.journal.message.LocationJournalMessage;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.structure.journal.message.SaaSignalFoundJournalMessage;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.structure.journal.message.ScanJournalMessage;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.dao.Allegiance;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.dao.EconomyEnum;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.dao.SecurityLevel;
import com.github.saphyra.apphub.service.custom.elite_base.message_handling.dao.EdMessage;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.saver.BodyDataSaver;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.saver.BodySaver;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.saver.MinorFactionSaver;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.saver.StarSystemDataSaver;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.saver.StarSystemSaver;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.util.ControllingFactionParser;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.util.StationSaverUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Component
@RequiredArgsConstructor
@Slf4j
class JournalMessageProcessor implements MessageProcessor {
    private final ObjectMapperWrapper objectMapperWrapper;
    private final StarSystemSaver starSystemSaver;
    private final BodySaver bodySaver;
    private final BodyDataSaver bodyDataSaver;
    private final StarSystemDataSaver starSystemDataSaver;
    private final MinorFactionSaver minorFactionSaver;
    private final StationSaverUtil stationSaverUtil;
    private final ControllingFactionParser controllingFactionParser;
    private final PerformanceReporter performanceReporter;

    @Override
    public boolean canProcess(EdMessage message) {
        return SchemaRefs.JOURNAL.equals(message.getSchemaRef());
    }

    @Override
    public void processMessage(EdMessage message) {
        String event = objectMapperWrapper.readTree(message.getMessage())
            .get("event")
            .asText();

        performanceReporter.wrap(
            () -> {
                switch (event) {
                    case "Scan" -> {
                        ScanJournalMessage scanJournalMessage = objectMapperWrapper.readValue(message.getMessage(), ScanJournalMessage.class);
                        processScanJournalMessage(scanJournalMessage);
                    }
                    case "FSDJump" -> {
                        FsdJumpJournalMessage fsdJumpJournalMessage = objectMapperWrapper.readValue(message.getMessage(), FsdJumpJournalMessage.class);
                        processFsdJumpJournalMessage(fsdJumpJournalMessage);
                    }
                    case "Docked" -> {
                        DockedJournalMessage dockedJournalMessage = objectMapperWrapper.readValue(message.getMessage(), DockedJournalMessage.class);
                        processDockedJournalMessage(dockedJournalMessage);
                    }
                    case "CarrierJump" -> {
                        CarrierJumpJournalMessage carrierJumpJournalMessage = objectMapperWrapper.readValue(message.getMessage(), CarrierJumpJournalMessage.class);
                        processCarrierJumpJournalMessage(carrierJumpJournalMessage);
                    }
                    case "Location" -> {
                        LocationJournalMessage locationJournalMessage = objectMapperWrapper.readValue(message.getMessage(), LocationJournalMessage.class);
                        processLocationJournalMessage(locationJournalMessage);
                    }
                    case "SAASignalsFound" -> {
                        SaaSignalFoundJournalMessage saaSignalFoundJournalMessage = objectMapperWrapper.readValue(message.getMessage(), SaaSignalFoundJournalMessage.class);
                        processSaaSignalFoundJournalMessage(saaSignalFoundJournalMessage);
                    }
                    default -> throw new IllegalArgumentException("Unhandled event: " + event);
                }
            },
            PerformanceReportingTopic.ELITE_BASE_MESSAGE_PROCESSING,
            PerformanceReportingKey.PROCESS_JOURNAL_MESSAGE.formatted(event)
        );
    }

    private void processSaaSignalFoundJournalMessage(SaaSignalFoundJournalMessage message) {
        starSystemSaver.save(
            message.getTimestamp(),
            message.getStarId(),
            message.getStarName(),
            message.getStarPosition()
        );
    }

    private void processLocationJournalMessage(LocationJournalMessage message) {
        StarSystem starSystem = starSystemSaver.save(
            message.getTimestamp(),
            message.getStarId(),
            message.getStarName(),
            message.getStarPosition()
        );

        Body body = bodySaver.save(
            message.getTimestamp(),
            starSystem.getId(),
            BodyType.parse(message.getBodyType()),
            message.getBodyId(),
            message.getBodyName(),
            message.getDistanceFromStar()
        );

        List<MinorFaction> minorFactions = minorFactionSaver.save(message.getTimestamp(), message.getFactions());

        starSystemDataSaver.save(
            starSystem.getId(),
            message.getTimestamp(),
            message.getPopulation(),
            Allegiance.parse(message.getAllegiance()),
            EconomyEnum.parse(message.getEconomy()),
            EconomyEnum.parse(message.getSecondEconomy()),
            SecurityLevel.parse(message.getSecurityLevel()),
            Power.parse(message.getControllingPower()),
            PowerplayState.parse(message.getPowerplayState()),
            minorFactions,
            controllingFactionParser.parse(message.getControllingFaction()),
            Optional.ofNullable(message.getPowers()).map(strings -> Arrays.stream(strings).map(Power::parse).toList()).orElse(null),
            message.getConflicts()
        );

        if (!isNull(message.getMarketId())) {
            stationSaverUtil.saveStationOrFleetCarrier(
                message.getTimestamp(),
                starSystem.getId(),
                body.getId(),
                message.getStationType(),
                message.getMarketId(),
                message.getStationName(),
                Allegiance.parse(message.getAllegiance()),
                EconomyEnum.parse(message.getEconomy()),
                message.getStationServices(),
                message.getStationEconomies(),
                null,
                controllingFactionParser.parse(message.getControllingFaction())
            );
        }
    }

    private void processCarrierJumpJournalMessage(CarrierJumpJournalMessage message) {
        StarSystem starSystem = starSystemSaver.save(
            message.getTimestamp(),
            message.getStarId(),
            message.getStarName(),
            message.getStarPosition()
        );

        bodySaver.save(
            message.getTimestamp(),
            starSystem.getId(),
            BodyType.parse(message.getBodyType()),
            message.getBodyId(),
            message.getBodyName()
        );

        List<MinorFaction> minorFactions = minorFactionSaver.save(message.getTimestamp(), message.getFactions());

        starSystemDataSaver.save(
            starSystem.getId(),
            message.getTimestamp(),
            message.getPopulation(),
            Allegiance.parse(message.getAllegiance()),
            EconomyEnum.parse(message.getEconomy()),
            EconomyEnum.parse(message.getSecondEconomy()),
            SecurityLevel.parse(message.getSecurityLevel()),
            Power.parse(message.getControllingPower()),
            PowerplayState.parse(message.getPowerplayState()),
            minorFactions,
            message.getControllingFaction(),
            Optional.ofNullable(message.getPowers()).map(strings -> Arrays.stream(strings).map(Power::parse).toList()).orElse(null),
            message.getConflicts()
        );
    }

    private void processDockedJournalMessage(DockedJournalMessage message) {
        StarSystem starSystem = starSystemSaver.save(
            message.getTimestamp(),
            message.getStarId(),
            message.getStarName(),
            message.getStarPosition()
        );

        Optional<Body> body = bodySaver.saveOptional(
            message.getTimestamp(),
            starSystem.getId(),
            BodyType.parse(message.getBodyType()),
            message.getBodyId(),
            message.getBodyName(),
            message.getDistanceFromStar()
        );

        stationSaverUtil.saveStationOrFleetCarrier(
            message.getTimestamp(),
            starSystem.getId(),
            body.map(Body::getId).orElse(null),
            message.getStationType(),
            message.getMarketId(),
            message.getStationName(),
            Allegiance.parse(message.getAllegiance()),
            EconomyEnum.parse(message.getEconomy()),
            message.getStationServices(),
            message.getEconomies(),
            null,
            message.getControllingFaction()
        );
    }

    private void processFsdJumpJournalMessage(FsdJumpJournalMessage message) {
        StarSystem starSystem = starSystemSaver.save(
            message.getTimestamp(),
            message.getStarId(),
            message.getStarName(),
            message.getStarPosition()
        );

        if (nonNull(message.getBodyId()) || nonNull(message.getBodyName())) {
            bodySaver.save(
                message.getTimestamp(),
                starSystem.getId(),
                BodyType.STAR,
                message.getBodyId(),
                message.getBodyName()
            );
        }


        List<MinorFaction> minorFactions = minorFactionSaver.save(message.getTimestamp(), message.getFactions());

        starSystemDataSaver.save(
            starSystem.getId(),
            message.getTimestamp(),
            message.getPopulation(),
            Allegiance.parse(message.getAllegiance()),
            EconomyEnum.parse(message.getEconomy()),
            EconomyEnum.parse(message.getSecondEconomy()),
            SecurityLevel.parse(message.getSecurityLevel()),
            Power.parse(message.getControllingPower()),
            PowerplayState.parse(message.getPowerplayState()),
            minorFactions,
            controllingFactionParser.parse(message.getControllingFaction()),
            Optional.ofNullable(message.getPowers()).map(strings -> Arrays.stream(strings).map(Power::parse).toList()).orElse(null),
            message.getConflicts()
        );
    }

    private void processScanJournalMessage(ScanJournalMessage message) {
        StarSystem starSystem = starSystemSaver.save(
            message.getTimestamp(),
            message.getStarId(),
            message.getStarName(),
            message.getStarPosition(),
            StarType.parse(message.getStarType())
        );

        Body body = bodySaver.save(
            message.getTimestamp(),
            starSystem.getId(),
            BodyType.PLANET,
            message.getBodyId(),
            message.getBodyName(),
            message.getDistanceFromStar()
        );

        bodyDataSaver.save(
            body.getId(),
            message.getTimestamp(),
            message.getLandable(),
            message.getSurfaceGravity(),
            ReserveLevel.parse(message.getReserveLevel()),
            nonNull(message.getRings()) && message.getRings().length > 0,
            message.getMaterials(),
            message.getRings()
        );
    }
}
