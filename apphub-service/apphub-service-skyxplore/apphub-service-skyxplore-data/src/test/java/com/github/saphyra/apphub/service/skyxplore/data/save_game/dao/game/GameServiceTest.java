package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.game;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class GameServiceTest {
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID HOST = UUID.randomUUID();

    @Mock
    private GameDao gameDao;

    @InjectMocks
    private GameService underTest;

    @Mock
    private GameModel model;

    @Test
    public void deleteByGameId() {
        underTest.deleteByGameId(GAME_ID);

        verify(gameDao).deleteById(GAME_ID);
    }

    @Test
    public void getType() {
        assertThat(underTest.getType()).isEqualTo(GameItemType.GAME);
    }

    @Test
    public void save() {
        underTest.save(Arrays.asList(model));

        verify(gameDao).saveAll(Arrays.asList(model));
    }

    @Test
    public void deleteById() {
        underTest.deleteById(GAME_ID);

        gameDao.deleteById(GAME_ID);
    }
}