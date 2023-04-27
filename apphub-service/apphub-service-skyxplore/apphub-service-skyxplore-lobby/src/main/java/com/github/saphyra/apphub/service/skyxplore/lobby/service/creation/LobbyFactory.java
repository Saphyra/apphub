package com.github.saphyra.apphub.service.skyxplore.lobby.service.creation;

import com.github.saphyra.apphub.api.skyxplore.model.game.AllianceModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.PlayerModel;
import com.github.saphyra.apphub.api.skyxplore.response.lobby.LobbyMemberStatus;
import com.github.saphyra.apphub.api.skyxplore.response.game.GameViewForLobbyCreation;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Alliance;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Lobby;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.LobbyType;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.LobbyMember;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
class LobbyFactory {
    private final DateTimeUtil dateTimeUtil;
    private final IdGenerator idGenerator;
    private final GameSettingsProperties gameSettingsProperties;

    Lobby createForNewGame(UUID userId, String lobbyName) {
        Map<UUID, LobbyMember> members = new ConcurrentHashMap<>();
        members.put(userId, LobbyMember.builder().userId(userId).status(LobbyMemberStatus.NOT_READY).build());

        return Lobby.builder()
            .lobbyId(idGenerator.randomUuid())
            .type(LobbyType.NEW_GAME)
            .lobbyName(lobbyName)
            .host(userId)
            .members(members)
            .lastAccess(dateTimeUtil.getCurrentDateTime())
            .settings(gameSettingsProperties.createDefaultSettings())
            .build();
    }

    Lobby createForLoadGame(UUID host, UUID gameId, GameViewForLobbyCreation game) {
        Map<UUID, LobbyMember> members = new ConcurrentHashMap<>();
        members.put(host, LobbyMember.builder().userId(host).status(LobbyMemberStatus.NOT_READY).allianceId(game.getHostAllianceId()).build());

        return Lobby.builder()
            .lobbyId(idGenerator.randomUuid())
            .gameId(gameId)
            .host(host)
            .type(LobbyType.LOAD_GAME)
            .lobbyName(game.getName())
            .alliances(mapAlliances(game.getAlliances()))
            .lastAccess(dateTimeUtil.getCurrentDateTime())
            .members(members)
            .expectedPlayers(game.getPlayers().stream().map(PlayerModel::getUserId).collect(Collectors.toList()))
            .ais(game.getAis())
            .build();
    }

    private List<Alliance> mapAlliances(List<AllianceModel> alliances) {
        return alliances.stream()
            .map(allianceModel -> Alliance.builder()
                .allianceId(allianceModel.getId())
                .allianceName(allianceModel.getName())
                .build()
            )
            .collect(Collectors.toList());
    }
}
