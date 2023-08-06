package com.github.saphyra.apphub.service.skyxplore.game.domain.data;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.allocated_resource.AllocatedResourceConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building.BuildingConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building_allocation.BuildingAllocationConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen.CitizenConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen_allocation.CitizenAllocationConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.ConstructionConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.coordinate.CoordinateConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.deconstruction.DeconstructionConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.durability.DurabilityConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.reserved_storage.ReservedStorageConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.skill.SkillConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.stored_resource.StoredResourceConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.SurfaceConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.PlanetConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.priority.PriorityConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.solar_system.SolarSystemConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.storage_setting.StorageSettingConverter;
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
//TODO unit test
public class GameDataConverter {
    private final CoordinateConverter coordinateConverter;
    private final SolarSystemConverter solarSystemConverter;
    private final PlanetConverter planetConverter;
    private final SurfaceConverter surfaceConverter;
    private final BuildingConverter buildingConverter;
    private final ConstructionConverter constructionConverter;
    private final PriorityConverter priorityConverter;
    private final DeconstructionConverter deconstructionConverter;
    private final AllocatedResourceConverter allocatedResourceConverter;
    private final ReservedStorageConverter reservedStorageConverter;
    private final StorageSettingConverter storageSettingConverter;
    private final StoredResourceConverter storedResourceConverter;
    private final CitizenConverter citizenConverter;
    private final SkillConverter skillConverter;
    private final DurabilityConverter durabilityConverter;
    private final BuildingAllocationConverter buildingAllocationConverter;
    private final CitizenAllocationConverter citizenAllocationConverter;

    public List<GameItem> convert(UUID gameId, GameData data) {
        List<GameItem> result = new ArrayList<>();

        //Map
        result.addAll(coordinateConverter.convert(gameId, data.getCoordinates()));
        result.addAll(solarSystemConverter.toModel(gameId, data.getSolarSystems()));
        result.addAll(planetConverter.toModel(gameId, data.getPlanets().values()));

        //Planet
        result.addAll(surfaceConverter.toModel(gameId, data.getSurfaces()));
        result.addAll(buildingConverter.toModel(gameId, data.getBuildings()));
        result.addAll(constructionConverter.toModel(gameId, data.getConstructions()));
        result.addAll(priorityConverter.toModel(gameId, data.getPriorities()));
        result.addAll(deconstructionConverter.toModel(gameId, data.getDeconstructions()));

        //Storage
        result.addAll(allocatedResourceConverter.toModel(gameId, data.getAllocatedResources()));
        result.addAll(reservedStorageConverter.toModel(gameId, data.getReservedStorages()));
        result.addAll(storageSettingConverter.toModel(gameId, data.getStorageSettings()));
        result.addAll(storedResourceConverter.toModel(gameId, data.getStoredResources()));

        //Citizen
        result.addAll(citizenConverter.toModel(gameId, data.getCitizens()));
        result.addAll(skillConverter.toModel(gameId, data.getSkills()));
        result.addAll(durabilityConverter.convert(gameId, data.getDurabilities()));

        //Process
        result.addAll(data.getProcesses().stream().map(Process::toModel).toList());
        result.addAll(buildingAllocationConverter.toModel(gameId, data.getBuildingAllocations()));
        result.addAll(citizenAllocationConverter.toModel(gameId, data.getCitizenAllocations()));

        return result;
    }
}
