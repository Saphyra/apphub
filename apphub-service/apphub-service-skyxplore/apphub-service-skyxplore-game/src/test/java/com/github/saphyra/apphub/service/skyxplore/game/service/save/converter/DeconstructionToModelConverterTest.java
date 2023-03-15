package com.github.saphyra.apphub.service.skyxplore.game.service.save.converter;

import com.github.saphyra.apphub.api.skyxplore.model.game.DeconstructionModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.deconstruction.Deconstruction;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class DeconstructionToModelConverterTest {
    private static final UUID DECONSTRUCTION_ID = UUID.randomUUID();
    private static final UUID EXTERNAL_REFERENCE = UUID.randomUUID();
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final int CURRENT_WORK_POINTS = 256;
    private static final int PRIORITY = 568;

    @InjectMocks
    private DeconstructionToModelConverter underTest;

    @Test
    void convert() {
        Deconstruction deconstruction = Deconstruction.builder()
            .deconstructionId(DECONSTRUCTION_ID)
            .externalReference(EXTERNAL_REFERENCE)
            .currentWorkPoints(CURRENT_WORK_POINTS)
            .priority(PRIORITY)
            .build();

        DeconstructionModel result = underTest.convert(deconstruction, GAME_ID);

        assertThat(result.getId()).isEqualTo(DECONSTRUCTION_ID);
        assertThat(result.getGameId()).isEqualTo(GAME_ID);
        assertThat(result.getType()).isEqualTo(GameItemType.DECONSTRUCTION);
        assertThat(result.getExternalReference()).isEqualTo(EXTERNAL_REFERENCE);
        assertThat(result.getCurrentWorkPoints()).isEqualTo(CURRENT_WORK_POINTS);
        assertThat(result.getPriority()).isEqualTo(PRIORITY);
    }
}