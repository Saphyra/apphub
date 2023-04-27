package com.github.saphyra.apphub.service.skyxplore.game.service.common.factory;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameConstants;
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
class DeconstructionFactoryTest {
    private static final UUID DECONSTRUCTION_ID = UUID.randomUUID();
    private static final UUID EXTERNAL_REFERENCE = UUID.randomUUID();
    private static final UUID LOCATION = UUID.randomUUID();

    @Mock
    private IdGenerator idGenerator;

    @InjectMocks
    private DeconstructionFactory underTest;

    @Test
    void create() {
        given(idGenerator.randomUuid()).willReturn(DECONSTRUCTION_ID);

        Deconstruction result = underTest.create(EXTERNAL_REFERENCE, LOCATION);

        assertThat(result.getDeconstructionId()).isEqualTo(DECONSTRUCTION_ID);
        assertThat(result.getExternalReference()).isEqualTo(EXTERNAL_REFERENCE);
        assertThat(result.getLocation()).isEqualTo(LOCATION);
        assertThat(result.getPriority()).isEqualTo(GameConstants.DEFAULT_PRIORITY);
        assertThat(result.getCurrentWorkPoints()).isEqualTo(0);
    }
}