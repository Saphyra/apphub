package com.github.saphyra.apphub.service.skyxplore.lobby.service.disconnect;

import com.github.saphyra.apphub.api.skyxplore.model.SkyXploreCharacterModel;
import com.github.saphyra.apphub.lib.common_domain.WebSocketEventName;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Lobby;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.LobbyPlayer;
import com.github.saphyra.apphub.service.skyxplore.lobby.proxy.CharacterProxy;
import com.github.saphyra.apphub.service.skyxplore.lobby.ws.SkyXploreLobbyWebSocketHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class ExitMessageSenderTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String CHARACTER_NAME = "character-name";
    private static final Long CURRENT_TIME_EPOCH = 234L;

    @Mock
    private CharacterProxy characterProxy;

    @Mock
    private DateTimeUtil dateTimeUtil;

    @Mock
    private SkyXploreLobbyWebSocketHandler lobbyWebSocketHandler;

    @InjectMocks
    private ExitMessageSender underTest;

    @Mock
    private Lobby lobby;

    @Mock
    private SkyXploreCharacterModel characterModel;

    @Test
    void sendExitMessage() {
        given(characterProxy.getCharacter(USER_ID)).willReturn(characterModel);
        given(characterModel.getName()).willReturn(CHARACTER_NAME);
        given(dateTimeUtil.getCurrentTimeEpochMillis()).willReturn(CURRENT_TIME_EPOCH);
        Map<UUID, LobbyPlayer> players = CollectionUtils.singleValueMap(USER_ID, null);
        given(lobby.getPlayers()).willReturn(players);
        given(lobby.getHost()).willReturn(USER_ID);
        given(lobby.getExpectedPlayers()).willReturn(List.of(USER_ID));

        underTest.sendExitMessage(USER_ID, lobby, true);

        then(lobbyWebSocketHandler).should().sendEvent(
            players.keySet(),
            WebSocketEventName.SKYXPLORE_LOBBY_EXIT,
            ExitMessage.builder()
                .userId(USER_ID)
                .host(true)
                .characterName(CHARACTER_NAME)
                .expectedPlayer(true)
                .createdAt(CURRENT_TIME_EPOCH)
                .onlyInvited(true)
                .build()
        );
    }
}