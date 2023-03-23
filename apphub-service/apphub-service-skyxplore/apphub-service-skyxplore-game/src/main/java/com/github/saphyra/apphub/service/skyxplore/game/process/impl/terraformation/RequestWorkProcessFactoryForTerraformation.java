package com.github.saphyra.apphub.service.skyxplore.game.process.impl.terraformation;

import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.ConstructionRequirements;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SkillType;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SurfaceType;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.terraforming.TerraformingPossibilitiesService;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Construction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.Surface;
import com.github.saphyra.apphub.service.skyxplore.game.process.impl.request_work.RequestWorkProcess;
import com.github.saphyra.apphub.service.skyxplore.game.process.impl.request_work.RequestWorkProcessFactory;
import com.github.saphyra.apphub.service.skyxplore.game.process.impl.request_work.RequestWorkProcessType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class RequestWorkProcessFactoryForTerraformation {
    private final TerraformingPossibilitiesService terraformingPossibilitiesService;
    private final RequestWorkProcessFactory requestWorkProcessFactory;

    List<RequestWorkProcess> createRequestWorkProcesses(GameData gameData, UUID location, UUID processId, Surface surface) {
        log.info("Creating RequestWorkProcesses...");
        Construction terraformation = gameData.getConstructions().findByExternalReferenceValidated(surface.getSurfaceId());
        SurfaceType targetSurfaceType = SurfaceType.valueOf(terraformation.getData());

        ConstructionRequirements constructionRequirements = terraformingPossibilitiesService.getOptional(surface.getSurfaceType())
            .orElseThrow(() -> ExceptionFactory.forbiddenOperation(surface.getSurfaceType() + " cannot be terraformed."))
            .stream()
            .filter(terraformingPossibilities -> terraformingPossibilities.getSurfaceType() == targetSurfaceType)
            .findFirst()
            .orElseThrow(() -> ExceptionFactory.forbiddenOperation(surface.getSurfaceType() + " cannot be terraformed to " + targetSurfaceType))
            .getConstructionRequirements();
        log.info("{}", constructionRequirements);

        return requestWorkProcessFactory.create(
            gameData,
            processId,
            location,
            terraformation.getConstructionId(),
            RequestWorkProcessType.TERRAFORMATION,
            SkillType.BUILDING,
            constructionRequirements.getRequiredWorkPoints(),
            constructionRequirements.getParallelWorkers()
        );
    }
}
