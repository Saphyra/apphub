package com.github.saphyra.apphub.service.skyxplore.game.domain.data.building;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class BuildingTest {
    private static final int LEVEL = 324;

    private final Building underTest = Building.builder()
        .buildingId(UUID.randomUUID())
        .location(UUID.randomUUID())
        .surfaceId(UUID.randomUUID())
        .dataId("asd")
        .level(LEVEL)
        .build();

    @Test
    void increaseLevel() {
        underTest.increaseLevel();

        assertThat(underTest.getLevel()).isEqualTo(LEVEL + 1);
    }
}