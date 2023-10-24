package com.github.saphyra.apphub.service.skyxplore.game.domain.data.processes;

import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessModel;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.Process;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ProcessConverterTest {
    private static final UUID GAME_ID = UUID.randomUUID();

    @InjectMocks
    private ProcessConverter underTest;

    @Mock
    private GameData gameData;

    @Mock
    private Process process;

    @Mock
    private ProcessModel model;

    @Test
    void convert() {
        given(gameData.getProcesses()).willReturn(CollectionUtils.toList(new Processes(), process));
        given(process.toModel()).willReturn(model);

        assertThat(underTest.convert(GAME_ID, gameData)).containsExactly(model);
    }
}