package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.construct_building_module;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SkillType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.GameProgressDiff;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Construction;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.AllocationRemovalService;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.StoredResourceService;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.resource_request.ResourceRequestProcessFactory;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.work.WorkProcessFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class ConstructBuildingModuleProcessHelper {
    private final WorkProcessFactory workProcessFactory;
    private final AllocationRemovalService allocationRemovalService;
    private final ResourceRequestProcessFactory resourceRequestProcessFactory;
    private final StoredResourceService storedResourceService;

    //TODO unit test
    public void createResourceRequestProcess(Game game, UUID location, UUID processId, UUID constructionId) {
        game.getData()
            .getReservedStorages()
            .getByExternalReference(constructionId)
            .forEach(reservedStorage -> resourceRequestProcessFactory.save(game, location, processId, reservedStorage.getReservedStorageId()));
    }

    //TODO unit test
    void startWork(Game game, UUID processId, UUID constructionId) {
        storedResourceService.useResources(game.getProgressDiff(), game.getData(), processId);

        Construction construction = game.getData()
            .getConstructions()
            .findByIdValidated(constructionId);

        workProcessFactory.save(game, construction.getLocation(), processId, construction.getRequiredWorkPoints(), SkillType.BUILDING);
    }

    void finishConstruction(GameProgressDiff progressDiff, GameData gameData, UUID constructionId) {
        log.info("Finishing constructionArea construction...");
        Construction construction = gameData.getConstructions()
            .findByIdValidated(constructionId);

        allocationRemovalService.removeAllocationsAndReservations(progressDiff, gameData, constructionId);

        gameData.getConstructions()
            .remove(construction);

        progressDiff.delete(constructionId, GameItemType.CONSTRUCTION);
    }
}
