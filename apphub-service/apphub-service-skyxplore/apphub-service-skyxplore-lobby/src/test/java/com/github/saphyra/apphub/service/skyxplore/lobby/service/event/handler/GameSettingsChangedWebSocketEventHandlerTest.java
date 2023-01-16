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
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.GameSettings;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Lobby;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.LobbyDao;
import com.github.saphyra.apphub.service.skyxplore.lobby.proxy.MessageSenderProxy;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class GameSettingsChangedWebSocketEventHandlerTest {
    private static final UUID FROM = UUID.randomUUID();
    private static final Object PAYLOAD = "payload";
    private static final UUID MEMBER_ID = UUID.randomUUID();

    @Mock
    private ObjectMapperWrapper objectMapperWrapper;

    @Mock
    private LobbyDao lobbyDao;

    @Mock
    private MessageSenderProxy messageSenderProxy;

    @InjectMocks
    private GameSettingsChangedWebSocketEventHandler underTest;

    @Mock
    private Lobby lobby;

    @Mock
    private GameSettings gameSettings;

    @Mock
    private WebSocketEvent event;

    @Mock
    private GameSettingsChangedWebSocketEventHandler.GameSettingsChangedEvent payload;


    @Test
    public void canHandle_settingsChangedEvent() {
        assertThat(underTest.canHandle(WebSocketEventName.SKYXPLORE_LOBBY_GAME_SETTINGS_CHANGED)).isTrue();
    }

    @Test
    public void canHandle_otherEvent() {
        assertThat(underTest.canHandle(WebSocketEventName.SKYXPLORE_GAME_USER_LEFT)).isFalse();
    }

    @Test
    public void forbiddenOperation() {
        given(lobbyDao.findByUserIdValidated(any())).willReturn(lobby);
        given(lobby.getHost()).willReturn(FROM);
        given(lobby.getSettings()).willReturn(gameSettings);
        given(event.getPayload()).willReturn(PAYLOAD);
        given(objectMapperWrapper.convertValue(PAYLOAD, GameSettingsChangedWebSocketEventHandler.GameSettingsChangedEvent.class)).willReturn(payload);
        given(lobby.getMembers()).willReturn(CollectionUtils.singleValueMap(MEMBER_ID, null));

        given(gameSettings.getUniverseSize()).willReturn(UniverseSize.LARGE);
        given(gameSettings.getSystemAmount()).willReturn(SystemAmount.COMMON);
        given(gameSettings.getSystemSize()).willReturn(SystemSize.LARGE);
        given(gameSettings.getPlanetSize()).willReturn(PlanetSize.LARGE);
        given(gameSettings.getAiPresence()).willReturn(AiPresence.COMMON);

        Throwable ex = catchThrowable(() -> underTest.handle(UUID.randomUUID(), event));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.FORBIDDEN, ErrorCode.FORBIDDEN_OPERATION);

        GameSettingsChangedWebSocketEventHandler.GameSettingsChangedEvent fwEvent = GameSettingsChangedWebSocketEventHandler.GameSettingsChangedEvent.builder()
            .universeSize(gameSettings.getUniverseSize())
            .systemAmount(gameSettings.getSystemAmount())
            .systemSize(gameSettings.getSystemSize())
            .planetSize(gameSettings.getPlanetSize())
            .aiPresence(gameSettings.getAiPresence())
            .build();
        verifyMessageSent(fwEvent);
    }

    @Test
    public void notFilled() {
        given(lobbyDao.findByUserIdValidated(any())).willReturn(lobby);
        given(lobby.getHost()).willReturn(FROM);
        given(lobby.getSettings()).willReturn(gameSettings);
        given(event.getPayload()).willReturn(PAYLOAD);
        given(objectMapperWrapper.convertValue(PAYLOAD, GameSettingsChangedWebSocketEventHandler.GameSettingsChangedEvent.class)).willReturn(payload);
        given(lobby.getMembers()).willReturn(CollectionUtils.singleValueMap(MEMBER_ID, null));

        given(gameSettings.getUniverseSize()).willReturn(UniverseSize.LARGE);
        given(gameSettings.getSystemAmount()).willReturn(SystemAmount.COMMON);
        given(gameSettings.getSystemSize()).willReturn(SystemSize.LARGE);
        given(gameSettings.getPlanetSize()).willReturn(PlanetSize.LARGE);
        given(gameSettings.getAiPresence()).willReturn(AiPresence.COMMON);
        given(payload.isFilled()).willReturn(false);

        Throwable ex = catchThrowable(() -> underTest.handle(FROM, event));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.FORBIDDEN, ErrorCode.FORBIDDEN_OPERATION);

        GameSettingsChangedWebSocketEventHandler.GameSettingsChangedEvent fwEvent = GameSettingsChangedWebSocketEventHandler.GameSettingsChangedEvent.builder()
            .universeSize(gameSettings.getUniverseSize())
            .systemAmount(gameSettings.getSystemAmount())
            .systemSize(gameSettings.getSystemSize())
            .planetSize(gameSettings.getPlanetSize())
            .aiPresence(gameSettings.getAiPresence())
            .build();
        verifyMessageSent(fwEvent);
    }

    @Test
    public void changeGameSettings() {
        given(lobbyDao.findByUserIdValidated(any())).willReturn(lobby);
        given(lobby.getHost()).willReturn(FROM);
        given(lobby.getSettings()).willReturn(gameSettings);
        given(event.getPayload()).willReturn(PAYLOAD);
        given(objectMapperWrapper.convertValue(PAYLOAD, GameSettingsChangedWebSocketEventHandler.GameSettingsChangedEvent.class)).willReturn(payload);
        given(lobby.getMembers()).willReturn(CollectionUtils.singleValueMap(MEMBER_ID, null));

        given(payload.isFilled()).willReturn(true);

        underTest.handle(FROM, event);

        verify(gameSettings).setUniverseSize(payload.getUniverseSize());
        verify(gameSettings).setSystemAmount(payload.getSystemAmount());
        verify(gameSettings).setSystemSize(payload.getSystemSize());
        verify(gameSettings).setPlanetSize(payload.getPlanetSize());
        verify(gameSettings).setAiPresence(payload.getAiPresence());

        verifyMessageSent(payload);
    }

    private void verifyMessageSent(GameSettingsChangedWebSocketEventHandler.GameSettingsChangedEvent fwEvent) {
        ArgumentCaptor<WebSocketMessage> argumentCaptor = ArgumentCaptor.forClass(WebSocketMessage.class);
        verify(messageSenderProxy).sendToLobby(argumentCaptor.capture());

        WebSocketMessage message = argumentCaptor.getValue();
        assertThat(message.getRecipients()).containsExactly(MEMBER_ID);

        WebSocketEvent event = message.getEvent();
        assertThat(event.getEventName()).isEqualTo(WebSocketEventName.SKYXPLORE_LOBBY_GAME_SETTINGS_CHANGED);

        GameSettingsChangedWebSocketEventHandler.GameSettingsChangedEvent payload = (GameSettingsChangedWebSocketEventHandler.GameSettingsChangedEvent) event.getPayload();

        assertThat(payload.getUniverseSize()).isEqualTo(fwEvent.getUniverseSize());
        assertThat(payload.getSystemAmount()).isEqualTo(fwEvent.getSystemAmount());
        assertThat(payload.getSystemSize()).isEqualTo(fwEvent.getSystemSize());
        assertThat(payload.getPlanetSize()).isEqualTo(fwEvent.getPlanetSize());
        assertThat(payload.getAiPresence()).isEqualTo(fwEvent.getAiPresence());
    }
}