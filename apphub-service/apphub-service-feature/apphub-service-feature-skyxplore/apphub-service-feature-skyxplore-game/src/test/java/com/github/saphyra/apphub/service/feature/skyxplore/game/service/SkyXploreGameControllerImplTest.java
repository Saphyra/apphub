package com.github.saphyra.apphub.service.feature.skyxplore.game.service;

import com.github.saphyra.apphub.lib.common_domain.AccessToken;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.service.feature.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.feature.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.feature.skyxplore.game.simulation.tick.TickSchedulerLauncher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
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

    @Mock
    private SaveGameService saveGameService;

    @Mock
    private TickSchedulerLauncher tickSchedulerLauncher;

    @InjectMocks
    private SkyXploreGameControllerImpl underTest;

    @Mock
    private Game game;

    @Mock
    private AccessToken accessToken;


    @Test
    public void userIsInGame() {
        given(accessToken.getUserId()).willReturn(USER_ID);
        given(gameDao.findByUserId(USER_ID)).willReturn(Optional.of(game));
        given(game.getGameId()).willReturn(GAME_ID);

        assertThat(underTest.getGameId(accessToken).getValue()).isEqualTo(GAME_ID);
    }

    @Test
    public void userIsNotInGame() {
        given(accessToken.getUserId()).willReturn(USER_ID);
        given(gameDao.findByUserId(USER_ID)).willReturn(Optional.empty());

        assertThat(underTest.getGameId(accessToken).getValue()).isNull();
    }

    @Test
    public void cleanUpExpiredGames() {
        underTest.cleanUpExpiredGames();

        verify(expiredGameCleanupService).cleanUp();
    }

    @Test
    public void exitGame() {
        given(accessToken.getUserId()).willReturn(USER_ID);

        underTest.exitGame(accessToken);

        verify(exitFromGameService).exitFromGame(USER_ID);
    }

    @Test
    public void pauseGame() {
        given(accessToken.getUserId()).willReturn(USER_ID);

        underTest.pauseGame(new OneParamRequest<>(true), accessToken);

        verify(pauseGameService).setPausedStatus(USER_ID, true);
    }

    @Test
    public void deleteGame() {
        underTest.deleteGame(GAME_ID);

        verify(gameDao).delete(GAME_ID);
    }

    @Test
    void saveGame() {
        given(accessToken.getUserId()).willReturn(USER_ID);

        underTest.saveGame(accessToken);

        then(saveGameService).should().saveGame(USER_ID);
    }

    @Test
    void processTick() {
        given(accessToken.getUserId()).willReturn(USER_ID);

        underTest.processTick(accessToken);

        then(tickSchedulerLauncher).should().processTick(USER_ID);
    }
}