package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.production_dispatcher;

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
public class ProductionDispatcherProcessFactory implements ProcessFactory {
    private final UuidConverter uuidConverter;
    private final IdGenerator idGenerator;
    private final ApplicationContextProxy applicationContextProxy;

    @Override
    public ProcessType getType() {
        return ProcessType.PRODUCTION_DISPATCHER;
    }

    @Override
    public ProductionDispatcherProcess createFromModel(Game game, ProcessModel model) {
        return ProductionDispatcherProcess.builder()
            .processId(model.getId())
            .status(model.getStatus())
            .productionRequestId(uuidConverter.convertEntity(model.getData().get(ProcessParamKeys.PRODUCTION_REQUEST_ID)))
            .externalReference(model.getExternalReference())
            .game(game)
            .location(model.getLocation())
            .applicationContextProxy(applicationContextProxy)
            .build();
    }

    public ProductionDispatcherProcess save(Game game, UUID location, UUID externalReference, UUID productionRequestId) {
        ProductionDispatcherProcess process = ProductionDispatcherProcess.builder()
            .processId(idGenerator.randomUuid())
            .status(ProcessStatus.CREATED)
            .productionRequestId(productionRequestId)
            .externalReference(externalReference)
            .game(game)
            .location(location)
            .applicationContextProxy(applicationContextProxy)
            .build();

        game.getData()
            .getProcesses()
            .add(process);
        game.getProgressDiff()
            .save(process.toModel());

        return process;
    }
}
