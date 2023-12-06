package com.github.saphyra.apphub.service.skyxplore.lobby.service.player;

import com.github.saphyra.apphub.api.skyxplore.response.lobby.LobbyPlayerResponse;
import com.github.saphyra.apphub.api.skyxplore.response.lobby.LobbyPlayerStatus;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Invitation;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.LobbyPlayer;
import com.github.saphyra.apphub.service.skyxplore.lobby.proxy.CharacterProxy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class LobbyPlayerToResponseConverter {
    private final CharacterProxy characterProxy;
    private final DateTimeUtil dateTimeUtil;

    public LobbyPlayerResponse convertPlayer(LobbyPlayer lobbyPlayer) {
        return LobbyPlayerResponse.builder()
            .userId(lobbyPlayer.getUserId())
            .status(lobbyPlayer.getStatus())
            .characterName(characterProxy.getCharacter(lobbyPlayer.getUserId()).getName())
            .allianceId(lobbyPlayer.getAllianceId())
            .createdAt(dateTimeUtil.getCurrentTimeEpochMillis())
            .build();
    }

    public LobbyPlayerResponse convertInvitation(Invitation invitation) {
        UUID userId = invitation.getCharacterId();

        return LobbyPlayerResponse.builder()
            .userId(userId)
            .characterName(characterProxy.getCharacter(userId).getName())
            .status(LobbyPlayerStatus.INVITED)
            .build();
    }
}
