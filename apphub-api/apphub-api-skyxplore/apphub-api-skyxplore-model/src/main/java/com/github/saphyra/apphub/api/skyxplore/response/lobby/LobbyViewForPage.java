package com.github.saphyra.apphub.api.skyxplore.response.lobby;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class LobbyViewForPage {
    private String lobbyName;
    private boolean isHost;
    private String lobbyType;
    private UUID ownUserId;
}
