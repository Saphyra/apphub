package com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface;

import com.github.saphyra.apphub.api.skyxplore.response.game.planet.DeconstructionResponse;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.DeconstructionProperties;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.GameProperties;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.deconstruction.Deconstruction;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class DeconstructionToResponseConverterTest {
    private static final UUID DECONSTRUCTION_ID = UUID.randomUUID();
    private static final int CURRENT_WORK_POINTS = 53642;
    private static final Integer REQUIRED_WORK_POINTS = 346;

    @Mock
    private GameProperties gameProperties;

    @InjectMocks
    private DeconstructionToResponseConverter underTest;

    @Mock
    private DeconstructionProperties deconstructionProperties;

    @Test
    void convert() {
        Deconstruction deconstruction = Deconstruction.builder()
            .deconstructionId(DECONSTRUCTION_ID)
            .currentWorkPoints(CURRENT_WORK_POINTS)
            .build();

        given(gameProperties.getDeconstruction()).willReturn(deconstructionProperties);
        given(deconstructionProperties.getRequiredWorkPoints()).willReturn(REQUIRED_WORK_POINTS);

        DeconstructionResponse result = underTest.convert(deconstruction);

        assertThat(result.getDeconstructionId()).isEqualTo(DECONSTRUCTION_ID);
        assertThat(result.getRequiredWorkPoints()).isEqualTo(REQUIRED_WORK_POINTS);
        assertThat(result.getCurrentWorkPoints()).isEqualTo(CURRENT_WORK_POINTS);
    }
}