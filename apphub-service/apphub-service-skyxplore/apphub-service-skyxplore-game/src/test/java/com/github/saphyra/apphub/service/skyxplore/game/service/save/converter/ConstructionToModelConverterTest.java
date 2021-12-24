package com.github.saphyra.apphub.service.skyxplore.game.service.save.converter;

import com.github.saphyra.apphub.api.skyxplore.model.game.ConstructionModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.LocationType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Construction;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class ConstructionToModelConverterTest {
    private static final UUID CONSTRUCTION_ID = UUID.randomUUID();
    private static final UUID LOCATION = UUID.randomUUID();
    private static final int REQUIRED_WORK_POINTS = 4253;
    private static final int CURRENT_WORK_POINTS = 56;
    private static final int PRIORITY = 12;
    private static final UUID GAME_ID = UUID.randomUUID();

    @InjectMocks
    private ConstructionToModelConverter underTest;

    @Test
    public void convert() {
        Construction construction = Construction.builder()
            .constructionId(CONSTRUCTION_ID)
            .location(LOCATION)
            .locationType(LocationType.BUILDING)
            .requiredWorkPoints(REQUIRED_WORK_POINTS)
            .currentWorkPoints(CURRENT_WORK_POINTS)
            .priority(PRIORITY)
            .build();

        ConstructionModel result = underTest.convert(construction, GAME_ID);

        assertThat(result.getId()).isEqualTo(CONSTRUCTION_ID);
        assertThat(result.getGameId()).isEqualTo(GAME_ID);
        assertThat(result.getType()).isEqualTo(GameItemType.CONSTRUCTION);
        assertThat(result.getLocation()).isEqualTo(LOCATION);
        assertThat(result.getLocationType()).isEqualTo(LocationType.BUILDING.name());
        assertThat(result.getRequiredWorkPoints()).isEqualTo(REQUIRED_WORK_POINTS);
        assertThat(result.getCurrentWorkPoints()).isEqualTo(CURRENT_WORK_POINTS);
        assertThat(result.getPriority()).isEqualTo(PRIORITY);
    }
}