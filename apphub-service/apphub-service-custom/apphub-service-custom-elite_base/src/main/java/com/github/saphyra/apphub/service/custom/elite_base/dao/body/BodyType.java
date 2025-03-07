package com.github.saphyra.apphub.service.custom.elite_base.dao.body;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static io.micrometer.common.util.StringUtils.isBlank;

public enum BodyType {
    PLANET(List.of("Planet")), //Planet or moon
    STAR(List.of("Star")),
    STATION(List.of("Station")),
    PLANETARY_RING(List.of("PlanetaryRing")),
    STELLAR_RING(List.of("StellarRing")),
    ASTEROID_CLUSTER(List.of("AsteroidCluster")),
    ;

    private final List<String> values;

    BodyType(List<String> values) {
        this.values = values.stream()
            .map(String::toLowerCase)
            .collect(Collectors.toList());
    }

    public static BodyType parse(String in) {
        if (isBlank(in)) {
            return null;
        }

        if ("Null".equalsIgnoreCase(in)) {
            return null;
        }

        return Arrays.stream(values())
            .filter(e -> e.values.contains(in.toLowerCase()))
            .findAny()
            .orElseThrow(() -> new IllegalArgumentException("Could not parse " + in + " to " + BodyType.class.getSimpleName()));
    }
}
