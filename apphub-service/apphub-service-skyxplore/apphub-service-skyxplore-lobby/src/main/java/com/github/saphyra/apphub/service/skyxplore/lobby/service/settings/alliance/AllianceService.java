package com.github.saphyra.apphub.service.skyxplore.lobby.service.settings.alliance;

import com.github.saphyra.apphub.api.skyxplore.request.game_creation.AiPlayer;
import com.github.saphyra.apphub.api.skyxplore.response.lobby.AllianceCreatedResponse;
import com.github.saphyra.apphub.api.skyxplore.response.lobby.AllianceResponse;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Alliance;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Lobby;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.LobbyDao;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.LobbyMember;
import com.github.saphyra.apphub.service.skyxplore.lobby.proxy.MessageSenderProxy;
import com.github.saphyra.apphub.service.skyxplore.lobby.service.member.LobbyMemberToResponseConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class AllianceService {
    private static final String NO_ALLIANCE = "no-alliance";
    private static final String NEW_ALLIANCE = "new-alliance";

    private final LobbyDao lobbyDao;
    private final UuidConverter uuidConverter;
    private final MessageSenderProxy messageSenderProxy;
    private final LobbyMemberToResponseConverter lobbyMemberToResponseConverter;
    private final AllianceToResponseConverter allianceToResponseConverter;
    private final AllianceFactory allianceFactory;

    public List<AllianceResponse> getAlliances(UUID userId) {
        return lobbyDao.findByUserIdValidated(userId)
            .getAlliances()
            .stream()
            .map(allianceToResponseConverter::convertToResponse)
            .collect(Collectors.toList());
    }

    public void setAllianceOfAi(UUID userId, UUID playerId, String allianceValue) {
        Lobby lobby = lobbyDao.findByUserIdValidated(userId);

        if (!lobby.getHost().equals(userId)) {
            throw ExceptionFactory.notLoggedException(HttpStatus.FORBIDDEN, ErrorCode.FORBIDDEN_OPERATION, userId + " must not change the alliance of " + playerId);
        }

        AiPlayer aiPlayer = lobby.getAis()
            .stream()
            .filter(player -> player.getUserId().equals(playerId))
            .findFirst()
            .orElseThrow(() -> ExceptionFactory.notLoggedException(HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND, "AiPlayer not found with userId " + playerId));

        switch (allianceValue) {
            case NO_ALLIANCE -> {
                aiPlayer.setAllianceId(null);
                messageSenderProxy.aiModified(aiPlayer, lobby.getMembers().keySet());
            }
            case NEW_ALLIANCE -> {
                Alliance alliance = allianceFactory.create(lobby.getAlliances().size());
                lobby.getAlliances()
                    .add(alliance);
                aiPlayer.setAllianceId(alliance.getAllianceId());
                messageSenderProxy.allianceCreated(
                    AllianceCreatedResponse.builder()
                        .alliance(allianceToResponseConverter.convertToResponse(alliance))
                        .ai(aiPlayer)
                        .build(),
                    lobby.getMembers().keySet()
                );
            }
            default -> {
                UUID allianceId = uuidConverter.convertEntity(allianceValue);
                aiPlayer.setAllianceId(allianceId);
                messageSenderProxy.aiModified(aiPlayer, lobby.getMembers().keySet());
            }
        }
    }

    public void setAllianceOfPlayer(UUID userId, UUID playerId, String allianceValue) {
        Lobby lobby = lobbyDao.findByUserIdValidated(userId);

        if (!lobby.getHost().equals(userId)) {
            throw ExceptionFactory.notLoggedException(HttpStatus.FORBIDDEN, ErrorCode.FORBIDDEN_OPERATION, userId + " must not change the alliance of " + playerId);
        }

        LobbyMember lobbyMember = lobby.getMembers()
            .get(playerId);

        switch (allianceValue) {
            case NO_ALLIANCE -> {
                lobbyMember.setAllianceId(null);
                messageSenderProxy.lobbyMemberModified(lobbyMemberToResponseConverter.convertMember(lobbyMember), lobby.getMembers().keySet());
            }
            case NEW_ALLIANCE -> {
                Alliance alliance = allianceFactory.create(lobby.getAlliances().size());
                lobby.getAlliances().add(alliance);
                lobbyMember.setAllianceId(alliance.getAllianceId());
                messageSenderProxy.allianceCreated(
                    AllianceCreatedResponse.builder()
                        .alliance(allianceToResponseConverter.convertToResponse(alliance))
                        .member(lobbyMemberToResponseConverter.convertMember(lobbyMember))
                        .build(),
                    lobby.getMembers().keySet()
                );
            }
            default -> {
                UUID allianceId = uuidConverter.convertEntity(allianceValue);
                lobbyMember.setAllianceId(allianceId);
                messageSenderProxy.lobbyMemberModified(lobbyMemberToResponseConverter.convertMember(lobbyMember), lobby.getMembers().keySet());
            }
        }
    }
}
