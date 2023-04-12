package com.github.saphyra.apphub.service.skyxplore.game.service.save.converter;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building_allocation.BuildingAllocationToModelConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen.CitizenConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen_allocation.CitizenAllocationToModelConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.durability.DurabilityToModelConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.skill.SkillConverter;
import com.github.saphyra.apphub.service.skyxplore.game.process.Process;
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
    private final SurfaceToModelConverter surfaceToModelConverter;
    private final BuildingToModelConverter buildingToModelConverter;
    private final ConstructionToModelConverter constructionToModelConverter;
    private final PriorityToModelConverter priorityToModelConverter;
    private final DeconstructionToModelConverter deconstructionToModelConverter;
    private final AllocatedResourceToModelConverter allocatedResourceToModelConverter;
    private final ReservedStorageToModelConverter reservedStorageToModelConverter;
    private final StorageSettingToModelConverter storageSettingToModelConverter;
    private final StoredResourceToModelConverter storedResourceToModelConverter;
    private final CitizenConverter citizenConverter;
    private final SkillConverter skillConverter;
    private final DurabilityToModelConverter durabilityToModelConverter;
    private final BuildingAllocationToModelConverter buildingAllocationToModelConverter;
    private final CitizenAllocationToModelConverter citizenAllocationToModelConverter;

    public List<GameItem> convert(UUID gameId, GameData data) {
        List<GameItem> result = new ArrayList<>();

        //Map
        result.addAll(coordinateToModelConverter.convert(gameId, data.getCoordinates()));
        result.addAll(solarSystemToModelConverter.convert(gameId, data.getSolarSystems()));
        result.addAll(planetToModelConverter.convert(gameId, data.getPlanets().values()));

        //Planet
        result.addAll(surfaceToModelConverter.convert(gameId, data.getSurfaces()));
        result.addAll(buildingToModelConverter.convert(gameId, data.getBuildings()));
        result.addAll(constructionToModelConverter.convert(gameId, data.getConstructions()));
        result.addAll(priorityToModelConverter.convert(gameId, data.getPriorities()));
        result.addAll(deconstructionToModelConverter.convert(gameId, data.getDeconstructions()));

        //Storage
        result.addAll(allocatedResourceToModelConverter.convert(gameId, data.getAllocatedResources()));
        result.addAll(reservedStorageToModelConverter.convert(gameId, data.getReservedStorages()));
        result.addAll(storageSettingToModelConverter.convert(gameId, data.getStorageSettings()));
        result.addAll(storedResourceToModelConverter.convert(gameId, data.getStoredResources()));

        //Citizen
        result.addAll(citizenConverter.toModel(gameId, data.getCitizens()));
        result.addAll(skillConverter.toModel(gameId, data.getSkills()));
        result.addAll(durabilityToModelConverter.convert(gameId, data.getDurabilities()));

        //Process
        result.addAll(data.getProcesses().stream().map(Process::toModel).toList());
        result.addAll(buildingAllocationToModelConverter.convert(gameId, data.getBuildingAllocations()));
        result.addAll(citizenAllocationToModelConverter.convert(gameId, data.getCitizenAllocations()));

        return result;
    }
}
