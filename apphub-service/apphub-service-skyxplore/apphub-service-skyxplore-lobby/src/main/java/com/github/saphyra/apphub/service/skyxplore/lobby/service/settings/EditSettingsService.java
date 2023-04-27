package com.github.saphyra.apphub.service.skyxplore.lobby.service.settings;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketMessage;
import com.github.saphyra.apphub.api.skyxplore.model.SkyXploreGameSettings;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Lobby;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.LobbyDao;
import com.github.saphyra.apphub.service.skyxplore.lobby.proxy.MessageSenderProxy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class EditSettingsService {
    private final LobbyDao lobbyDao;
    private final SkyXploreGameSettingsValidator settingsValidator;
    private final MessageSenderProxy messageSenderProxy;

    void editSettings(UUID userId, SkyXploreGameSettings settings) {
        settingsValidator.validate(settings);

        Lobby lobby = lobbyDao.findByHostValidated(userId);

        lobby.setSettings(settings);

        WebSocketMessage message = WebSocketMessage.forEventAndRecipients(
            WebSocketEventName.SKYXPLORE_LOBBY_SETTINGS_MODIFIED,
            lobby.getMembers().keySet(),
            settings
        );
        messageSenderProxy.sendToLobby(message);
    }
}
