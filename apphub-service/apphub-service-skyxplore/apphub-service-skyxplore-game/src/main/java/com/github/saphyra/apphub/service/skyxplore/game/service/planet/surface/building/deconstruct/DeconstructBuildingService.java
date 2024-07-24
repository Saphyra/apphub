package com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.building.deconstruct;

import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.GameProgressDiff;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building.Building;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.deconstruction.Deconstruction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.deconstruction.DeconstructionConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.deconstruction.DeconstructionFactory;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.deconstruction.DeconstructionProcess;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.deconstruction.DeconstructionProcessFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeconstructBuildingService {
    private final GameDao gameDao;
    private final DeconstructionFactory deconstructionFactory;
    private final DeconstructionProcessFactory deconstructionProcessFactory;
    private final DeconstructionPreconditions deconstructionPreconditions;
    private final DeconstructionConverter deconstructionConverter;

    public void deconstructBuilding(UUID userId, UUID planetId, UUID buildingId) {
        Game game = gameDao.findByUserIdValidated(userId);

        if (!userId.equals(game.getData().getPlanets().findByIdValidated(planetId).getOwner())) {
            throw ExceptionFactory.forbiddenOperation(userId + " cannot deconstruct building on planet " + planetId);
        }

        if (game.getData().getConstructions().findByExternalReference(buildingId).isPresent()) {
            throw ExceptionFactory.forbiddenOperation(buildingId + " is under construction");
        }

        Building building = game.getData()
            .getBuildings()
            .findByBuildingId(buildingId);

        deconstructionPreconditions.checkIfBuildingCanBeDeconstructed(game.getData(), building);

        Deconstruction deconstruction = deconstructionFactory.create(buildingId, planetId);

        game.getEventLoop()
            .processWithWait(() -> {
                game.getData()
                    .getDeconstructions()
                    .add(deconstruction);

                DeconstructionProcess process = deconstructionProcessFactory.create(game, planetId, deconstruction.getDeconstructionId());

                GameProgressDiff progressDiff = game.getProgressDiff();
                progressDiff.save(process.toModel());
                progressDiff.save(deconstructionConverter.toModel(game.getGameId(), deconstruction));

                game.getData()
                    .getProcesses()
                    .add(process);
            })
            .getOrThrow();
    }
}
