package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.convoy;

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
public class ConvoyProcessFactory implements ProcessFactory {
    private final IdGenerator idGenerator;
    private final UuidConverter uuidConverter;
    private final ApplicationContextProxy applicationContextProxy;

    @Override
    public ProcessType getType() {
        return ProcessType.CONVOY;
    }

    @Override
    public ConvoyProcess createFromModel(Game game, ProcessModel model) {
        return ConvoyProcess.builder()
            .processId(model.getId())
            .status(model.getStatus())
            .convoyId(uuidConverter.convertEntity(model.getData().get(ProcessParamKeys.CONVOY_ID)))
            .externalReference(model.getExternalReference())
            .location(model.getLocation())
            .applicationContextProxy(applicationContextProxy)
            .game(game)
            .build();
    }

    public ConvoyProcess save(Game game, UUID location, UUID externalReference, UUID convoyId) {
        ConvoyProcess process = ConvoyProcess.builder()
            .processId(idGenerator.randomUuid())
            .status(ProcessStatus.CREATED)
            .convoyId(convoyId)
            .externalReference(externalReference)
            .location(location)
            .applicationContextProxy(applicationContextProxy)
            .game(game)
            .build();

        game.getProgressDiff()
            .save(process.toModel());
        game.getData()
            .getProcesses()
            .add(process);

        return process;
    }
}
