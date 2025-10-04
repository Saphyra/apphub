package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.deconstruct_building_module;

import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessStatus;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.lib.common_util.ApplicationContextProxy;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.deconstruction.Deconstruction;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.ProcessFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeconstructBuildingModuleProcessFactory implements ProcessFactory {
    private final ApplicationContextProxy applicationContextProxy;
    private final IdGenerator idGenerator;

    public DeconstructBuildingModuleProcess create(Game game, Deconstruction deconstruction) {
        return DeconstructBuildingModuleProcess.builder()
            .processId(idGenerator.randomUuid())
            .deconstructionId(deconstruction.getDeconstructionId())
            .status(ProcessStatus.CREATED)
            .location(deconstruction.getLocation())
            .game(game)
            .applicationContextProxy(applicationContextProxy)
            .build();
    }

    @Override
    public ProcessType getType() {
        return ProcessType.DECONSTRUCT_BUILDING_MODULE;
    }

    @Override
    public DeconstructBuildingModuleProcess createFromModel(Game game, ProcessModel model) {
        return DeconstructBuildingModuleProcess.builder()
            .processId(model.getId())
            .deconstructionId(model.getExternalReference())
            .status(model.getStatus())
            .location(model.getLocation())
            .game(game)
            .applicationContextProxy(applicationContextProxy)
            .build();
    }
}
