package com.github.saphyra.apphub.service.skyxplore.lobby.dao;

import com.github.saphyra.apphub.api.skyxplore.model.SkyXploreGameSettings;
import com.github.saphyra.apphub.api.skyxplore.request.game_creation.AiPlayer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Vector;

@Data
@Builder
@AllArgsConstructor
public class Lobby {
    @NonNull
    private final UUID lobbyId;

    @NonNull
    private final LobbyType type;
    private UUID gameId;

    @Builder.Default
    private final List<UUID> expectedPlayers = Collections.emptyList();

    @NonNull
    private final String lobbyName;

    @NonNull
    private final UUID host;

    @NonNull
    private final Map<UUID, LobbyPlayer> players;

    @NonNull
    @Builder.Default
    private final List<AiPlayer> ais = new Vector<>();

    @NonNull
    @Builder.Default
    private final List<Alliance> alliances = new Vector<>();

    @NonNull
    private LocalDateTime lastAccess;

    @Builder.Default
    private final List<Invitation> invitations = new Vector<>();

    private SkyXploreGameSettings settings;

    private boolean gameCreationStarted;
}
