package com.github.saphyra.apphub.service.custom.elite_base.message_processing.processor;

import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.dao.body.Body;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.dao.body.BodyType;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.dao.star_system.StarSystem;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.structure.approach_settlement.ApproachSettlementMessage;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.dao.Allegiance;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.dao.FactionStateEnum;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.dao.EconomyEnum;
import com.github.saphyra.apphub.service.custom.elite_base.message_handling.dao.EdMessage;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.saver.BodySaver;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.saver.MinorFactionSaver;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.saver.SettlementSaver;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.saver.StarSystemSaver;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.saver.StationSaver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static java.util.Objects.nonNull;

@Component
@RequiredArgsConstructor
@Slf4j
class ApproachSettlementMessageProcessor implements MessageProcessor {
    private final ObjectMapperWrapper objectMapperWrapper;
    private final StarSystemSaver starSystemSaver;
    private final BodySaver bodySaver;
    private final MinorFactionSaver minorFactionSaver;
    private final StationSaver stationSaver;
    private final SettlementSaver settlementSaver;

    @Override
    public boolean canProcess(EdMessage message) {
        return SchemaRefs.APPROACH_SETTLEMENT.equals(message.getSchemaRef());
    }

    @Override
    public void processMessage(EdMessage message) {
        ApproachSettlementMessage approachSettlementMessage = objectMapperWrapper.readValue(message.getMessage(), ApproachSettlementMessage.class);

        StarSystem starSystem = starSystemSaver.save(
            approachSettlementMessage.getTimestamp(),
            approachSettlementMessage.getStarId(),
            approachSettlementMessage.getStarName(),
            approachSettlementMessage.getStarPosition()
        );

        Body body = bodySaver.save(
            approachSettlementMessage.getTimestamp(),
            starSystem.getId(),
            BodyType.PLANET,
            approachSettlementMessage.getBodyId(),
            approachSettlementMessage.getBodyName()
        );

        Optional.ofNullable(approachSettlementMessage.getControllingFaction())
            .ifPresent(controllingFaction -> minorFactionSaver.save(
                approachSettlementMessage.getTimestamp(),
                approachSettlementMessage.getControllingFaction().getFactionName(),
                FactionStateEnum.parse(approachSettlementMessage.getControllingFaction().getState())
            ));

        if (nonNull(approachSettlementMessage.getServices())) {
            stationSaver.save(
                approachSettlementMessage.getTimestamp(),
                starSystem.getId(),
                body.getId(),
                approachSettlementMessage.getSettlementName(),
                approachSettlementMessage.getMarketId(),
                Allegiance.parse(approachSettlementMessage.getAllegiance()),
                EconomyEnum.parse(approachSettlementMessage.getEconomy()),
                approachSettlementMessage.getServices(),
                approachSettlementMessage.getEconomies()
            );
        }

        settlementSaver.save(
            approachSettlementMessage.getTimestamp(),
            starSystem.getId(),
            body.getId(),
            approachSettlementMessage.getSettlementName(),
            approachSettlementMessage.getMarketId(),
            approachSettlementMessage.getLongitude(),
            approachSettlementMessage.getLatitude()
        );
    }
}
