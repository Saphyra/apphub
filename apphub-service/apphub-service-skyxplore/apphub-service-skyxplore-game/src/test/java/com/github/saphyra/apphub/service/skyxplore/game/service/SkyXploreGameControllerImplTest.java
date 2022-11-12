package com.github.saphyra.apphub.service.skyxplore.game.service;

import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
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

    @Before
    public void setUp() {
        given(accessTokenHeader.getUserId()).willReturn(USER_ID);
    }

    @Test
    public void userIsInGame() {
        given(gameDao.findByUserId(USER_ID)).willReturn(Optional.of(game));

        assertThat(underTest.isUserInGame(accessTokenHeader)).isTrue();
    }

    @Test
    public void userIsNotInGame() {
        given(gameDao.findByUserId(USER_ID)).willReturn(Optional.empty());

        assertThat(underTest.isUserInGame(accessTokenHeader)).isFalse();
    }

    @Test
    public void cleanUpExpiredGames() {
        underTest.cleanUpExpiredGames();

        verify(expiredGameCleanupService).cleanUp();
    }

    @Test
    public void exitGame() {
        underTest.exitGame(accessTokenHeader);

        verify(exitFromGameService).exitFromGame(USER_ID);
    }

    @Test
    public void pauseGame() {
        underTest.pauseGame(new OneParamRequest<>(true), accessTokenHeader);

        verify(pauseGameService).setPausedStatus(USER_ID, true);
    }

    @Test
    public void deleteGame() {
        underTest.deleteGame(GAME_ID);

        verify(gameDao).delete(GAME_ID);
    }
}