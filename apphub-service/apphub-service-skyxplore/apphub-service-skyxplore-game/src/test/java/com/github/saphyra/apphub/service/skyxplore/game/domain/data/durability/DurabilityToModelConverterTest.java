package com.github.saphyra.apphub.service.skyxplore.game.domain.data.durability;

import com.github.saphyra.apphub.api.skyxplore.model.game.DurabilityModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class DurabilityToModelConverterTest {
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID DURABILITY_ID = UUID.randomUUID();
    private static final UUID EXTERNAL_REFERENCE = UUID.randomUUID();
    private static final Integer MAX_HIT_POINTS = 245;
    private static final Integer CURRENT_HIT_POINTS = 64;

    private final DurabilityToModelConverter underTest = new DurabilityToModelConverter();

    @Test
    void convert() {
        Durability durability = Durability.builder()
            .durabilityId(DURABILITY_ID)
            .externalReference(EXTERNAL_REFERENCE)
            .maxHitPoints(MAX_HIT_POINTS)
            .currentHitPoints(CURRENT_HIT_POINTS)
            .build();

        DurabilityModel result = underTest.convert(GAME_ID, durability);

        assertThat(result.getId()).isEqualTo(DURABILITY_ID);
        assertThat(result.getGameId()).isEqualTo(GAME_ID);
        assertThat(result.getType()).isEqualTo(GameItemType.DURABILITY);
        assertThat(result.getExternalReference()).isEqualTo(EXTERNAL_REFERENCE);
        assertThat(result.getMaxHitPoints()).isEqualTo(MAX_HIT_POINTS);
        assertThat(result.getCurrentHitPoints()).isEqualTo(CURRENT_HIT_POINTS);
    }
}