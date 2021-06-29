package com.github.saphyra.apphub.service.skyxplore.lobby.service.start_game;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Lobby;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.LobbyDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class StartGameService {
    private final LobbyDao lobbyDao;
    private final CreateNewGameService createNewGameService;
    private final LoadGameService loadGameService;

    public void startGame(UUID userId) {
        Lobby lobby = lobbyDao.findByUserIdValidated(userId);

        if (!lobby.getHost().equals(userId)) {
            throw ExceptionFactory.notLoggedException(HttpStatus.FORBIDDEN, ErrorCode.FORBIDDEN_OPERATION, userId + " must not start the game.");
        }

        switch (lobby.getType()) {
            case NEW_GAME:
                createNewGameService.createNewGame(lobby);
                break;
            case LOAD_GAME:
                loadGameService.loadGame(lobby);
                break;
            default:
                throw ExceptionFactory.reportedException(HttpStatus.NOT_IMPLEMENTED, "Unhandled lobbyType: " + lobby.getType());
        }
    }
}
