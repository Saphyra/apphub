package com.github.saphyra.apphub.service.skyxplore.game.service.save.converter;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
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
//TODO unit test
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
    private final CitizenToModelConverter citizenToModelConverter;
    private final SkillToModelConverter skillToModelConverter;

    public List<GameItem> convert(UUID gameId, GameData data) {
        List<GameItem> result = new ArrayList<>();

        result.addAll(coordinateToModelConverter.convert(gameId, data.getCoordinates()));
        result.addAll(solarSystemToModelConverter.convert(gameId, data.getSolarSystems()));
        result.addAll(planetToModelConverter.convert(gameId, data.getPlanets().values()));
        result.addAll(surfaceToModelConverter.convert(gameId, data.getSurfaces()));
        result.addAll(buildingToModelConverter.convert(gameId, data.getBuildings()));
        result.addAll(constructionToModelConverter.convert(gameId, data.getConstructions()));
        result.addAll(priorityToModelConverter.convert(gameId, data.getPriorities()));
        result.addAll(deconstructionToModelConverter.convert(gameId, data.getDeconstructions()));
        result.addAll(allocatedResourceToModelConverter.convert(gameId, data.getAllocatedResources()));
        result.addAll(reservedStorageToModelConverter.convert(gameId, data.getReservedStorages()));
        result.addAll(storageSettingToModelConverter.convert(gameId, data.getStorageSettings()));
        result.addAll(storedResourceToModelConverter.convert(gameId, data.getStoredResources()));
        result.addAll(citizenToModelConverter.convert(gameId, data.getCitizens()));
        result.addAll(skillToModelConverter.convert(gameId, data.getSkills()));
        result.addAll(data.getProcesses().stream().map(Process::toModel).toList());

        return result;
    }
}