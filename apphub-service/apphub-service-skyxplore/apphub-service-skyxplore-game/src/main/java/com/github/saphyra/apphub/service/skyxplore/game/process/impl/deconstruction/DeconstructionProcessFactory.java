package com.github.saphyra.apphub.service.skyxplore.game.process.impl.deconstruction;

import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.skyxplore.game.common.ApplicationContextProxy;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Deconstruction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.process.ProcessFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

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
        Planet planet = game.getUniverse()
            .findPlanetByIdValidated(model.getLocation());

        Deconstruction deconstruction = planet.findBuildingByDeconstructionIdValidated(model.getExternalReference())
            .getDeconstruction();

        return DeconstructionProcess.builder()
            .processId(model.getId())
            .status(model.getStatus())
            .deconstruction(deconstruction)
            .game(game)
            .planet(planet)
            .applicationContextProxy(applicationContextProxy)
            .build();
    }

    public DeconstructionProcess create(Game game, Planet planet, Deconstruction deconstruction) {
        return DeconstructionProcess.builder()
            .processId(idGenerator.randomUuid())
            .deconstruction(deconstruction)
            .game(game)
            .planet(planet)
            .applicationContextProxy(applicationContextProxy)
            .build();
    }
}
