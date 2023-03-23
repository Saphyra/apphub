package com.github.saphyra.apphub.service.skyxplore.game.process.impl.deconstruction;

import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.skyxplore.game.common.ApplicationContextProxy;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.deconstruction.Deconstruction;
import com.github.saphyra.apphub.service.skyxplore.game.process.ProcessFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeconstructionProcessFactory implements ProcessFactory {
    private final IdGenerator idGenerator;
    private final ApplicationContextProxy applicationContextProxy;

    @Override
    public ProcessType getType() {
        return ProcessType.DECONSTRUCTION;
    }

    @Override
    public DeconstructionProcess createFromModel(Game game, ProcessModel model) {
        Deconstruction deconstruction = game.getData()
            .getDeconstructions()
            .findByDeconstructionId(model.getExternalReference());

        return DeconstructionProcess.builder()
            .processId(model.getId())
            .status(model.getStatus())
            .deconstruction(deconstruction)
            .gameData(game.getData())
            .location(model.getLocation())
            .applicationContextProxy(applicationContextProxy)
            .build();
    }

    public DeconstructionProcess create(GameData gameData, UUID location, Deconstruction deconstruction) {
        return DeconstructionProcess.builder()
            .processId(idGenerator.randomUuid())
            .deconstruction(deconstruction)
            .gameData(gameData)
            .location(location)
            .applicationContextProxy(applicationContextProxy)
            .build();
    }
}
