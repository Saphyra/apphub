package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.resource_delivery;

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
public class ResourceDeliveryProcessFactory implements ProcessFactory {
    private final UuidConverter uuidConverter;
    private final ApplicationContextProxy applicationContextProxy;
    private final IdGenerator idGenerator;

    @Override
    public ProcessType getType() {
        return ProcessType.RESOURCE_DELIVERY;
    }

    @Override
    public ResourceDeliveryProcess createFromModel(Game game, ProcessModel model) {
        return ResourceDeliveryProcess.builder()
            .processId(model.getId())
            .status(model.getStatus())
            .resourceDeliveryRequestId(uuidConverter.convertEntity(model.getData().get(ProcessParamKeys.RESOURCE_DELIVERY_REQUEST_ID)))
            .externalReference(model.getExternalReference())
            .gameData(game.getData())
            .location(model.getLocation())
            .applicationContextProxy(applicationContextProxy)
            .game(game)
            .build();
    }

    public ResourceDeliveryProcess save(Game game, UUID location, UUID externalReference, UUID resourceDeliveryRequestId) {
        ResourceDeliveryProcess process = ResourceDeliveryProcess.builder()
            .processId(idGenerator.randomUuid())
            .status(ProcessStatus.CREATED)
            .resourceDeliveryRequestId(resourceDeliveryRequestId)
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

        return process;
    }
}
