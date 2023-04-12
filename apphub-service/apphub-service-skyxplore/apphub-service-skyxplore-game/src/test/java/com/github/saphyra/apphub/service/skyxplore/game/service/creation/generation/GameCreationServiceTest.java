package com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketMessage;
import com.github.saphyra.apphub.api.skyxplore.request.game_creation.SkyXploreGameCreationRequest;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBeenTestUtils;
import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.proxy.MessageSenderProxy;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory.GameFactory;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.tick.TickSchedulerLauncher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class GameCreationServiceTest {
    private static final UUID PLAYER_ID = UUID.randomUUID();

    @Mock
    private MessageSenderProxy messageSenderProxy;

    @Mock
    private GameFactory gameFactory;

    @Mock
    private GameDao gameDao;

    @Mock
    private GameSaverService gameSaverService;

    @Mock
    private TickSchedulerLauncher tickSchedulerLauncher;

    private final BlockingQueue<SkyXploreGameCreationRequest> requests = new ArrayBlockingQueue<>(1);

    @Mock
    private ErrorReporterService errorReporterService;

    private GameCreationService underTest;

    @Mock
    private SkyXploreGameCreationRequest request;

    @Mock
    private Game game;

    @BeforeEach
    public void setUp() {
        underTest = GameCreationService.builder()
            .messageSenderProxy(messageSenderProxy)
            .gameFactory(gameFactory)
            .gameDao(gameDao)
            .requests(requests)
            .gameSaverService(gameSaverService)
            .executorServiceBeanFactory(ExecutorServiceBeenTestUtils.createFactory(Mockito.mock(ErrorReporterService.class)))
            .errorReporterService(errorReporterService)
            .tickSchedulerLauncher(tickSchedulerLauncher)
            .build();
    }

    @Test
    public void create() throws InterruptedException {
        given(gameFactory.create(request)).willReturn(game);
        given(request.getMembers()).willReturn(CollectionUtils.singleValueMap(PLAYER_ID, null));

        underTest.createGames();

        requests.put(request);

        verify(gameDao, timeout(1000)).save(game);
        ArgumentCaptor<WebSocketMessage> argumentCaptor = ArgumentCaptor.forClass(WebSocketMessage.class);
        verify(messageSenderProxy).sendToLobby(argumentCaptor.capture());
        WebSocketMessage message = argumentCaptor.getValue();
        assertThat(message.getRecipients()).containsExactly(PLAYER_ID);
        assertThat(message.getEvent().getEventName()).isEqualTo(WebSocketEventName.SKYXPLORE_LOBBY_GAME_LOADED);
        verify(gameSaverService).save(game);
        verify(tickSchedulerLauncher).launch(game);
    }
}