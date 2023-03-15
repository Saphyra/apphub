package com.github.saphyra.apphub.service.skyxplore.game.process.impl.terraformation;

import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.ConstructionRequirements;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SkillType;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SurfaceType;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.terraforming.TerraformingPossibilitiesService;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planet;
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

    List<RequestWorkProcess> createRequestWorkProcesses(UUID processId, Game game, Planet planet, Surface surface) {
        log.info("Creating RequestWorkProcesses...");
        SurfaceType targetSurfaceType = SurfaceType.valueOf(surface.getTerraformation().getData());

        ConstructionRequirements constructionRequirements = terraformingPossibilitiesService.getOptional(surface.getSurfaceType())
            .orElseThrow(() -> ExceptionFactory.forbiddenOperation(surface.getSurfaceType() + " cannot be terraformed."))
            .stream()
            .filter(terraformingPossibilities -> terraformingPossibilities.getSurfaceType() == targetSurfaceType)
            .findFirst()
            .orElseThrow(() -> ExceptionFactory.forbiddenOperation(surface.getSurfaceType() + " cannot be terraformed to " + targetSurfaceType))
            .getConstructionRequirements();
        log.info("{}", constructionRequirements);

        return requestWorkProcessFactory.create(
            game,
            processId,
            planet,
            surface.getTerraformation().getConstructionId(),
            RequestWorkProcessType.TERRAFORMATION,
            SkillType.BUILDING,
            constructionRequirements.getRequiredWorkPoints(),
            constructionRequirements.getParallelWorkers()
        );
    }
}
