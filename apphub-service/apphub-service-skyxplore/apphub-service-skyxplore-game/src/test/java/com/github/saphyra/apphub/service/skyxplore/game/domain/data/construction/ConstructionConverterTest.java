package com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction;

import com.github.saphyra.apphub.api.skyxplore.model.game.ConstructionModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.overview.surface.ConstructionResponse;
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
class ConstructionConverterTest {
    private static final UUID CONSTRUCTION_ID = UUID.randomUUID();
    private static final UUID EXTERNAL_REFERENCE = UUID.randomUUID();
    private static final UUID LOCATION = UUID.randomUUID();
    private static final Integer REQUIRED_WORK_POINTS = 4235;
    private static final String DATA = "data";
    private static final Integer CURRENT_WORK_POINTS = 324;
    private static final Integer PRIORITY = 45;
    private static final UUID GAME_ID = UUID.randomUUID();

    @Mock
    private WorkPointsUtil workPointsUtil;

    @InjectMocks
    private ConstructionConverter underTest;

    @Mock
    private GameData gameData;

    @Test
    void toModel() {
        Construction construction = Construction.builder()
            .constructionId(CONSTRUCTION_ID)
            .externalReference(EXTERNAL_REFERENCE)
            .location(LOCATION)
            .requiredWorkPoints(REQUIRED_WORK_POINTS)
            .data(DATA)
            .priority(PRIORITY)
            .build();

        ConstructionModel result = underTest.toModel(GAME_ID, construction);

        assertThat(result.getId()).isEqualTo(CONSTRUCTION_ID);
        assertThat(result.getGameId()).isEqualTo(GAME_ID);
        assertThat(result.getType()).isEqualTo(GameItemType.CONSTRUCTION);
        assertThat(result.getExternalReference()).isEqualTo(EXTERNAL_REFERENCE);
        assertThat(result.getLocation()).isEqualTo(LOCATION);
        assertThat(result.getRequiredWorkPoints()).isEqualTo(REQUIRED_WORK_POINTS);
        assertThat(result.getData()).isEqualTo(DATA);
        assertThat(result.getPriority()).isEqualTo(PRIORITY);
    }

    @Test
    void toResponse() {
        Construction construction = Construction.builder()
            .constructionId(CONSTRUCTION_ID)
            .externalReference(EXTERNAL_REFERENCE)
            .location(LOCATION)
            .requiredWorkPoints(REQUIRED_WORK_POINTS)
            .data(DATA)
            .priority(PRIORITY)
            .build();

        given(workPointsUtil.getCompletedWorkPoints(gameData, CONSTRUCTION_ID, ProcessType.CONSTRUCT_BUILDING_MODULE)).willReturn(CURRENT_WORK_POINTS);

        ConstructionResponse result = underTest.toResponse(gameData, construction, ProcessType.CONSTRUCT_BUILDING_MODULE);

        assertThat(result.getConstructionId()).isEqualTo(CONSTRUCTION_ID);
        assertThat(result.getRequiredWorkPoints()).isEqualTo(REQUIRED_WORK_POINTS);
        assertThat(result.getCurrentWorkPoints()).isEqualTo(CURRENT_WORK_POINTS);
        assertThat(result.getData()).isEqualTo(DATA);
    }
}