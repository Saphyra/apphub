package com.github.saphyra.apphub.service.skyxplore.game.domain.map;

import com.github.saphyra.apphub.api.skyxplore.model.game.CoordinateModel;
import com.github.saphyra.apphub.lib.common_util.collection.OptionalHashMap;
import com.github.saphyra.apphub.lib.common_util.collection.OptionalMap;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameConstants;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.citizen.Citizen;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StorageDetails;
import com.google.gson.Gson;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
    private final OptionalMap<UUID, Citizen> population = new OptionalHashMap<>();

    @Builder.Default
    private final StorageDetails storageDetails = StorageDetails.builder().build();

    @Builder.Default
    private final Map<PriorityType, Integer> priorities = getDefaultPriorities();

    @Builder.Default
    private final Set<ProductionOrder> orders = new HashSet<>();

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
}
