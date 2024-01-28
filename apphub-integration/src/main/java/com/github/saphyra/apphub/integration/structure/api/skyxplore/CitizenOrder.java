package com.github.saphyra.apphub.integration.structure.api.skyxplore;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Getter
public enum CitizenOrder {
    ASCENDING(1, "skyxplore-game-population-order-ascending"),
    DESCENDING(-1, "skyxplore-game-population-order-descending");

    private final Integer value;
    private final String id;

    public List<String> sort(List<String> in) {
        return in.stream()
            .sorted((o1, o2) -> value * o1.compareTo(o2))
            .collect(Collectors.toList());
    }
}
