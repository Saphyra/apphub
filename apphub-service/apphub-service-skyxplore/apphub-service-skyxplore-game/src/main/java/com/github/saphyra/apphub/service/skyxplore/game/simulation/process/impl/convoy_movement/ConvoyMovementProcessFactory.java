package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.convoy_movement;

import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessStatus;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.lib.common_util.ApplicationContextProxy;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.ProcessFactory;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.ProcessParamKeys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class ConvoyMovementProcessFactory implements ProcessFactory {
    private final UuidConverter uuidConverter;
    private final ApplicationContextProxy applicationContextProxy;
    private final IdGenerator idGenerator;

    @Override
    public ProcessType getType() {
        return ProcessType.CONVOY_MOVEMENT;
    }

    @Override
    public ConvoyMovementProcess createFromModel(Game game, ProcessModel model) {
        return ConvoyMovementProcess.builder()
            .processId(model.getId())
            .status(model.getStatus())
            .citizenId(uuidConverter.convertEntity(model.getData().get(ProcessParamKeys.CITIZEN_ID)))
            .requiredWorkPoints(Integer.parseInt(model.getData().get(ProcessParamKeys.REQUIRED_WORK_POINTS)))
            .completedWorkPoints(Integer.parseInt(model.getData().get(ProcessParamKeys.COMPLETED_WORK_POINTS)))
            .externalReference(model.getExternalReference())
            .gameData(game.getData())
            .location(model.getLocation())
            .applicationContextProxy(applicationContextProxy)
            .game(game)
            .build();
    }

    public ConvoyMovementProcess save(Game game, UUID location, UUID externalReference, UUID citizenId, int requiredWorkPoints) {
        ConvoyMovementProcess process = ConvoyMovementProcess.builder()
            .processId(idGenerator.randomUuid())
            .status(ProcessStatus.CREATED)
            .citizenId(citizenId)
            .requiredWorkPoints(requiredWorkPoints)
            .completedWorkPoints(0)
            .externalReference(externalReference)
            .gameData(game.getData())
            .location(location)
            .applicationContextProxy(applicationContextProxy)
            .game(game)
            .build();

        game.getProgressDiff()
            .save(process.toModel());
        game.getData()
            .getProcesses()
            .add(process);

        log.info("Saved: {}", process);

        return process;
    }
}
