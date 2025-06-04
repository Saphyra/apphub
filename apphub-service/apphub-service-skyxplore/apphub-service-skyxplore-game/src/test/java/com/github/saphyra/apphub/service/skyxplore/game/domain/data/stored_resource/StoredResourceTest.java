package com.github.saphyra.apphub.service.skyxplore.game.domain.data.stored_resource;

import com.github.saphyra.apphub.api.skyxplore.model.game.ContainerType;
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
            .containerId(UUID.randomUUID())
            .containerType(ContainerType.PRODUCER_OUTPUT)
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
            .containerId(UUID.randomUUID())
            .containerType(ContainerType.PRODUCER_OUTPUT)
            .build();

        underTest.increaseAmount(MODIFICATION);

        assertThat(underTest.getAmount()).isEqualTo(AMOUNT + MODIFICATION);
    }
}