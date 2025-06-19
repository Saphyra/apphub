package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.resource_request;

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
//TODO unit test
public class ResourceRequestProcessFactory implements ProcessFactory {
    private final IdGenerator idGenerator;
    private final ApplicationContextProxy applicationContextProxy;
    private final UuidConverter uuidConverter;

    @Override
    public ProcessType getType() {
        return ProcessType.RESOURCE_REQUEST;
    }

    @Override
    public ResourceRequestProcess createFromModel(Game game, ProcessModel model) {
        return ResourceRequestProcess.builder()
            .processId(model.getId())
            .status(model.getStatus())
            .location(model.getLocation())
            .externalReference(model.getExternalReference())
            .reservedStorageId(uuidConverter.convertEntity(model.getData().get(ProcessParamKeys.RESERVED_STORAGE_ID)))
            .game(game)
            .applicationContextProxy(applicationContextProxy)
            .build();
    }

    public ResourceRequestProcess save(Game game, UUID location, UUID externalReference, UUID reservedStorageId) {
        ResourceRequestProcess process = ResourceRequestProcess.builder()
            .processId(idGenerator.randomUuid())
            .status(ProcessStatus.CREATED)
            .location(location)
            .externalReference(externalReference)
            .reservedStorageId(reservedStorageId)
            .game(game)
            .applicationContextProxy(applicationContextProxy)
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
