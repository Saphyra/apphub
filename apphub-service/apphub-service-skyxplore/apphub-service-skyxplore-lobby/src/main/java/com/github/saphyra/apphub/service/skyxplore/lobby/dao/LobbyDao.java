package com.github.saphyra.apphub.service.skyxplore.lobby.dao;

import com.github.saphyra.apphub.lib.common_domain.ErrorMessage;
import com.github.saphyra.apphub.lib.common_util.ErrorCode;
import com.github.saphyra.apphub.lib.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
@Slf4j
public class LobbyDao {
    private final Map<UUID, Lobby> repository = new ConcurrentHashMap<>();

    public void save(Lobby lobby) {
        repository.put(lobby.getLobbyId(), lobby);
    }

    public Optional<Lobby> findByUserId(UUID userId) {
        return repository.values()
            .stream()
            .filter(lobby -> lobby.getMembers().containsKey(userId))
            .findFirst();
    }

    public Lobby findByUserIdValidated(UUID userId) {
        return findByUserId(userId)
            .orElseThrow(() -> new NotFoundException(new ErrorMessage(ErrorCode.LOBBY_NOT_FOUND.name()), "Lobby not found for userId " + userId));
    }

    public List<Lobby> getByLastAccessedBefore(LocalDateTime expiration) {
        return repository.values()
            .stream()
            .filter(lobby -> lobby.getLastAccess().isBefore(expiration))
            .collect(Collectors.toList());
    }

    public void delete(Lobby lobby) {
        repository.remove(lobby.getLobbyId());
    }

    public Collection<Lobby> getAll() {
        return repository.values();
    }
}
