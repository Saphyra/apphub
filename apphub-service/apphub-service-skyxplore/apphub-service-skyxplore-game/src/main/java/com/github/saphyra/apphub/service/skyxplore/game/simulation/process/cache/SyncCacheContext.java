package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.cache;

import com.github.saphyra.apphub.service.skyxplore.game.domain.data.allocated_resource.AllocatedResourceConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building.BuildingConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen.CitizenConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.ConstructionConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.deconstruction.DeconstructionConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.skill.SkillConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.SurfaceConverter;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue.QueueItemToResponseConverter;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue.service.construction.BuildingConstructionToQueueItemConverter;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue.service.deconstruction.BuildingDeconstructionToQueueItemConverter;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue.service.terraformation.TerraformationToQueueItemConverter;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.overview.PlanetStorageOverviewQueryService;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.building.overview.PlanetBuildingOverviewQueryService;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.stored_resource.StoredResourceConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.reserved_storage.ReservedStorageConverter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
@Getter
class SyncCacheContext {
    //Platform
    private final WsMessageKeyFactory messageKeyFactory;

    private final PlanetStorageOverviewQueryService planetStorageOverviewQueryService;

    //Converters
    private final AllocatedResourceConverter allocatedResourceConverter;
    private final BuildingConverter buildingConverter;
    private final CitizenConverter citizenConverter;
    private final ConstructionConverter constructionConverter;
    private final DeconstructionConverter deconstructionConverter;
    private final ReservedStorageConverter reservedStorageConverter;
    private final SkillConverter skillConverter;
    private final StoredResourceConverter storedResourceConverter;
    private final SurfaceConverter surfaceConverter;
    private final PlanetBuildingOverviewQueryService planetBuildingOverviewQueryService;

    //Queue item
    private final QueueItemToResponseConverter queueItemToResponseConverter;
    private final BuildingConstructionToQueueItemConverter buildingConstructionToQueueItemConverter;
    private final BuildingDeconstructionToQueueItemConverter buildingDeconstructionToQueueItemConverter;
    private final TerraformationToQueueItemConverter terraformationToQueueItemConverter;
}
