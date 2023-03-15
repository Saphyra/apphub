package com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface;

import com.github.saphyra.apphub.api.skyxplore.response.game.planet.ConstructionResponse;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Construction;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class ConstructionToResponseConverterTest {
    private static final UUID CONSTRUCTION_ID = UUID.randomUUID();
    private static final int REQUIRED_WORK_POINTS = 235;
    private static final int CURRENT_WORK_POINTS = 657;
    private static final String DATA = "data";

    @InjectMocks
    private ConstructionToResponseConverter underTest;

    @Test
    public void convert() {
        Construction construction = Construction.builder()
            .constructionId(CONSTRUCTION_ID)
            .requiredWorkPoints(REQUIRED_WORK_POINTS)
            .currentWorkPoints(CURRENT_WORK_POINTS)
            .data(DATA)
            .build();

        ConstructionResponse result = underTest.convert(construction);

        assertThat(result.getConstructionId()).isEqualTo(CONSTRUCTION_ID);
        assertThat(result.getRequiredWorkPoints()).isEqualTo(REQUIRED_WORK_POINTS);
        assertThat(result.getCurrentWorkPoints()).isEqualTo(CURRENT_WORK_POINTS);
        assertThat(result.getData()).isEqualTo(DATA);
    }
}