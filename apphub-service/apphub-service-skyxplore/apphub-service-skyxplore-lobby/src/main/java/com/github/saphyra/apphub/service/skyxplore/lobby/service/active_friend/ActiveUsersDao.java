package com.github.saphyra.apphub.service.skyxplore.lobby.service.active_friend;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.Vector;

@Component
@RequiredArgsConstructor
@Slf4j
class ActiveUsersDao {
    private final List<UUID> activePlayers = new Vector<>();

    void playerOnline(UUID userId) {
        if (!activePlayers.contains(userId)) {
            activePlayers.add(userId);
        }
    }

    void playerOffline(UUID userId) {
        activePlayers.removeIf(uuid -> uuid.equals(userId));
    }

    public boolean isOnline(UUID userId) {
        return activePlayers.contains(userId);
    }
}
