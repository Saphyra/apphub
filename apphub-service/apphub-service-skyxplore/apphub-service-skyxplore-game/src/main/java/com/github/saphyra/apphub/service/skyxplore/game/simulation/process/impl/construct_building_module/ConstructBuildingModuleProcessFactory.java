package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.construct_building_module;

import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessStatus;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.lib.common_util.ApplicationContextProxy;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Construction;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.ProcessFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ConstructBuildingModuleProcessFactory implements ProcessFactory {
    private final IdGenerator idGenerator;
    private final ApplicationContextProxy applicationContextProxy;

    @Override
    public ProcessType getType() {
        return ProcessType.CONSTRUCT_BUILDING_MODULE;
    }

    @Override
    public ConstructBuildingModuleProcess createFromModel(Game game, ProcessModel model) {
        return ConstructBuildingModuleProcess.builder()
            .processId(model.getId())
            .constructionId(model.getExternalReference())
            .status(model.getStatus())
            .location(model.getLocation())
            .game(game)
            .gameData(game.getData())
            .applicationContextProxy(applicationContextProxy)
            .build();
    }

    public ConstructBuildingModuleProcess create(Game game, Construction construction) {
        return ConstructBuildingModuleProcess.builder()
            .processId(idGenerator.randomUuid())
            .constructionId(construction.getConstructionId())
            .status(ProcessStatus.CREATED)
            .location(construction.getLocation())
            .game(game)
            .gameData(game.getData())
            .applicationContextProxy(applicationContextProxy)
            .build();
    }
}
