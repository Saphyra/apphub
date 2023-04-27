package com.github.saphyra.apphub.service.skyxplore.game.domain.data.allocated_resource;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AllocatedResourceTest {
    private static final int AMOUNT = 234;
    private static final int ADDITION = 2345;

    private final AllocatedResource underTest = AllocatedResource.builder()
        .allocatedResourceId(UUID.randomUUID())
        .location(UUID.randomUUID())
        .externalReference(UUID.randomUUID())
        .dataId("data-id")
        .amount(AMOUNT)
        .build();

    @Test
    void increaseAmount() {
        underTest.increaseAmount(ADDITION);

        assertThat(underTest.getAmount()).isEqualTo(AMOUNT + ADDITION);
    }
}