package com.github.saphyra.apphub.service.skyxplore.game.domain.data.deconstruction;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class DeconstructionTest {
    private static final int CURRENT_WORK_POINTS = 34;
    private static final int DELTA = 4253;

    private final Deconstruction underTest = Deconstruction.builder()
        .deconstructionId(UUID.randomUUID())
        .externalReference(UUID.randomUUID())
        .location(UUID.randomUUID())
        .currentWorkPoints(CURRENT_WORK_POINTS)
        .priority(32)
        .build();

    @Test
    void increaseWorkPoints() {
        underTest.increaseWorkPoints(DELTA);

        assertThat(underTest.getCurrentWorkPoints()).isEqualTo(CURRENT_WORK_POINTS + DELTA);
    }
}