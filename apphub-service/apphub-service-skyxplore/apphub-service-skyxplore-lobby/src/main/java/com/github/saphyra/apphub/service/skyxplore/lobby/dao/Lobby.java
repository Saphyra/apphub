package com.github.saphyra.apphub.service.skyxplore.lobby.dao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.time.LocalDateTime;
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
    private final String lobbyName;

    @NonNull
    private final UUID host;

    @NonNull
    private final Map<UUID, Member> members;

    @NonNull
    @Builder.Default
    private final List<Alliance> alliances = new Vector<>();

    @NonNull
    private LocalDateTime lastAccess;

    @Builder.Default
    private final List<Invitation> invitations = new Vector<>();

    @Builder.Default
    private final GameSettings settings = new GameSettings();

    private boolean gameCreationStarted;
}
