package com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface;

import com.github.saphyra.apphub.api.skyxplore.response.game.planet.ConstructionResponse;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Construction;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class ConstructionToResponseConverterTest {
    private static final UUID CONSTRUCTION_ID = UUID.randomUUID();
    private static final int REQUIRED_WORK_POINTS = 235;
    private static final int CURRENT_WORK_POINTS = 657;

    @InjectMocks
    private ConstructionToResponseConverter underTest;

    @Test
    public void convert() {
        Construction construction = Construction.builder()
            .constructionId(CONSTRUCTION_ID)
            .requiredWorkPoints(REQUIRED_WORK_POINTS)
            .currentWorkPoints(CURRENT_WORK_POINTS)
            .build();

        ConstructionResponse result = underTest.convert(construction);

        assertThat(result.getConstructionId()).isEqualTo(CONSTRUCTION_ID);
        assertThat(result.getRequiredWorkPoints()).isEqualTo(REQUIRED_WORK_POINTS);
        assertThat(result.getCurrentWorkPoints()).isEqualTo(CURRENT_WORK_POINTS);
    }
}