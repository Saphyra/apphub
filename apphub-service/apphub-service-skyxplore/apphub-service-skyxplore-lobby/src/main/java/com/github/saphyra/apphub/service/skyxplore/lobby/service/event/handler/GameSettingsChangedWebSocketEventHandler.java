package com.github.saphyra.apphub.service.skyxplore.lobby.service.event.handler;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEvent;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketMessage;
import com.github.saphyra.apphub.api.skyxplore.model.game_setting.AiPresence;
import com.github.saphyra.apphub.api.skyxplore.model.game_setting.PlanetSize;
import com.github.saphyra.apphub.api.skyxplore.model.game_setting.SystemAmount;
import com.github.saphyra.apphub.api.skyxplore.model.game_setting.SystemSize;
import com.github.saphyra.apphub.api.skyxplore.model.game_setting.UniverseSize;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.GameSettings;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Lobby;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.LobbyDao;
import com.github.saphyra.apphub.service.skyxplore.lobby.proxy.MessageSenderProxy;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static java.util.Objects.isNull;


@Component
@RequiredArgsConstructor
@Slf4j
class GameSettingsChangedWebSocketEventHandler implements WebSocketEventHandler {
    private final ObjectMapperWrapper objectMapperWrapper;
    private final LobbyDao lobbyDao;
    private final MessageSenderProxy messageSenderProxy;

    @Override
    public boolean canHandle(WebSocketEventName eventName) {
        return WebSocketEventName.SKYXPLORE_LOBBY_GAME_SETTINGS_CHANGED == eventName;
    }

    @Override
    public void handle(UUID from, WebSocketEvent event) {
        log.info("{} changes gameSettings: {}", from, event.getPayload());

        GameSettingsChangedEvent gameSettingsChangedEvent = objectMapperWrapper.convertValue(event.getPayload(), GameSettingsChangedEvent.class);
        log.info("Converted payload: {}", gameSettingsChangedEvent);

        Lobby lobby = lobbyDao.findByUserIdValidated(from);

        if (!lobby.getHost().equals(from) || !gameSettingsChangedEvent.isFilled()) {
            GameSettings settings = lobby.getSettings();

            GameSettingsChangedEvent fwEvent = GameSettingsChangedEvent.builder()
                .universeSize(settings.getUniverseSize())
                .systemAmount(settings.getSystemAmount())
                .systemSize(settings.getSystemSize())
                .planetSize(settings.getPlanetSize())
                .aiPresence(settings.getAiPresence())
                .build();
            sendEvent(fwEvent, lobby);
            throw ExceptionFactory.notLoggedException(HttpStatus.FORBIDDEN, ErrorCode.FORBIDDEN_OPERATION, from + " must not change the game settings.");
        }

        GameSettings settings = lobby.getSettings();
        settings.setUniverseSize(gameSettingsChangedEvent.getUniverseSize());
        settings.setSystemAmount(gameSettingsChangedEvent.getSystemAmount());
        settings.setSystemSize(gameSettingsChangedEvent.getSystemSize());
        settings.setPlanetSize(gameSettingsChangedEvent.getPlanetSize());
        settings.setAiPresence(gameSettingsChangedEvent.getAiPresence());

        sendEvent(gameSettingsChangedEvent, lobby);
    }

    private void sendEvent(GameSettingsChangedEvent gameSettingsChangedEvent, Lobby lobby) {
        WebSocketEvent fwEvent = WebSocketEvent.builder()
            .eventName(WebSocketEventName.SKYXPLORE_LOBBY_GAME_SETTINGS_CHANGED)
            .payload(gameSettingsChangedEvent)
            .build();

        WebSocketMessage message = WebSocketMessage.builder()
            .recipients(lobby.getMembers().keySet())
            .event(fwEvent)
            .build();
        messageSenderProxy.sendToLobby(message);
    }

    @NoArgsConstructor
    @Data
    @AllArgsConstructor
    @Builder
    static class GameSettingsChangedEvent {
        private UniverseSize universeSize;
        private SystemAmount systemAmount;
        private SystemSize systemSize;
        private PlanetSize planetSize;
        private AiPresence aiPresence;

        public boolean isFilled() {
            return !isNull(universeSize)
                && !isNull(systemAmount)
                && !isNull(systemSize)
                && !isNull(planetSize)
                && !isNull(aiPresence);
        }
    }
}
