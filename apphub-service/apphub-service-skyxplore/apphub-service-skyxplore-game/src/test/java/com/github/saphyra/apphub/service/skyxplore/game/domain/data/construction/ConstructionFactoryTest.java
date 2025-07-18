package com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameConstants;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class ConstructionFactoryTest {
    private static final UUID CONSTRUCTION_ID = UUID.randomUUID();
    private static final UUID EXTERNAL_REFERENCE = UUID.randomUUID();
    private static final Integer REQUIRED_WORK_POINTS = 4567;
    private static final UUID LOCATION = UUID.randomUUID();

    @Mock
    private IdGenerator idGenerator;

    @InjectMocks
    private ConstructionFactory underTest;

    @Test
    public void create() {
        given(idGenerator.randomUuid()).willReturn(CONSTRUCTION_ID);

        Construction result = underTest.create(EXTERNAL_REFERENCE,  LOCATION, REQUIRED_WORK_POINTS);

        assertThat(result.getConstructionId()).isEqualTo(CONSTRUCTION_ID);
        assertThat(result.getLocation()).isEqualTo(LOCATION);
        assertThat(result.getExternalReference()).isEqualTo(EXTERNAL_REFERENCE);
        assertThat(result.getRequiredWorkPoints()).isEqualTo(REQUIRED_WORK_POINTS);
        assertThat(result.getPriority()).isEqualTo(GameConstants.DEFAULT_PRIORITY);
    }
}