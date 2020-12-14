package com.github.saphyra.apphub.api.skyxplore.response;

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
    private boolean inLobby;
    private UUID host;
    private boolean gameCreationStarted;
}
