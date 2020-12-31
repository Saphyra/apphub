package com.github.saphyra.apphub.service.skyxplore.game.domain.map;

import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.Citizen;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StorageDetails;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Planet {
    private final UUID planetId;
    private final UUID solarSystemId;
    private final String planetName; //TODO allow users to rename planets
    private final Coordinate coordinate;
    private final int size;
    private final Map<Coordinate, Surface> surfaces;
    private UUID owner;

    @Builder.Default
    private final Map<UUID, Citizen> population = new HashMap<>();

    @Builder.Default
    private final StorageDetails storageDetails = new StorageDetails();

    @Builder.Default
    private final Map<PriorityType, Integer> priorities = Arrays.stream(PriorityType.values())
        .collect(Collectors.toMap(Function.identity(), priorityType -> 5));
}
