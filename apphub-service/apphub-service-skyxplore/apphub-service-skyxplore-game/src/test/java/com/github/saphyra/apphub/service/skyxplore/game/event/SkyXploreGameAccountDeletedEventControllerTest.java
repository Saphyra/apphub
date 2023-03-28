package com.github.saphyra.apphub.service.skyxplore.game.event;

import com.github.saphyra.apphub.api.platform.event_gateway.model.request.SendEventRequest;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEvent;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketMessage;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.config.common.Endpoints;
import com.github.saphyra.apphub.lib.event.DeleteAccountEvent;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Player;
import com.github.saphyra.apphub.service.skyxplore.game.proxy.MessageSenderProxy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
public class SkyXploreGameAccountDeletedEventControllerTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String USERNAME = "username";

    @Mock
    private GameDao gameDao;

    @Mock
    private MessageSenderProxy messageSenderProxy;

    @InjectMocks
    private SkyXploreGameAccountDeletedEventController underTest;

    @Mock
    private Game game;

    @Mock
    private Player player;

    @BeforeEach
    public void setUp() {
        given(gameDao.findByUserId(USER_ID)).willReturn(Optional.of(game));
    }

    @Test
    public void hostDeleted() {
        given(game.getHost()).willReturn(USER_ID);
        given(game.getConnectedPlayers()).willReturn(Arrays.asList(USER_ID));

        underTest.deleteAccountEvent(SendEventRequest.<DeleteAccountEvent>builder().payload(new DeleteAccountEvent(USER_ID)).build());

        ArgumentCaptor<WebSocketMessage> argumentCaptor = ArgumentCaptor.forClass(WebSocketMessage.class);
        verify(messageSenderProxy).sendToGame(argumentCaptor.capture());
        WebSocketMessage message = argumentCaptor.getValue();
        assertThat(message.getRecipients()).containsExactly(USER_ID);

        WebSocketEvent event = message.getEvent();
        assertThat(event.getEventName()).isEqualTo(WebSocketEventName.REDIRECT);
        assertThat(event.getPayload()).isEqualTo(Endpoints.SKYXPLORE_MAIN_MENU_PAGE);

        verify(gameDao).delete(game);
    }

    @Test
    public void memberDeleted() {
        given(game.getHost()).willReturn(UUID.randomUUID());
        given(game.getPlayers()).willReturn(CollectionUtils.singleValueMap(USER_ID, player));

        given(player.getPlayerName()).willReturn(USERNAME);

        underTest.deleteAccountEvent(SendEventRequest.<DeleteAccountEvent>builder().payload(new DeleteAccountEvent(USER_ID)).build());

        verify(player).setAi(true);
        verify(player).setConnected(true);
        verify(player).setPlayerName(USERNAME + " (AI)");
    }
}