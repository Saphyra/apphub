package com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.construction_area.building_module;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building_module.BuildingModule;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Construction;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.AllocationRemovalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class CancelConstructionOfBuildingModuleService {
    private final GameDao gameDao;
    private final AllocationRemovalService allocationRemovalService;

    public void cancelConstructionOfConstructionAreaBuildingModules(Game game, UUID constructionAreaId) {
        GameData gameData = game.getData();

        gameData.getBuildingModules()
            .getByConstructionAreaId(constructionAreaId)
            .stream()
            .map(buildingModule -> new BiWrapper<>(buildingModule, gameData.getConstructions().findByExternalReference(buildingModule.getBuildingModuleId())))
            .filter(bw -> bw.getEntity2().isPresent())
            .map(bw -> bw.mapEntity2(Optional::get))
            .forEach(bw -> doCancelConstruction(game, bw.getEntity1(), bw.getEntity2()));
    }

    public UUID cancelConstruction(UUID userId, UUID constructionId) {
        Game game = gameDao.findByUserIdValidated(userId);
        GameData gameData = game.getData();

        Construction construction = gameData.getConstructions()
            .findByConstructionIdValidated(constructionId);

        BuildingModule buildingModule = gameData.getBuildingModules()
            .findByBuildingModuleIdValidated(construction.getExternalReference());

        if (!userId.equals(game.getData().getPlanets().findByIdValidated(construction.getLocation()).getOwner())) {
            throw ExceptionFactory.forbiddenOperation(userId + " cannot cancel construction of constructionArea on planet " + constructionId);
        }

        game.getEventLoop()
            .processWithWait(() -> doCancelConstruction(game, buildingModule, construction))
            .getOrThrow();

        return buildingModule.getConstructionAreaId();
    }

    private void doCancelConstruction(Game game, BuildingModule buildingModule, Construction construction) {
        GameData gameData = game.getData();

        gameData.getProcesses()
            .findByExternalReferenceAndTypeValidated(construction.getConstructionId(), ProcessType.CONSTRUCT_BUILDING_MODULE)
            .cleanup();

        gameData.getConstructions()
            .remove(construction);

        allocationRemovalService.removeAllocationsAndReservations(game.getProgressDiff(), gameData, construction.getConstructionId());

        game.getProgressDiff()
            .delete(construction.getConstructionId(), GameItemType.CONSTRUCTION);

        gameData.getBuildingModules()
            .remove(buildingModule);
        game.getProgressDiff()
            .delete(buildingModule.getBuildingModuleId(), GameItemType.BUILDING_MODULE);
    }
}
