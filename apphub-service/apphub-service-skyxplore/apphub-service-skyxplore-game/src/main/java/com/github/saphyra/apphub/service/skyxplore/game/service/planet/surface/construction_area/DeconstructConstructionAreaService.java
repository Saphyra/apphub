package com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.construction_area;

import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.GameProgressDiff;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.deconstruction.Deconstruction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.deconstruction.DeconstructionConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.deconstruction.DeconstructionFactory;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.deconstruct_construction_area.DeconstructConstructionAreaProcess;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.deconstruct_construction_area.DeconstructConstructionAreaProcessFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class DeconstructConstructionAreaService {
    private final GameDao gameDao;
    private final DeconstructionFactory deconstructionFactory;
    private final DeconstructionConverter deconstructionConverter;
    private final DeconstructConstructionAreaProcessFactory deconstructConstructionAreaProcessFactory;

    public void deconstructConstructionArea(UUID userId, UUID constructionAreaId) {
        Game game = gameDao.findByUserIdValidated(userId);
        GameData gameData = game.getData();

        UUID planetId = gameData.getConstructionAreas()
            .findByConstructionAreaIdValidated(constructionAreaId)
            .getLocation();

        if (!userId.equals(game.getData().getPlanets().findByIdValidated(planetId).getOwner())) {
            throw ExceptionFactory.forbiddenOperation(userId + " cannot deconstruct constructionArea on planet " + planetId);
        }

        if (game.getData().getConstructions().findByExternalReference(constructionAreaId).isPresent()) {
            throw ExceptionFactory.forbiddenOperation("ConstructionArea " + constructionAreaId + " is under construction");
        }

        if (gameData.getDeconstructions().findByExternalReference(constructionAreaId).isPresent()) {
            throw ExceptionFactory.forbiddenOperation("ConstructionArea " + constructionAreaId + " is already being deconstructed");
        }

        Deconstruction deconstruction = deconstructionFactory.create(constructionAreaId, planetId);

        game.getEventLoop()
            .processWithWait(() -> {
                GameProgressDiff progressDiff = game.getProgressDiff();

                gameData.getDeconstructions()
                    .add(deconstruction);
                progressDiff.save(deconstructionConverter.toModel(gameData.getGameId(), deconstruction));

                DeconstructConstructionAreaProcess process = deconstructConstructionAreaProcessFactory.create(game, deconstruction);
                gameData.getProcesses()
                    .add(process);
                progressDiff.save(process.toModel());
            })
            .getOrThrow();
    }
}
