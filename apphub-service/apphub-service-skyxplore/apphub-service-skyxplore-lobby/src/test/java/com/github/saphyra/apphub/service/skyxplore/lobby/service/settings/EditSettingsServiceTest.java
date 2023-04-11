package com.github.saphyra.apphub.service.skyxplore.lobby.service.settings;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketMessage;
import com.github.saphyra.apphub.api.skyxplore.model.SkyXploreGameSettings;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Lobby;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.LobbyDao;
import com.github.saphyra.apphub.service.skyxplore.lobby.proxy.MessageSenderProxy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EditSettingsServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();

    @Mock
    private LobbyDao lobbyDao;

    @Mock
    private SkyXploreGameSettingsValidator settingsValidator;

    @Mock
    private MessageSenderProxy messageSenderProxy;

    @InjectMocks
    private EditSettingsService underTest;

    @Mock
    private SkyXploreGameSettings settings;

    @Mock
    private Lobby lobby;

    @Test
    void editSettings() {
        given(lobbyDao.findByHostValidated(USER_ID)).willReturn(lobby);
        given(lobby.getMembers()).willReturn(CollectionUtils.singleValueMap(USER_ID, null));

        underTest.editSettings(USER_ID, settings);

        verify(settingsValidator).validate(settings);
        verify(lobby).setSettings(settings);

        ArgumentCaptor<WebSocketMessage> argumentCaptor = ArgumentCaptor.forClass(WebSocketMessage.class);
        verify(messageSenderProxy).sendToLobby(argumentCaptor.capture());
        WebSocketMessage message = argumentCaptor.getValue();
        assertThat(message.getEvent().getEventName()).isEqualTo(WebSocketEventName.SKYXPLORE_LOBBY_SETTINGS_MODIFIED);
        assertThat(message.getEvent().getPayload()).isEqualTo(settings);
        assertThat(message.getRecipients()).containsExactly(USER_ID);
    }
}