package com.github.saphyra.apphub.service.skyxplore.game.domain.data.deconstruction;

import com.github.saphyra.apphub.api.skyxplore.model.game.DeconstructionModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.overview.surface.DeconstructionResponse;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.DeconstructionProperties;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.GameProperties;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.util.WorkPointsUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class DeconstructionConverterTest {
    private static final UUID DECONSTRUCTION_ID = UUID.randomUUID();
    private static final UUID EXTERNAL_REFERENCE = UUID.randomUUID();
    private static final UUID LOCATION = UUID.randomUUID();
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final int CURRENT_WORK_POINTS = 352;
    private static final int PRIORITY = 356;
    private static final Integer REQUIRED_WORK_POINTS = 234;

    @Mock
    private GameProperties gameProperties;

    @Mock
    private WorkPointsUtil workPointsUtil;

    @InjectMocks
    private DeconstructionConverter underTest;

    @Mock
    private DeconstructionProperties deconstructionProperties;

    @Mock
    private GameData gameData;

    @Test
    void toModel() {
        Deconstruction deconstruction = Deconstruction.builder()
            .deconstructionId(DECONSTRUCTION_ID)
            .externalReference(EXTERNAL_REFERENCE)
            .location(LOCATION)
            .priority(PRIORITY)
            .build();

        DeconstructionModel result = underTest.toModel(GAME_ID, deconstruction);

        assertThat(result.getId()).isEqualTo(DECONSTRUCTION_ID);
        assertThat(result.getGameId()).isEqualTo(GAME_ID);
        assertThat(result.getType()).isEqualTo(GameItemType.DECONSTRUCTION);
        assertThat(result.getExternalReference()).isEqualTo(EXTERNAL_REFERENCE);
        assertThat(result.getLocation()).isEqualTo(LOCATION);
        assertThat(result.getPriority()).isEqualTo(PRIORITY);
    }

    @Test
    void toResponse() {
        Deconstruction deconstruction = Deconstruction.builder()
            .deconstructionId(DECONSTRUCTION_ID)
            .externalReference(EXTERNAL_REFERENCE)
            .location(LOCATION)
            .priority(PRIORITY)
            .build();

        given(gameProperties.getDeconstruction()).willReturn(deconstructionProperties);
        given(deconstructionProperties.getRequiredWorkPoints()).willReturn(REQUIRED_WORK_POINTS);
        given(workPointsUtil.getCompletedWorkPoints(gameData, DECONSTRUCTION_ID, ProcessType.DECONSTRUCT_BUILDING_MODULE)).willReturn(CURRENT_WORK_POINTS);

        DeconstructionResponse result = underTest.toResponse(gameData, deconstruction, ProcessType.DECONSTRUCT_BUILDING_MODULE);

        assertThat(result.getDeconstructionId()).isEqualTo(DECONSTRUCTION_ID);
        assertThat(result.getRequiredWorkPoints()).isEqualTo(REQUIRED_WORK_POINTS);
        assertThat(result.getCurrentWorkPoints()).isEqualTo(CURRENT_WORK_POINTS);
    }
}