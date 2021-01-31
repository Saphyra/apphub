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
//TODO unit test
class ActiveUsersDao {
    private final List<UUID> activeCharacters = new Vector<>();

    void userOnline(UUID userId) {
        if (!activeCharacters.contains(userId)) {
            activeCharacters.add(userId);
        }
    }

    void userOffline(UUID userId) {
        activeCharacters.removeIf(uuid -> uuid.equals(userId));
    }

    public boolean isActive(UUID userId) {
        return activeCharacters.contains(userId);
    }
}
