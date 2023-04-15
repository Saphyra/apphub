package com.github.saphyra.apphub.service.skyxplore.game.service.save.converter;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.allocated_resource.AllocatedResourceConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building.BuildingConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building_allocation.BuildingAllocationConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen.CitizenConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen_allocation.CitizenAllocationConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.ConstructionConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.deconstruction.DeconstructionConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.durability.DurabilityToModelConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.reserved_storage.ReservedStorageConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.skill.SkillConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.stored_resource.StoredResourceConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.SurfaceConverter;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.Process;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class GameDataConverter {
    private final CoordinateToModelConverter coordinateToModelConverter;
    private final SolarSystemToModelConverter solarSystemToModelConverter;
    private final PlanetToModelConverter planetToModelConverter;
    private final SurfaceConverter surfaceConverter;
    private final BuildingConverter buildingConverter;
    private final ConstructionConverter constructionConverter;
    private final PriorityToModelConverter priorityToModelConverter;
    private final DeconstructionConverter deconstructionConverter;
    private final AllocatedResourceConverter allocatedResourceConverter;
    private final ReservedStorageConverter reservedStorageConverter;
    private final StorageSettingToModelConverter storageSettingToModelConverter;
    private final StoredResourceConverter storedResourceConverter;
    private final CitizenConverter citizenConverter;
    private final SkillConverter skillConverter;
    private final DurabilityToModelConverter durabilityToModelConverter;
    private final BuildingAllocationConverter buildingAllocationConverter;
    private final CitizenAllocationConverter citizenAllocationConverter;

    public List<GameItem> convert(UUID gameId, GameData data) {
        List<GameItem> result = new ArrayList<>();

        //Map
        result.addAll(coordinateToModelConverter.convert(gameId, data.getCoordinates()));
        result.addAll(solarSystemToModelConverter.convert(gameId, data.getSolarSystems()));
        result.addAll(planetToModelConverter.convert(gameId, data.getPlanets().values()));

        //Planet
        result.addAll(surfaceConverter.toModel(gameId, data.getSurfaces()));
        result.addAll(buildingConverter.toModel(gameId, data.getBuildings()));
        result.addAll(constructionConverter.toModel(gameId, data.getConstructions()));
        result.addAll(priorityToModelConverter.convert(gameId, data.getPriorities()));
        result.addAll(deconstructionConverter.toModel(gameId, data.getDeconstructions()));

        //Storage
        result.addAll(allocatedResourceConverter.toModel(gameId, data.getAllocatedResources()));
        result.addAll(reservedStorageConverter.toModel(gameId, data.getReservedStorages()));
        result.addAll(storageSettingToModelConverter.convert(gameId, data.getStorageSettings()));
        result.addAll(storedResourceConverter.toModel(gameId, data.getStoredResources()));

        //Citizen
        result.addAll(citizenConverter.toModel(gameId, data.getCitizens()));
        result.addAll(skillConverter.toModel(gameId, data.getSkills()));
        result.addAll(durabilityToModelConverter.convert(gameId, data.getDurabilities()));

        //Process
        result.addAll(data.getProcesses().stream().map(Process::toModel).toList());
        result.addAll(buildingAllocationConverter.toModel(gameId, data.getBuildingAllocations()));
        result.addAll(citizenAllocationConverter.toModel(gameId, data.getCitizenAllocations()));

        return result;
    }
}
