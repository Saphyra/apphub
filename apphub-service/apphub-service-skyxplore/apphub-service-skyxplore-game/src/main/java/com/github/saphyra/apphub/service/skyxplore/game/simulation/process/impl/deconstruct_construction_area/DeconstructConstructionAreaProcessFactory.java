package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.deconstruct_construction_area;

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
public class DeconstructConstructionAreaProcessFactory implements ProcessFactory {
    private final IdGenerator idGenerator;
    private final ApplicationContextProxy applicationContextProxy;

    @Override
    public ProcessType getType() {
        return ProcessType.DECONSTRUCT_CONSTRUCTION_AREA;
    }

    @Override
    public DeconstructConstructionAreaProcess createFromModel(Game game, ProcessModel model) {
        return DeconstructConstructionAreaProcess.builder()
            .processId(model.getId())
            .deconstructionId(model.getExternalReference())
            .status(model.getStatus())
            .location(model.getLocation())
            .game(game)
            .applicationContextProxy(applicationContextProxy)
            .build();
    }

    public DeconstructConstructionAreaProcess create(Game game, Deconstruction deconstruction) {
        return DeconstructConstructionAreaProcess.builder()
            .processId(idGenerator.randomUuid())
            .deconstructionId(deconstruction.getDeconstructionId())
            .status(ProcessStatus.CREATED)
            .location(deconstruction.getLocation())
            .game(game)
            .applicationContextProxy(applicationContextProxy)
            .build();
    }
}
