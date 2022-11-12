package com.github.saphyra.apphub.service.skyxplore.game.service;

import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.config.CommonSkyXploreConfiguration;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ExpiredGameCleanupServiceTest {
    private static final LocalDateTime CURRENT_DATE = LocalDateTime.now();
    private static final Integer ABANDONED_GAME_EXPIRATION_SECONDS = 324;
    @Mock
    private GameDao gameDao;

    @Mock
    private DateTimeUtil dateTimeUtil;

    @Mock
    private CommonSkyXploreConfiguration configuration;

    @InjectMocks
    private ExpiredGameCleanupService underTest;

    @Mock
    private Game gameWithConnectedPlayers;

    @Mock
    private Game gameNotExpired;

    @Mock
    private Game gameExpired;

    @Mock
    private Game gameWithNoExpirationSet;

    @Test
    public void cleanUp() {
        given(dateTimeUtil.getCurrentDateTime()).willReturn(CURRENT_DATE);

        given(gameDao.getAll()).willReturn(Arrays.asList(gameWithConnectedPlayers, gameNotExpired, gameExpired, gameWithNoExpirationSet));
        given(gameWithConnectedPlayers.getConnectedPlayers()).willReturn(Arrays.asList(UUID.randomUUID()));
        given(gameNotExpired.getExpiresAt()).willReturn(CURRENT_DATE.plusSeconds(1));
        given(gameExpired.getExpiresAt()).willReturn(CURRENT_DATE.minusSeconds(1));

        given(configuration.getAbandonedGameExpirationSeconds()).willReturn(ABANDONED_GAME_EXPIRATION_SECONDS);

        underTest.cleanUp();


        verify(gameNotExpired, times(0)).setExpiresAt(any());
        verify(gameWithConnectedPlayers, times(0)).setExpiresAt(any());
        verify(gameWithNoExpirationSet).setExpiresAt(CURRENT_DATE.plusSeconds(ABANDONED_GAME_EXPIRATION_SECONDS));

        verify(gameDao).delete(gameExpired);
        verify(gameDao, times(0)).delete(gameWithConnectedPlayers);
        verify(gameDao, times(0)).delete(gameNotExpired);
        verify(gameDao, times(0)).delete(gameWithNoExpirationSet);
    }
}