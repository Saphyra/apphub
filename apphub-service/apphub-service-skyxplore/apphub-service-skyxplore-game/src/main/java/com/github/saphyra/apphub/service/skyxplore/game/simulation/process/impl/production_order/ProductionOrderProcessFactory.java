package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.production_order;

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
public class ProductionOrderProcessFactory implements ProcessFactory {
    private final IdGenerator idGenerator;
    private final UuidConverter uuidConverter;
    private final ApplicationContextProxy applicationContextProxy;

    @Override
    public ProcessType getType() {
        return ProcessType.PRODUCTION_ORDER;
    }

    @Override
    public ProductionOrderProcess createFromModel(Game game, ProcessModel model) {
        return ProductionOrderProcess.builder()
            .processId(model.getId())
            .status(model.getStatus())
            .productionOrderId(uuidConverter.convertEntity(model.getData().get(ProcessParamKeys.PRODUCTION_ORDER_ID)))
            .externalReference(model.getExternalReference())
            .gameData(game.getData())
            .location(model.getLocation())
            .applicationContextProxy(applicationContextProxy)
            .game(game)
            .build();
    }

    public ProductionOrderProcess save(Game game, UUID location, UUID externalReference, UUID productionOrderId) {
        ProductionOrderProcess process = ProductionOrderProcess.builder()
            .processId(idGenerator.randomUuid())
            .status(ProcessStatus.CREATED)
            .productionOrderId(productionOrderId)
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
