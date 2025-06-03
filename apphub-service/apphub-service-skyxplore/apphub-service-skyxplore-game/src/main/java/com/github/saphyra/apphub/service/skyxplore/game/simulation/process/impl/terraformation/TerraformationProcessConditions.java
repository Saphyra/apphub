package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.terraformation;

import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessStatus;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SurfaceType;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.terraforming.TerraformingPossibilitiesService;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Construction;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.StoredResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class TerraformationProcessConditions {
    private final TerraformingPossibilitiesService terraformingPossibilitiesService;
    private final StoredResourceService storedResourceService;

    boolean resourcesAvailable(GameData gameData, UUID processId, UUID constructionId) {
        Construction construction = gameData.getConstructions()
            .findByIdValidated(constructionId);
        SurfaceType newSurfaceType = SurfaceType.valueOf(construction.getData());
        SurfaceType currentSurfaceType = gameData.getSurfaces()
            .findByIdValidated(construction.getExternalReference())
            .getSurfaceType();
        Map<String, Integer> requiredResources = terraformingPossibilitiesService.get(currentSurfaceType)
            .findBySurfaceType(newSurfaceType)
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

    boolean hasWorkProcesses(GameData gameData, UUID processId) {
        return !gameData.getProcesses()
            .getByExternalReferenceAndType(processId, ProcessType.WORK)
            .isEmpty();
    }

    boolean workFinished(GameData gameData, UUID processId) {
        return gameData.getProcesses()
            .getByExternalReferenceAndType(processId, ProcessType.WORK)
            .stream()
            .allMatch(process -> process.getStatus() == ProcessStatus.DONE);
    }
}
