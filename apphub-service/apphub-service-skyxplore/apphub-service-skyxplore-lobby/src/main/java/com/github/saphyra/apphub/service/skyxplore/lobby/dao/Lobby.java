package com.github.saphyra.apphub.service.skyxplore.lobby.dao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.Vector;

@Data
@Builder
@AllArgsConstructor
public class Lobby {
    @NonNull
    private final UUID lobbyId;

    @NonNull
    private final UUID host;

    @NonNull
    private final List<UUID> members;

    @NonNull
    private LocalDateTime lastAccess;

    @Builder.Default
    private final List<Invitation> invitations = new Vector<>();
}
