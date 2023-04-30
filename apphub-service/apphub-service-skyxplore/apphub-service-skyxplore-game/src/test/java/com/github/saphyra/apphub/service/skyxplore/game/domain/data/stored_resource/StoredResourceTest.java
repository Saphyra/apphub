package com.github.saphyra.apphub.service.skyxplore.game.domain.data.stored_resource;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class StoredResourceTest {
    private static final Integer AMOUNT = 46;
    private static final int MODIFICATION = 45;

    @Test
    void decreaseAmount() {
        StoredResource underTest = StoredResource.builder()
            .storedResourceId(UUID.randomUUID())
            .location(UUID.randomUUID())
            .dataId("asd")
            .amount(AMOUNT)
            .build();

        underTest.decreaseAmount(MODIFICATION);

        assertThat(underTest.getAmount()).isEqualTo(AMOUNT - MODIFICATION);
    }

    @Test
    void increaseAmount() {
        StoredResource underTest = StoredResource.builder()
            .storedResourceId(UUID.randomUUID())
            .location(UUID.randomUUID())
            .dataId("asd")
            .amount(AMOUNT)
            .build();

        underTest.increaseAmount(MODIFICATION);

        assertThat(underTest.getAmount()).isEqualTo(AMOUNT + MODIFICATION);
    }
}