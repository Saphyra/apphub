package com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction;

import com.github.saphyra.apphub.api.skyxplore.model.game.ConstructionModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.ConstructionType;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.ConstructionResponse;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ConstructionConverterTest {
    private static final UUID CONSTRUCTION_ID = UUID.randomUUID();
    private static final UUID EXTERNAL_REFERENCE = UUID.randomUUID();
    private static final UUID LOCATION = UUID.randomUUID();
    private static final Integer REQUIRED_WORK_POINTS = 4235;
    private static final String DATA = "data";
    private static final Integer CURRENT_WORK_POINTS = 324;
    private static final Integer PRIORITY = 45;
    private static final UUID GAME_ID = UUID.randomUUID();

    private final ConstructionConverter underTest = new ConstructionConverter();

    @Test
    void toModel() {
        Construction construction = Construction.builder()
            .constructionId(CONSTRUCTION_ID)
            .externalReference(EXTERNAL_REFERENCE)
            .constructionType(ConstructionType.CONSTRUCTION)
            .location(LOCATION)
            .requiredWorkPoints(REQUIRED_WORK_POINTS)
            .data(DATA)
            .currentWorkPoints(CURRENT_WORK_POINTS)
            .priority(PRIORITY)
            .build();

        ConstructionModel result = underTest.toModel(GAME_ID, construction);

        assertThat(result.getId()).isEqualTo(CONSTRUCTION_ID);
        assertThat(result.getGameId()).isEqualTo(GAME_ID);
        assertThat(result.getType()).isEqualTo(GameItemType.CONSTRUCTION);
        assertThat(result.getExternalReference()).isEqualTo(EXTERNAL_REFERENCE);
        assertThat(result.getConstructionType()).isEqualTo(ConstructionType.CONSTRUCTION);
        assertThat(result.getLocation()).isEqualTo(LOCATION);
        assertThat(result.getRequiredWorkPoints()).isEqualTo(REQUIRED_WORK_POINTS);
        assertThat(result.getData()).isEqualTo(DATA);
        assertThat(result.getCurrentWorkPoints()).isEqualTo(CURRENT_WORK_POINTS);
        assertThat(result.getPriority()).isEqualTo(PRIORITY);
    }

    @Test
    void toResponse() {
        Construction construction = Construction.builder()
            .constructionId(CONSTRUCTION_ID)
            .externalReference(EXTERNAL_REFERENCE)
            .constructionType(ConstructionType.CONSTRUCTION)
            .location(LOCATION)
            .requiredWorkPoints(REQUIRED_WORK_POINTS)
            .data(DATA)
            .currentWorkPoints(CURRENT_WORK_POINTS)
            .priority(PRIORITY)
            .build();

        ConstructionResponse result = underTest.toResponse(construction);

        assertThat(result.getConstructionId()).isEqualTo(CONSTRUCTION_ID);
        assertThat(result.getRequiredWorkPoints()).isEqualTo(REQUIRED_WORK_POINTS);
        assertThat(result.getCurrentWorkPoints()).isEqualTo(CURRENT_WORK_POINTS);
        assertThat(result.getData()).isEqualTo(DATA);
    }
}