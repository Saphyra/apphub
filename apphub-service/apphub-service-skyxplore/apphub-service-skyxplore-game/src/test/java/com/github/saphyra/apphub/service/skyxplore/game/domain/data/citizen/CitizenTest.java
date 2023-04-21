package com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CitizenTest {
    private static final int MORALE = 23143;
    private static final int DELTA = 245;
    private static final Integer SATIETY = 2354;

    private final Citizen underTest = Citizen.builder()
        .citizenId(UUID.randomUUID())
        .location(UUID.randomUUID())
        .name("asd")
        .morale(MORALE)
        .satiety(SATIETY)
        .build();

    @Test
    void reduceMorale() {
        underTest.reduceMorale(DELTA);

        assertThat(underTest.getMorale()).isEqualTo(MORALE - DELTA);
    }

    @Test
    void decreaseSatiety() {
        underTest.decreaseSatiety(DELTA);

        assertThat(underTest.getSatiety()).isEqualTo(SATIETY - DELTA);
    }

    @Test
    void increaseMorale() {
        underTest.increaseMorale(DELTA);

        assertThat(underTest.getMorale()).isEqualTo(MORALE + DELTA);
    }
}