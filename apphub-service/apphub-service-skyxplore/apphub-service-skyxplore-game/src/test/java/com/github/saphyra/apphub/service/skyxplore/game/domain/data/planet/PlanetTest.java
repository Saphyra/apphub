package com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class PlanetTest {
    @Test
    void hasOwner() {
        Planet planet = Planet.builder()
            .planetId(UUID.randomUUID())
            .solarSystemId(UUID.randomUUID())
            .defaultName("asd")
            .size(342)
            .orbitRadius(34d)
            .orbitSpeed(32d)
            .owner(UUID.randomUUID())
            .build();

        assertThat(planet.hasOwner()).isTrue();
    }

    @Test
    void doesNotHaveOwner() {
        Planet planet = Planet.builder()
            .planetId(UUID.randomUUID())
            .solarSystemId(UUID.randomUUID())
            .defaultName("asd")
            .size(342)
            .orbitRadius(34d)
            .orbitSpeed(32d)
            .owner(null)
            .build();

        assertThat(planet.hasOwner()).isFalse();
    }
}