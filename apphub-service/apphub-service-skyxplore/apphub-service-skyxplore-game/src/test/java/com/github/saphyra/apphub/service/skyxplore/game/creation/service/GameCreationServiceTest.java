package com.github.saphyra.apphub.service.skyxplore.game.creation.service;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketMessage;
import com.github.saphyra.apphub.api.skyxplore.request.game_creation.SkyXploreGameCreationRequest;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.creation.service.factory.GameFactory;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.proxy.MessageSenderProxy;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class GameCreationServiceTest {
    private static final UUID PLAYER_ID = UUID.randomUUID();

    @Mock
    private MessageSenderProxy messageSenderProxy;

    @Mock
    private GameFactory gameFactory;

    @Mock
    private GameDao gameDao;

    private final BlockingQueue<SkyXploreGameCreationRequest> requests = new ArrayBlockingQueue<>(1);

    private GameCreationService underTest;

    @Mock
    private SkyXploreGameCreationRequest request;

    @Mock
    private Game game;

    @Before
    public void setUp() {
        underTest = GameCreationService.builder()
            .messageSenderProxy(messageSenderProxy)
            .gameFactory(gameFactory)
            .gameDao(gameDao)
            .requests(requests)
            .build();
    }

    @Test
    public void create() throws InterruptedException {
        given(gameFactory.create(request)).willReturn(game);
        given(request.getMembers()).willReturn(CollectionUtils.singleValueMap(PLAYER_ID, null));

        underTest.createGames();

        requests.put(request);

        Thread.sleep(1000);

        verify(gameDao).save(game);
        ArgumentCaptor<WebSocketMessage> argumentCaptor = ArgumentCaptor.forClass(WebSocketMessage.class);
        verify(messageSenderProxy).sendToLobby(argumentCaptor.capture());
        WebSocketMessage message = argumentCaptor.getValue();
        assertThat(message.getRecipients()).containsExactly(PLAYER_ID);
        assertThat(message.getEvent().getEventName()).isEqualTo(WebSocketEventName.SKYXPLORE_LOBBY_GAME_LOADED);
    }
}