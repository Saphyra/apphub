package com.github.saphyra.apphub.service.skyxplore.game.domain.map;

import com.github.saphyra.apphub.api.skyxplore.model.game.CoordinateModel;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.collection.OptionalHashMap;
import com.github.saphyra.apphub.lib.common_util.collection.OptionalMap;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameConstants;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.citizen.Citizen;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StorageDetails;
import com.google.gson.Gson;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Planet {
    private final UUID planetId;
    private final UUID solarSystemId;
    private final String defaultName;
    @Builder.Default
    private final OptionalMap<UUID, String> customNames = new OptionalHashMap<>();
    private final CoordinateModel coordinate;
    private final int size;
    private final SurfaceMap surfaces;
    private UUID owner;
    @Builder.Default
    private final BuildingAllocations buildingAllocations = new BuildingAllocations(); //BuildingId <-> List<ProcessId>
    @Builder.Default
    private final CitizenAllocations citizenAllocations = new CitizenAllocations(); //CitizenId <-> ProcessId

    @Builder.Default
    private final OptionalMap<UUID, Citizen> population = new OptionalHashMap<>();

    private final StorageDetails storageDetails;

    @Builder.Default
    private final Map<PriorityType, Integer> priorities = getDefaultPriorities();

    private static Map<PriorityType, Integer> getDefaultPriorities() {
        return Arrays.stream(PriorityType.values())
            .collect(Collectors.toMap(Function.identity(), priorityType -> GameConstants.DEFAULT_PRIORITY));
    }

    public List<Building> getBuildings() {
        return surfaces.values()
            .stream()
            .filter(surface -> !isNull(surface.getBuilding()))
            .map(Surface::getBuilding)
            .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return String.format("Planet(%s)", new Gson().toJson(this));
    }

    public Building findBuildingByConstructionIdValidated(UUID constructionId) {
        return surfaces.values()
            .stream()
            .filter(surface -> !isNull(surface.getBuilding()))
            .map(Surface::getBuilding)
            .filter(building -> !isNull(building.getConstruction()))
            .filter(building -> building.getConstruction().getConstructionId().equals(constructionId))
            .findFirst()
            .orElseThrow(() -> ExceptionFactory.loggedException(HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND, "Building not found by constructionId " + constructionId));
    }

    public Surface findSurfaceByBuildingIdValidated(UUID buildingId) {
        return surfaces.values()
            .stream()
            .filter(surface -> !isNull(surface.getBuilding()))
            .filter(surface -> surface.getBuilding().getBuildingId().equals(buildingId))
            .findFirst()
            .orElseThrow(() -> ExceptionFactory.loggedException(HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND, "Surface not found by buildingId " + buildingId));
    }
}
