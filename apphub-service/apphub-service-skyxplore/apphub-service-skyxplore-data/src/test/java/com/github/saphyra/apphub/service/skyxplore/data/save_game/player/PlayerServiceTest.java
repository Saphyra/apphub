package com.github.saphyra.apphub.service.skyxplore.data.save_game.player;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.PlayerModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class PlayerServiceTest {
    private static final UUID GAME_ID = UUID.randomUUID();

    @Mock
    private PlayerDao playerDao;

    @Mock
    private PlayerModelValidator playerModelValidator;

    @InjectMocks
    private PlayerService underTest;

    @Mock
    private PlayerModel model;

    @Test
    public void deleteByGameId() {
        underTest.deleteByGameId(GAME_ID);

        verify(playerDao).deleteByGameId(GAME_ID);
    }

    @Test
    public void getType() {
        assertThat(underTest.getType()).isEqualTo(GameItemType.PLAYER);
    }

    @Test
    public void save() {
        underTest.save(Arrays.asList(model));

        verify(playerModelValidator).validate(model);
        verify(playerDao).saveAll(Arrays.asList(model));
    }
}