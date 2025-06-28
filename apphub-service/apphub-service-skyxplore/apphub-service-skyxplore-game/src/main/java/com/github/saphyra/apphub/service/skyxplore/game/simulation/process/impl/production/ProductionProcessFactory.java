package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.production;

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
public class ProductionProcessFactory implements ProcessFactory {
    private final IdGenerator idGenerator;
    private final UuidConverter uuidConverter;
    private final ApplicationContextProxy applicationContextProxy;

    @Override
    public ProcessType getType() {
        return ProcessType.PRODUCTION;
    }

    @Override
    public ProductionProcess createFromModel(Game game, ProcessModel model) {
        return ProductionProcess.builder()
            .processId(model.getId())
            .status(model.getStatus())
            .location(model.getLocation())
            .externalReference(model.getExternalReference())
            .productionOrderId(uuidConverter.convertEntity(model.getData().get(ProcessParamKeys.PRODUCTION_ORDER_ID)))
            .buildingModuleId(uuidConverter.convertEntity(model.getData().get(ProcessParamKeys.BUILDING_MODULE_ID)))
            .amount(Integer.parseInt(model.getData().get(ProcessParamKeys.AMOUNT)))
            .applicationContextProxy(applicationContextProxy)
            .game(game)
            .build();
    }

    public ProductionProcess save(Game game, UUID location, UUID externalReference, UUID productionOrderId, UUID buildingModuleId, int amount) {
        ProductionProcess process = ProductionProcess.builder()
            .processId(idGenerator.randomUuid())
            .status(ProcessStatus.CREATED)
            .location(location)
            .externalReference(externalReference)
            .productionOrderId(productionOrderId)
            .buildingModuleId(buildingModuleId)
            .amount(amount)
            .applicationContextProxy(applicationContextProxy)
            .game(game)
            .build();

        game.getData()
            .getProcesses()
            .add(process);
        game.getProgressDiff()
            .save(process.toModel());

        return process;
    }
}
