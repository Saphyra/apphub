package com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue.service.terraformation;

import com.github.saphyra.apphub.api.skyxplore.model.game.ConstructionModel;
import com.github.saphyra.apphub.lib.concurrency.ExecutionResult;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.GameProgressDiff;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Construction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.ConstructionConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Constructions;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.event_loop.EventLoop;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class TerraformationQueueItemPriorityUpdateServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID CONSTRUCTION_ID = UUID.randomUUID();
    private static final Integer PRIORITY = 5;
    private static final UUID GAME_ID = UUID.randomUUID();

    @Mock
    private GameDao gameDao;

    @Mock
    private ConstructionConverter constructionConverter;

    @InjectMocks
    private TerraformationQueueItemPriorityUpdateService underTest;

    @Mock
    private Game game;

    @Mock
    private GameData gameData;

    @Mock
    private Construction terraformation;

    @Mock
    private EventLoop eventLoop;

    @Mock
    private ExecutionResult<Void> executionResult;

    @Mock
    private Constructions constructions;

    @Mock
    private GameProgressDiff progressDiff;

    @Mock
    private ConstructionModel constructionModel;

    @Test
    public void updatePriority() {
        given(gameDao.findByUserIdValidated(USER_ID)).willReturn(game);
        given(game.getData()).willReturn(gameData);
        given(gameData.getConstructions()).willReturn(constructions);
        given(constructions.findByIdValidated(CONSTRUCTION_ID)).willReturn(terraformation);

        given(game.getEventLoop()).willReturn(eventLoop);
        given(eventLoop.processWithWait(any())).willReturn(executionResult);
        given(game.getGameId()).willReturn(GAME_ID);
        given(constructionConverter.toModel(GAME_ID, terraformation)).willReturn(constructionModel);
        given(game.getProgressDiff()).willReturn(progressDiff);

        underTest.updatePriority(USER_ID, CONSTRUCTION_ID, PRIORITY);

        ArgumentCaptor<Runnable> argumentCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(eventLoop).processWithWait(argumentCaptor.capture());
        argumentCaptor.getValue()
            .run();

        verify(terraformation).setPriority(PRIORITY);
        then(progressDiff).should().save(constructionModel);
        verify(executionResult).getOrThrow();
    }
}