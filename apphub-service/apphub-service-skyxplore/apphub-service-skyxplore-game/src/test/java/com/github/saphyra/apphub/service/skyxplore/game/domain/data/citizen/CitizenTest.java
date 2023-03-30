package com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CitizenTest {
    private static final int MORALE = 23143;
    private static final int REDUCTION = 245;

    private final Citizen underTest = Citizen.builder()
        .citizenId(UUID.randomUUID())
        .location(UUID.randomUUID())
        .name("asd")
        .morale(MORALE)
        .satiety(2345)
        .build();

    @Test
    void reduceMorale() {
        underTest.reduceMorale(REDUCTION);

        assertThat(underTest.getMorale()).isEqualTo(MORALE - REDUCTION);
    }
}