package com.github.saphyra.apphub.service.skyxplore.lobby.service.settings;

import com.github.saphyra.apphub.api.skyxplore.request.game_creation.AiPlayer;
import com.github.saphyra.apphub.api.skyxplore.response.lobby.AllianceResponse;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Alliance;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Lobby;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.LobbyDao;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Member;
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
//TODO unit test
class AllianceService {
    private static final String NO_ALLIANCE = "no-alliance";
    private static final String NEW_ALLIANCE = "new-alliance";

    private final LobbyDao lobbyDao;
    private final IdGenerator idGenerator;
    private final UuidConverter uuidConverter;
    private final MessageSenderProxy messageSenderProxy;
    private final LobbyMemberToResponseConverter lobbyMemberToResponseConverter;

    List<AllianceResponse> getAlliances(UUID userId) {
        return lobbyDao.findByUserIdValidated(userId)
            .getAlliances()
            .stream()
            .map(AllianceService::convertToResponse)
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
                Alliance alliance = Alliance.builder()
                    .allianceId(idGenerator.randomUuid())
                    .allianceName(String.valueOf(lobby.getAlliances().size() + 1))
                    .build();
                lobby.getAlliances().add(alliance);
                aiPlayer.setAllianceId(alliance.getAllianceId());
                messageSenderProxy.allianceCreated(convertToResponse(alliance), lobby.getMembers().keySet());
                messageSenderProxy.aiModified(aiPlayer, lobby.getMembers().keySet());
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

        Member member = lobby.getMembers()
            .get(playerId);

        switch (allianceValue) {
            case NO_ALLIANCE -> {
                member.setAlliance(null);
                messageSenderProxy.lobbyMemberModified(lobbyMemberToResponseConverter.convertMember(member), lobby.getMembers().keySet());
            }
            case NEW_ALLIANCE -> {
                Alliance alliance = Alliance.builder()
                    .allianceId(idGenerator.randomUuid())
                    .allianceName(String.valueOf(lobby.getAlliances().size() + 1))
                    .build();
                lobby.getAlliances().add(alliance);
                member.setAlliance(alliance.getAllianceId());
                messageSenderProxy.allianceCreated(convertToResponse(alliance), lobby.getMembers().keySet());
                messageSenderProxy.lobbyMemberModified(lobbyMemberToResponseConverter.convertMember(member), lobby.getMembers().keySet());
            }
            default -> {
                UUID allianceId = uuidConverter.convertEntity(allianceValue);
                member.setAlliance(allianceId);
                messageSenderProxy.lobbyMemberModified(lobbyMemberToResponseConverter.convertMember(member), lobby.getMembers().keySet());
            }
        }
    }


    private static AllianceResponse convertToResponse(Alliance alliance) {
        return AllianceResponse.builder()
            .allianceId(alliance.getAllianceId())
            .allianceName(alliance.getAllianceName())
            .build();
    }
}
