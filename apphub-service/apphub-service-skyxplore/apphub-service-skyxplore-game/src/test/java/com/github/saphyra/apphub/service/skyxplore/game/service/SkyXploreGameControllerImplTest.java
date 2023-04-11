package com.github.saphyra.apphub.service.skyxplore.game.service;

import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class SkyXploreGameControllerImplTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID GAME_ID = UUID.randomUUID();

    @Mock
    private GameDao gameDao;

    @Mock
    private ExpiredGameCleanupService expiredGameCleanupService;

    @Mock
    private ExitFromGameService exitFromGameService;

    @Mock
    private PauseGameService pauseGameService;

    @InjectMocks
    private SkyXploreGameControllerImpl underTest;

    @Mock
    private Game game;

    @Mock
    private AccessTokenHeader accessTokenHeader;


    @Test
    public void userIsInGame() {
        given(accessTokenHeader.getUserId()).willReturn(USER_ID);
        given(gameDao.findByUserId(USER_ID)).willReturn(Optional.of(game));

        assertThat(underTest.isUserInGame(accessTokenHeader).getValue()).isTrue();
    }

    @Test
    public void userIsNotInGame() {
        given(accessTokenHeader.getUserId()).willReturn(USER_ID);
        given(gameDao.findByUserId(USER_ID)).willReturn(Optional.empty());

        assertThat(underTest.isUserInGame(accessTokenHeader).getValue()).isFalse();
    }

    @Test
    public void cleanUpExpiredGames() {
        underTest.cleanUpExpiredGames();

        verify(expiredGameCleanupService).cleanUp();
    }

    @Test
    public void exitGame() {
        given(accessTokenHeader.getUserId()).willReturn(USER_ID);

        underTest.exitGame(accessTokenHeader);

        verify(exitFromGameService).exitFromGame(USER_ID);
    }

    @Test
    public void pauseGame() {
        given(accessTokenHeader.getUserId()).willReturn(USER_ID);

        underTest.pauseGame(new OneParamRequest<>(true), accessTokenHeader);

        verify(pauseGameService).setPausedStatus(USER_ID, true);
    }

    @Test
    public void deleteGame() {
        underTest.deleteGame(GAME_ID);

        verify(gameDao).delete(GAME_ID);
    }
}