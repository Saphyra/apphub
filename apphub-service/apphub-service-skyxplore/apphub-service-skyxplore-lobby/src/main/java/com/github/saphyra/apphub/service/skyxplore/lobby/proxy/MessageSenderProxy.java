package com.github.saphyra.apphub.service.skyxplore.lobby.proxy;

import com.github.saphyra.apphub.api.platform.message_sender.client.MessageSenderApiClient;
import com.github.saphyra.apphub.api.platform.message_sender.model.MessageGroup;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketMessage;
import com.github.saphyra.apphub.api.skyxplore.request.game_creation.AiPlayer;
import com.github.saphyra.apphub.api.skyxplore.response.lobby.AllianceCreatedResponse;
import com.github.saphyra.apphub.api.skyxplore.response.lobby.LobbyMemberResponse;
import com.github.saphyra.apphub.lib.web_utils.LocaleProvider;
import com.github.saphyra.apphub.service.skyxplore.lobby.service.event.handler.ChatSendMessageWebSocketEventHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class MessageSenderProxy {
    private final MessageSenderApiClient messageSenderApiClient;
    private final LocaleProvider localeProvider;

    public void sendToMainMenu(WebSocketMessage message) {
        messageSenderApiClient.sendMessage(MessageGroup.SKYXPLORE_MAIN_MENU, message, localeProvider.getLocaleValidated());
    }

    public void sendToLobby(WebSocketMessage message) {
        messageSenderApiClient.sendMessage(MessageGroup.SKYXPLORE_LOBBY, message, localeProvider.getLocaleValidated());
    }

    public void lobbyMemberModified(LobbyMemberResponse member, Collection<UUID> recipients) {
        WebSocketMessage message = WebSocketMessage.forEventAndRecipients(WebSocketEventName.SKYXPLORE_LOBBY_PLAYER_MODIFIED, recipients, member);
        sendToLobby(message);
    }

    public void allianceCreated(AllianceCreatedResponse payload, Collection<UUID> recipients) {
        WebSocketMessage message = WebSocketMessage.forEventAndRecipients(WebSocketEventName.SKYXPLORE_LOBBY_ALLIANCE_CREATED, recipients, payload);
        sendToLobby(message);
    }

    public void aiModified(AiPlayer aiPlayer, Collection<UUID> recipients) {
        WebSocketMessage message = WebSocketMessage.forEventAndRecipients(WebSocketEventName.SKYXPLORE_LOBBY_AI_MODIFIED, recipients, aiPlayer);
        sendToLobby(message);
    }

    public void sendLobbyChatMessage(ChatSendMessageWebSocketEventHandler.Message message, Collection<UUID> recipients) {
        WebSocketMessage webSocketMessage = WebSocketMessage.forEventAndRecipients(WebSocketEventName.SKYXPLORE_LOBBY_CHAT_SEND_MESSAGE, recipients, message);
        sendToLobby(webSocketMessage);
    }

    public void lobbyMemberConnected(LobbyMemberResponse member, Collection<UUID> recipients) {
        WebSocketMessage message = WebSocketMessage.forEventAndRecipients(WebSocketEventName.SKYXPLORE_LOBBY_PLAYER_CONNECTED, recipients, member);
        sendToLobby(message);
    }

    public void lobbyMemberDisconnected(LobbyMemberResponse member, Collection<UUID> recipients) {
        WebSocketMessage message = WebSocketMessage.forEventAndRecipients(WebSocketEventName.SKYXPLORE_LOBBY_PLAYER_DISCONNECTED, recipients, member);
        sendToLobby(message);
    }
}
