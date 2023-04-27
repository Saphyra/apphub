package com.github.saphyra.apphub.service.skyxplore.game.domain.data.allocated_resource;

import com.github.saphyra.apphub.api.skyxplore.model.game.AllocatedResourceModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AllocatedResourceConverterTest {
    private static final UUID ALLOCATED_RESOURCE_ID = UUID.randomUUID();
    private static final UUID LOCATION = UUID.randomUUID();
    private static final UUID EXTERNAL_REFERENCE = UUID.randomUUID();
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final String DATA_ID = "data-id";
    private static final int AMOUNT = 321;

    private final AllocatedResourceConverter underTest = new AllocatedResourceConverter();

    @Test
    void toModel() {
        AllocatedResource allocatedResource = AllocatedResource.builder()
            .allocatedResourceId(ALLOCATED_RESOURCE_ID)
            .location(LOCATION)
            .externalReference(EXTERNAL_REFERENCE)
            .dataId(DATA_ID)
            .amount(AMOUNT)
            .build();

        AllocatedResourceModel result = underTest.toModel(GAME_ID, allocatedResource);

        assertThat(result.getId()).isEqualTo(ALLOCATED_RESOURCE_ID);
        assertThat(result.getGameId()).isEqualTo(GAME_ID);
        assertThat(result.getType()).isEqualTo(GameItemType.ALLOCATED_RESOURCE);
        assertThat(result.getLocation()).isEqualTo(LOCATION);
        assertThat(result.getExternalReference()).isEqualTo(EXTERNAL_REFERENCE);
        assertThat(result.getDataId()).isEqualTo(DATA_ID);
        assertThat(result.getAmount()).isEqualTo(AMOUNT);
    }
}