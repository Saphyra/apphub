package com.github.saphyra.apphub.service.skyxplore.lobby.service.start_game;

import com.github.saphyra.apphub.api.skyxplore.response.lobby.LobbyPlayerStatus;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Lobby;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.LobbyDao;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.LobbyPlayer;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.LobbyType;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.UUID;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class StartGameServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();

    @Mock
    private LobbyDao lobbyDao;

    @Mock
    private CreateNewGameService createNewGameService;

    @Mock
    private LoadGameService loadGameService;

    @InjectMocks
    private StartGameService underTest;

    @Mock
    private Lobby lobby;

    @Mock
    private LobbyPlayer lobbyPlayer;

    @Test
    public void forbiddenOperation() {
        given(lobbyDao.findByUserIdValidated(USER_ID)).willReturn(lobby);
        given(lobby.getHost()).willReturn(UUID.randomUUID());

        Throwable ex = catchThrowable(() -> underTest.startGame(USER_ID));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.FORBIDDEN, ErrorCode.FORBIDDEN_OPERATION);
    }

    @Test
    public void unreadyMember() {
        given(lobbyDao.findByUserIdValidated(USER_ID)).willReturn(lobby);
        given(lobby.getPlayers()).willReturn(CollectionUtils.singleValueMap(USER_ID, lobbyPlayer));
        given(lobbyPlayer.getStatus()).willReturn(LobbyPlayerStatus.NOT_READY);
        given(lobby.getHost()).willReturn(USER_ID);

        Throwable ex = catchThrowable(() -> underTest.startGame(USER_ID));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.PRECONDITION_FAILED, ErrorCode.LOBBY_PLAYER_NOT_READY);
    }

    @Test
    public void createNewGame() {
        given(lobbyDao.findByUserIdValidated(USER_ID)).willReturn(lobby);
        given(lobby.getHost()).willReturn(USER_ID);
        given(lobby.getType()).willReturn(LobbyType.NEW_GAME);

        underTest.startGame(USER_ID);

        verify(createNewGameService).createNewGame(lobby);
    }

    @Test
    public void loadGame() {
        given(lobbyDao.findByUserIdValidated(USER_ID)).willReturn(lobby);
        given(lobby.getHost()).willReturn(USER_ID);
        given(lobby.getType()).willReturn(LobbyType.LOAD_GAME);

        underTest.startGame(USER_ID);

        verify(loadGameService).loadGame(lobby);
    }
}