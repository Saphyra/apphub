package com.github.saphyra.apphub.service.elite_base.message_processing.processor;

import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import com.github.saphyra.apphub.service.elite_base.dao.Allegiance;
import com.github.saphyra.apphub.service.elite_base.dao.EconomyEnum;
import com.github.saphyra.apphub.service.elite_base.dao.SecurityLevel;
import com.github.saphyra.apphub.service.elite_base.dao.body.Body;
import com.github.saphyra.apphub.service.elite_base.dao.body.BodyType;
import com.github.saphyra.apphub.service.elite_base.dao.body_data.ReserveLevel;
import com.github.saphyra.apphub.service.elite_base.dao.minor_faction.MinorFaction;
import com.github.saphyra.apphub.service.elite_base.dao.star_system.StarSystem;
import com.github.saphyra.apphub.service.elite_base.dao.star_system_data.Power;
import com.github.saphyra.apphub.service.elite_base.dao.star_system_data.PowerplayState;
import com.github.saphyra.apphub.service.elite_base.message_handling.dao.EdMessage;
import com.github.saphyra.apphub.service.elite_base.message_processing.saver.BodyDataSaver;
import com.github.saphyra.apphub.service.elite_base.message_processing.saver.BodySaver;
import com.github.saphyra.apphub.service.elite_base.message_processing.saver.MinorFactionSaver;
import com.github.saphyra.apphub.service.elite_base.message_processing.saver.StarSystemDataSaver;
import com.github.saphyra.apphub.service.elite_base.message_processing.saver.StarSystemSaver;
import com.github.saphyra.apphub.service.elite_base.message_processing.structure.journal.message.CarrierJumpJournalMessage;
import com.github.saphyra.apphub.service.elite_base.message_processing.structure.journal.message.DockedJournalMessage;
import com.github.saphyra.apphub.service.elite_base.message_processing.structure.journal.message.FsdJumpJournalMessage;
import com.github.saphyra.apphub.service.elite_base.message_processing.structure.journal.message.LocationJournalMessage;
import com.github.saphyra.apphub.service.elite_base.message_processing.structure.journal.message.SaaSignalFoundJournalMessage;
import com.github.saphyra.apphub.service.elite_base.message_processing.structure.journal.message.ScanJournalMessage;
import com.github.saphyra.apphub.service.elite_base.message_processing.util.StationSaverUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static java.util.Objects.nonNull;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class JournalMessageProcessor implements MessageProcessor {
    private final ObjectMapperWrapper objectMapperWrapper;
    private final StarSystemSaver starSystemSaver;
    private final BodySaver bodySaver;
    private final BodyDataSaver bodyDataSaver;
    private final StarSystemDataSaver starSystemDataSaver;
    private final MinorFactionSaver minorFactionSaver;
    private final StationSaverUtil stationSaverUtil;

    @Override
    public boolean canProcess(EdMessage message) {
        return SchemaRefs.JOURNAL.equals(message.getSchemaRef());
    }

    @Override
    public void processMessage(EdMessage message) {
        String event = objectMapperWrapper.readTree(message.getMessage())
            .get("event")
            .asText();

        switch (event) {
            case "Scan":
                ScanJournalMessage scanJournalMessage = objectMapperWrapper.readValue(message.getMessage(), ScanJournalMessage.class);
                processScanJournalMessage(scanJournalMessage);
                break;
            case "FSDJump":
                FsdJumpJournalMessage fsdJumpJournalMessage = objectMapperWrapper.readValue(message.getMessage(), FsdJumpJournalMessage.class);
                processFsdJumpJournalMessage(fsdJumpJournalMessage);
                break;
            case "Docked":
                DockedJournalMessage dockedJournalMessage = objectMapperWrapper.readValue(message.getMessage(), DockedJournalMessage.class);
                processDockedJournalMessage(dockedJournalMessage);
                break;
            case "CarrierJump":
                CarrierJumpJournalMessage carrierJumpJournalMessage = objectMapperWrapper.readValue(message.getMessage(), CarrierJumpJournalMessage.class);
                break;
            case "Location":
                LocationJournalMessage locationJournalMessage = objectMapperWrapper.readValue(message.getMessage(), LocationJournalMessage.class);
                break;
            case "SAASignalsFound":
                SaaSignalFoundJournalMessage saaSignalFoundJournalMessage = objectMapperWrapper.readValue(message.getMessage(), SaaSignalFoundJournalMessage.class);
                break;
            default:
                throw new RuntimeException("Unhandled event: " + event);
        }
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

        bodySaver.save(
            message.getTimestamp(),
            starSystem.getId(),
            BodyType.STAR,
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

    private void processScanJournalMessage(ScanJournalMessage message) {
        StarSystem starSystem = starSystemSaver.save(
            message.getTimestamp(),
            message.getStarId(),
            message.getStarName(),
            message.getStarPosition(),
            message.getStarType()
        );

        Body body = bodySaver.save(
            message.getTimestamp(),
            starSystem.getId(),
            BodyType.WORLD,
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
