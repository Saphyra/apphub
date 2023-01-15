package com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.loader;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessStatus;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.process.Process;
import com.github.saphyra.apphub.service.skyxplore.game.process.ProcessFactory;
import com.github.saphyra.apphub.service.skyxplore.game.proxy.GameDataProxy;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.GameItemLoader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ProcessLoaderTest {
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID PROCESS_ID = UUID.randomUUID();

    @Mock
    private GameItemLoader gameItemLoader;

    @Mock
    private ProcessFactory processFactory;

    @Mock
    private GameDataProxy gameDataProxy;

    private ProcessLoader underTest;

    @Mock
    private Game game;

    @Mock
    private ProcessModel readyToDeleteProcessModel;

    @Mock
    private ProcessModel processModel;

    @Mock
    private Process process;

    @BeforeEach
    public void setUp() {
        given(processFactory.getType()).willReturn(ProcessType.PRODUCTION_ORDER);

        underTest = ProcessLoader.builder()
            .gameItemLoader(gameItemLoader)
            .gameDataProxy(gameDataProxy)
            .factories(List.of(processFactory))
            .build();
    }

    @Test
    public void load() {
        given(game.getGameId()).willReturn(GAME_ID);
        given(gameItemLoader.loadChildren(GAME_ID, GameItemType.PROCESS, ProcessModel[].class)).willReturn(List.of(readyToDeleteProcessModel, processModel));

        given(readyToDeleteProcessModel.getStatus()).willReturn(ProcessStatus.READY_TO_DELETE);
        given(readyToDeleteProcessModel.getId()).willReturn(PROCESS_ID);
        given(processModel.getStatus()).willReturn(ProcessStatus.IN_PROGRESS);
        given(processModel.getProcessType()).willReturn(ProcessType.PRODUCTION_ORDER);
        given(processFactory.createFromModel(game, processModel)).willReturn(process);

        List<Process> result = underTest.load(game);

        assertThat(result).containsExactly(process);

        verify(gameDataProxy).deleteItem(PROCESS_ID, GameItemType.PROCESS);
    }
}