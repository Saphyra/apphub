package com.github.saphyra.apphub.service.skyxplore.lobby.service.settings.alliance;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Alliance;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class AllianceFactoryTest {
    private static final UUID ALLIANCE_ID = UUID.randomUUID();
    private static final int CURRENT_NUMBER_OF_ALLIANCES = 34;

    @Mock
    private IdGenerator idGenerator;

    @InjectMocks
    private AllianceFactory underTest;

    @Test
    void create() {
        given(idGenerator.randomUuid()).willReturn(ALLIANCE_ID);

        Alliance result = underTest.create(CURRENT_NUMBER_OF_ALLIANCES);

        assertThat(result.getAllianceId()).isEqualTo(ALLIANCE_ID);
        assertThat(result.getAllianceName()).isEqualTo(String.valueOf(CURRENT_NUMBER_OF_ALLIANCES + 1));
    }
}