package com.github.saphyra.apphub.service.skyxplore.lobby.service.event.handler;

import com.github.saphyra.apphub.api.platform.message_sender.client.MessageSenderApiClient;
import com.github.saphyra.apphub.api.platform.message_sender.model.MessageGroup;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEvent;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketMessage;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.common_util.LocaleProvider;
import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Alliance;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Lobby;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.LobbyDao;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class ChangeAllianceWebSocketEventHandler implements WebSocketEventHandler {
    private static final String NO_ALLIANCE = "no-alliance";
    private static final String NEW_ALLIANCE = "new-alliance";

    private final ObjectMapperWrapper objectMapperWrapper;
    private final LobbyDao lobbyDao;
    private final IdGenerator idGenerator;
    private final MessageSenderApiClient messageSenderClient;
    private final LocaleProvider localeProvider;

    @Override
    public boolean canHandle(WebSocketEventName eventName) {
        return WebSocketEventName.SKYXPLORE_LOBBY_CHANGE_ALLIANCE == eventName;
    }

    @Override
    public void handle(UUID from, WebSocketEvent event) {
        log.info("{} changes alliance: {}", from, event.getPayload());

        ChangeAllianceEvent changeAllianceEvent = objectMapperWrapper.convertValue(event.getPayload(), ChangeAllianceEvent.class);

        Lobby lobby = lobbyDao.findByUserIdValidated(from);

        Member member = lobby.getMembers().get(changeAllianceEvent.getUserId());
        if (!lobby.getHost().equals(from) && !from.equals(changeAllianceEvent.getUserId())) {
            String alliance = Optional.ofNullable(member.getAlliance())
                .map(allianceId -> lobby.getAlliances().stream().filter(a -> a.getAllianceId().equals(allianceId)).findFirst().orElseThrow(() -> new RuntimeException("Alliance not found with id " + allianceId)))
                .map(Alliance::getAllianceName)
                .orElse(NO_ALLIANCE);
            sendMessage(changeAllianceEvent.getUserId(), alliance, false, lobby.getMembers());
            throw new RuntimeException(from + " must not change the alliance of " + changeAllianceEvent.getUserId());
        }

        switch (changeAllianceEvent.getAlliance()) {
            case NO_ALLIANCE:
                member.setAlliance(null);
                sendMessage(changeAllianceEvent.getUserId(), changeAllianceEvent.getAlliance(), false, lobby.getMembers());
                break;
            case NEW_ALLIANCE:
                Alliance alliance = Alliance.builder()
                    .allianceId(idGenerator.randomUuid())
                    .allianceName(String.valueOf(lobby.getAlliances().size() + 1))
                    .build();
                lobby.getAlliances().add(alliance);
                member.setAlliance(alliance.getAllianceId());
                sendMessage(changeAllianceEvent.getUserId(), alliance.getAllianceName(), true, lobby.getMembers());
                break;
            default:
                UUID allianceId = lobby.getAlliances()
                    .stream()
                    .filter(a -> a.getAllianceName().equals(changeAllianceEvent.getAlliance()))
                    .findFirst()
                    .map(Alliance::getAllianceId)
                    .orElseThrow(() -> new RuntimeException("Alliance not found with name " + changeAllianceEvent.getAlliance()));

                member.setAlliance(allianceId);
                sendMessage(changeAllianceEvent.getUserId(), changeAllianceEvent.getAlliance(), false, lobby.getMembers());
        }
    }

    private void sendMessage(UUID userId, String alliance, boolean newAlliance, Map<UUID, Member> members) {
        List<UUID> recipients = new ArrayList<>(members.keySet());

        AllianceChangedEvent payload = AllianceChangedEvent.builder()
            .userId(userId)
            .alliance(alliance)
            .newAlliance(newAlliance)
            .build();

        WebSocketEvent event = WebSocketEvent.builder()
            .eventName(WebSocketEventName.SKYXPLORE_LOBBY_CHANGE_ALLIANCE)
            .payload(payload)
            .build();

        WebSocketMessage message = WebSocketMessage.builder()
            .recipients(recipients)
            .event(event)
            .build();
        messageSenderClient.sendMessage(MessageGroup.SKYXPLORE_LOBBY, message, localeProvider.getLocaleValidated());
    }

    @NoArgsConstructor
    @Data
    private static class ChangeAllianceEvent {
        private UUID userId;
        private String alliance;
    }

    @Data
    @AllArgsConstructor
    @Builder
    private static class AllianceChangedEvent {
        private UUID userId;
        private String alliance;
        private boolean newAlliance;
    }
}
