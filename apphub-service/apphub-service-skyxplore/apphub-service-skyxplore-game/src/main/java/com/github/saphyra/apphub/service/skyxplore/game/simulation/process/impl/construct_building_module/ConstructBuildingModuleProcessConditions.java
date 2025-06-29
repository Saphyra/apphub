package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.construct_building_module;

import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessStatus;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.module.BuildingModuleDataService;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.StoredResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class ConstructBuildingModuleProcessConditions {
    private final BuildingModuleDataService buildingModuleDataService;
    private final StoredResourceService storedResourceService;

    boolean resourcesAvailable(GameData gameData, UUID processId, UUID constructionId) {
        UUID buildingModuleId = gameData.getConstructions()
            .findByIdValidated(constructionId)
            .getExternalReference();
        String dataId = gameData.getBuildingModules()
            .findByIdValidated(buildingModuleId)
            .getDataId();
        Map<String, Integer> requiredResources = buildingModuleDataService.get(dataId)
            .getConstructionRequirements()
            .getRequiredResources();

        return requiredResources.entrySet()
            .stream()
            .allMatch(entry -> hasEnough(gameData, processId, entry.getKey(), entry.getValue()));
    }

    private boolean hasEnough(GameData gameData, UUID processId, String dataId, Integer amount) {
        int available = storedResourceService.count(gameData, dataId, processId);
        log.info("There is {} of {} available. Needed: {}", available, dataId, amount);
        return available >= amount;
    }

    public boolean hasWorkProcesses(GameData gameData, UUID processId) {
        return !gameData.getProcesses()
            .getByExternalReferenceAndType(processId, ProcessType.WORK)
            .isEmpty();
    }

    public boolean workFinished(GameData gameData, UUID processId) {
        return gameData.getProcesses()
            .getByExternalReferenceAndType(processId, ProcessType.WORK)
            .stream()
            .allMatch(process -> process.getStatus() == ProcessStatus.DONE);
    }
}
