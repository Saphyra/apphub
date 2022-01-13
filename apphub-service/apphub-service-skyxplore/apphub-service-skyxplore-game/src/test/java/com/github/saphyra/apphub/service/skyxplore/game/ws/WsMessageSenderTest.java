package com.github.saphyra.apphub.service.skyxplore.game.ws;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketMessage;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.PlanetStorageResponse;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.QueueResponse;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.SurfaceResponse;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.OpenedPage;
import com.github.saphyra.apphub.service.skyxplore.game.domain.OpenedPageType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Player;
import com.github.saphyra.apphub.service.skyxplore.game.proxy.MessageSenderProxy;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class WsMessageSenderTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID PLANET_ID = UUID.randomUUID();
    private static final UUID ITEM_ID = UUID.randomUUID();

    @Mock
    private GameDao gameDao;

    @Mock
    private MessageSenderProxy messageSenderProxy;

    @InjectMocks
    private WsMessageSender underTest;

    @Mock
    private QueueResponse queueResponse;

    @Mock
    private Game game;

    @Mock
    private Player player;

    @Mock
    private OpenedPage openedPage;

    @Mock
    private SurfaceResponse surfaceResponse;

    @Mock
    private PlanetStorageResponse storageResponse;

    @Before
    public void setUp() {
        given(gameDao.findByUserIdValidated(USER_ID)).willReturn(game);
        given(game.getPlayers()).willReturn(CollectionUtils.singleValueMap(USER_ID, player));
        given(player.getOpenedPage()).willReturn(openedPage);
    }

    @Test
    public void planetQueueItemModified() {
        given(openedPage.getPageType()).willReturn(OpenedPageType.PLANET);
        given(openedPage.getPageId()).willReturn(PLANET_ID);

        underTest.planetQueueItemModified(USER_ID, PLANET_ID, queueResponse);

        ArgumentCaptor<WebSocketMessage> argumentCaptor = ArgumentCaptor.forClass(WebSocketMessage.class);
        verify(messageSenderProxy).sendToGame(argumentCaptor.capture());
        verifyMessageSent(argumentCaptor.getValue(), WebSocketEventName.SKYXPLORE_GAME_PLANET_QUEUE_ITEM_MODIFIED, queueResponse);
    }

    @Test
    public void planetQueueItemDeleted() {
        given(openedPage.getPageType()).willReturn(OpenedPageType.PLANET);
        given(openedPage.getPageId()).willReturn(PLANET_ID);

        underTest.planetQueueItemDeleted(USER_ID, PLANET_ID, ITEM_ID);

        ArgumentCaptor<WebSocketMessage> argumentCaptor = ArgumentCaptor.forClass(WebSocketMessage.class);
        verify(messageSenderProxy).sendToGame(argumentCaptor.capture());
        verifyMessageSent(argumentCaptor.getValue(), WebSocketEventName.SKYXPLORE_GAME_PLANET_QUEUE_ITEM_DELETED, ITEM_ID);
    }

    @Test
    public void planetSurfaceModified() {
        given(openedPage.getPageType()).willReturn(OpenedPageType.PLANET);
        given(openedPage.getPageId()).willReturn(PLANET_ID);

        underTest.planetSurfaceModified(USER_ID, PLANET_ID, surfaceResponse);

        ArgumentCaptor<WebSocketMessage> argumentCaptor = ArgumentCaptor.forClass(WebSocketMessage.class);
        verify(messageSenderProxy).sendToGame(argumentCaptor.capture());
        verifyMessageSent(argumentCaptor.getValue(), WebSocketEventName.SKYXPLORE_GAME_PLANET_SURFACE_MODIFIED, surfaceResponse);
    }

    @Test
    public void planetStorageModified() {
        given(openedPage.getPageType()).willReturn(OpenedPageType.PLANET);
        given(openedPage.getPageId()).willReturn(PLANET_ID);

        underTest.planetStorageModified(USER_ID, PLANET_ID, storageResponse);

        ArgumentCaptor<WebSocketMessage> argumentCaptor = ArgumentCaptor.forClass(WebSocketMessage.class);
        verify(messageSenderProxy).sendToGame(argumentCaptor.capture());
        verifyMessageSent(argumentCaptor.getValue(), WebSocketEventName.SKYXPLORE_GAME_PLANET_STORAGE_MODIFIED, storageResponse);
    }

    private void verifyMessageSent(WebSocketMessage webSocketMessage, WebSocketEventName eventName, Object payload) {
        assertThat(webSocketMessage.getRecipients()).containsExactly(USER_ID);
        assertThat(webSocketMessage.getEvent().getEventName()).isEqualTo(eventName);
        assertThat(webSocketMessage.getEvent().getPayload()).isEqualTo(payload);
    }
}