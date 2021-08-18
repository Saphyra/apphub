package com.github.saphyra.apphub.service.skyxplore.game.service;

import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ExpiredGameCleanupServiceTest {
    @Mock
    private GameDao gameDao;

    @InjectMocks
    private ExpiredGameCleanupService underTest;

    @Mock
    private Game game1;

    @Mock
    private Game game2;

    @Mock
    private Game game3;

    @Test
    public void cleanUp() {
        given(gameDao.getAll()).willReturn(Arrays.asList(game1, game2, game3));
        given(game1.getConnectedPlayers()).willReturn(Arrays.asList(UUID.randomUUID()));
        given(game2.isMarkedForDeletion()).willReturn(true);

        underTest.cleanUp();

        verify(game3).setMarkedForDeletion(true);
        verify(gameDao).delete(game2);

        verify(game2, times(0)).setMarkedForDeletion(anyBoolean());
        verify(game1, times(0)).setMarkedForDeletion(anyBoolean());

        verify(gameDao, times(0)).delete(game1);
        verify(gameDao, times(0)).delete(game3);
    }
}