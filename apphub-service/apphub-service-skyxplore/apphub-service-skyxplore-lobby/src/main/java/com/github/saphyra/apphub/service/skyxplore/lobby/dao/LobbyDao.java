package com.github.saphyra.apphub.service.skyxplore.lobby.dao;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_domain.WebSocketEventName;
import com.github.saphyra.apphub.lib.common_util.ApplicationContextProxy;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.skyxplore.lobby.ws.SkyXploreLobbyInvitationWebSocketHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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

    private final ApplicationContextProxy applicationContextProxy;

    public void save(Lobby lobby) {
        repository.put(lobby.getLobbyId(), lobby);
    }

    public Lobby findByHostValidated(UUID userId) {
        Lobby result = findByUserIdValidated(userId);

        if (!result.getHost().equals(userId)) {
            throw ExceptionFactory.forbiddenOperation(userId + " has not host access to lobby " + result.getLobbyId());
        }

        return result;
    }

    public Optional<Lobby> findByUserId(UUID userId) {
        return repository.values()
            .stream()
            .filter(lobby -> lobby.getMembers().containsKey(userId))
            .findFirst();
    }

    public Lobby findByUserIdValidated(UUID userId) {
        return findByUserId(userId)
            .orElseThrow(() -> ExceptionFactory.notLoggedException(HttpStatus.NOT_FOUND, ErrorCode.LOBBY_NOT_FOUND, "Lobby not found for userUd" + userId));
    }

    public List<Lobby> getByLastAccessedBefore(LocalDateTime expiration) {
        return repository.values()
            .stream()
            .filter(lobby -> lobby.getLastAccess().isBefore(expiration))
            .collect(Collectors.toList());
    }

    public void delete(Lobby lobby) {
        lobby.getInvitations()
            .forEach(this::cancelInvitation);

        repository.remove(lobby.getLobbyId());
    }

    private void cancelInvitation(Invitation invitation) {
        applicationContextProxy.getBean(SkyXploreLobbyInvitationWebSocketHandler.class)
            .sendEvent(invitation.getCharacterId(), WebSocketEventName.SKYXPLORE_MAIN_MENU_CANCEL_INVITATION, invitation.getInvitorId());
    }

    public Collection<Lobby> getAll() {
        return repository.values();
    }

    public Optional<Lobby> findByGameId(UUID gameId) {
        return repository.values()
            .stream()
            .filter(lobby -> gameId.equals(lobby.getGameId()))
            .findFirst();
    }
}
