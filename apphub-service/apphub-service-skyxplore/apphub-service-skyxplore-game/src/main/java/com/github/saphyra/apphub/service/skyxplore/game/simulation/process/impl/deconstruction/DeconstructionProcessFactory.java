package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.deconstruction;

import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.lib.common_util.ApplicationContextProxy;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.ProcessFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
@Deprecated(forRemoval = true)
public class DeconstructionProcessFactory implements ProcessFactory {
    private final IdGenerator idGenerator;
    private final ApplicationContextProxy applicationContextProxy;

    @Override
    public ProcessType getType() {
        return ProcessType.DECONSTRUCTION;
    }

    @Override
    public DeconstructionProcess createFromModel(Game game, ProcessModel model) {
        return DeconstructionProcess.builder()
            .processId(model.getId())
            .status(model.getStatus())
            .deconstructionId(model.getExternalReference())
            .gameData(game.getData())
            .location(model.getLocation())
            .applicationContextProxy(applicationContextProxy)
            .game(game)
            .build();
    }

    public DeconstructionProcess create(Game game, UUID location, UUID deconstructionId) {
        return DeconstructionProcess.builder()
            .processId(idGenerator.randomUuid())
            .deconstructionId(deconstructionId)
            .gameData(game.getData())
            .game(game)
            .location(location)
            .applicationContextProxy(applicationContextProxy)
            .build();
    }
}
