package com.github.saphyra.apphub.service.skyxplore.lobby.service.settings;

import com.github.saphyra.apphub.api.skyxplore.model.SkyXploreGameSettings;
import com.github.saphyra.apphub.lib.common_domain.WebSocketEventName;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Lobby;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.LobbyDao;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.LobbyMember;
import com.github.saphyra.apphub.service.skyxplore.lobby.ws.SkyXploreLobbyWebSocketHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EditSettingsServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();

    @Mock
    private LobbyDao lobbyDao;

    @Mock
    private SkyXploreGameSettingsValidator settingsValidator;

    @Mock
    private SkyXploreLobbyWebSocketHandler lobbyWebSocketHandler;

    @InjectMocks
    private EditSettingsService underTest;

    @Mock
    private SkyXploreGameSettings settings;

    @Mock
    private Lobby lobby;

    @Test
    void editSettings() {
        given(lobbyDao.findByHostValidated(USER_ID)).willReturn(lobby);
        Map<UUID, LobbyMember> lobbyMembers = CollectionUtils.singleValueMap(USER_ID, null);
        given(lobby.getMembers()).willReturn(lobbyMembers);

        underTest.editSettings(USER_ID, settings);

        verify(settingsValidator).validate(settings);
        verify(lobby).setSettings(settings);

        then(lobbyWebSocketHandler).should().sendEvent(lobbyMembers.keySet(), WebSocketEventName.SKYXPLORE_LOBBY_SETTINGS_MODIFIED, settings);
    }
}