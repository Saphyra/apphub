package com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.construction_area.building_module;

import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.GameProgressDiff;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building_module.BuildingModule;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.deconstruction.Deconstruction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.deconstruction.DeconstructionConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.deconstruction.DeconstructionFactory;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.deconstruct_building_module.DeconstructBuildingModuleProcess;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.deconstruct_building_module.DeconstructBuildingModuleProcessFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class DeconstructBuildingModuleService {
    private final GameDao gameDao;
    private final DeconstructionFactory deconstructionFactory;
    private final DeconstructionConverter deconstructionConverter;
    private final DeconstructBuildingModuleProcessFactory deconstructBuildingModuleProcessFactory;

    UUID deconstructBuildingModule(UUID userId, UUID buildingModuleId) {
        Game game = gameDao.findByUserIdValidated(userId);
        GameData gameData = game.getData();

        BuildingModule buildingModule = gameData.getBuildingModules()
            .findByBuildingModuleIdValidated(buildingModuleId);
        UUID planetId = buildingModule.getLocation();

        if (!userId.equals(game.getData().getPlanets().findByIdValidated(planetId).getOwner())) {
            throw ExceptionFactory.forbiddenOperation(userId + " cannot deconstruct buildingModule on planet " + planetId);
        }

        if (game.getData().getConstructions().findByExternalReference(buildingModuleId).isPresent()) {
            throw ExceptionFactory.forbiddenOperation("BuildingModule " + buildingModuleId + " is under construction");
        }

        if (gameData.getDeconstructions().findByExternalReference(buildingModuleId).isPresent()) {
            throw ExceptionFactory.forbiddenOperation("BuildingModule " + buildingModuleId + " is already being deconstructed");
        }

        game.getEventLoop()
            .processWithWait(() -> deconstructBuildingModule(game, planetId, buildingModuleId))
            .getOrThrow();

        return buildingModule.getConstructionAreaId();
    }

    public void deconstructBuildingModule(Game game, UUID planetId, UUID buildingModuleId) {
        GameProgressDiff progressDiff = game.getProgressDiff();
        GameData gameData = game.getData();

        Deconstruction deconstruction = deconstructionFactory.create(buildingModuleId, planetId);

        gameData.getDeconstructions()
            .add(deconstruction);
        progressDiff.save(deconstructionConverter.toModel(gameData.getGameId(), deconstruction));

        DeconstructBuildingModuleProcess process = deconstructBuildingModuleProcessFactory.create(game, deconstruction);
        gameData.getProcesses()
            .add(process);
        progressDiff.save(process.toModel());
    }
}
