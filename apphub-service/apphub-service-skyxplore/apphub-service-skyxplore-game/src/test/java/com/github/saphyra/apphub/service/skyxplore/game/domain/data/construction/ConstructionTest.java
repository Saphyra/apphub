package com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction;

import com.github.saphyra.apphub.api.skyxplore.model.game.ConstructionType;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ConstructionTest {
    private static final Integer CURRENT_WORK_POINTS = 234;
    private static final int DELTA = 254;

    private final Construction underTest = Construction.builder()
        .constructionId(UUID.randomUUID())
        .externalReference(UUID.randomUUID())
        .constructionType(ConstructionType.CONSTRUCTION)
        .location(UUID.randomUUID())
        .parallelWorkers(0)
        .requiredWorkPoints(34)
        .data("")
        .currentWorkPoints(CURRENT_WORK_POINTS)
        .priority(3)
        .build();

    @Test
    void increaseCurrentWorkPoints() {
        underTest.increaseCurrentWorkPoints(DELTA);

        assertThat(underTest.getCurrentWorkPoints()).isEqualTo(CURRENT_WORK_POINTS + DELTA);
    }
}