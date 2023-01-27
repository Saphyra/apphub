package com.github.saphyra.apphub.service.skyxplore.game.service.save.converter;

import com.github.saphyra.apphub.api.skyxplore.model.game.ConstructionModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Construction;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class ConstructionToModelConverterTest {
    private static final UUID CONSTRUCTION_ID = UUID.randomUUID();
    private static final UUID EXTERNAL_REFERENCE = UUID.randomUUID();
    private static final int REQUIRED_WORK_POINTS = 4253;
    private static final int CURRENT_WORK_POINTS = 56;
    private static final int PRIORITY = 12;
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final String DATA = "data";
    private static final int PARALLEL_WORKERS = 345;

    @InjectMocks
    private ConstructionToModelConverter underTest;

    @Test
    public void convert() {
        Construction construction = Construction.builder()
            .constructionId(CONSTRUCTION_ID)
            .externalReference(EXTERNAL_REFERENCE)
            .parallelWorkers(PARALLEL_WORKERS)
            .requiredWorkPoints(REQUIRED_WORK_POINTS)
            .currentWorkPoints(CURRENT_WORK_POINTS)
            .priority(PRIORITY)
            .data(DATA)
            .build();

        ConstructionModel result = underTest.convert(construction, GAME_ID);

        assertThat(result.getId()).isEqualTo(CONSTRUCTION_ID);
        assertThat(result.getGameId()).isEqualTo(GAME_ID);
        assertThat(result.getType()).isEqualTo(GameItemType.CONSTRUCTION);
        assertThat(result.getExternalReference()).isEqualTo(EXTERNAL_REFERENCE);
        assertThat(result.getParallelWorkers()).isEqualTo(PARALLEL_WORKERS);
        assertThat(result.getRequiredWorkPoints()).isEqualTo(REQUIRED_WORK_POINTS);
        assertThat(result.getCurrentWorkPoints()).isEqualTo(CURRENT_WORK_POINTS);
        assertThat(result.getPriority()).isEqualTo(PRIORITY);
        assertThat(result.getData()).isEqualTo(DATA);
    }
}