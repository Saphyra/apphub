package com.github.saphyra.apphub.service.skyxplore.game.common;

import com.github.saphyra.apphub.lib.common_util.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.exception.NotFoundException;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Player;
import com.github.saphyra.apphub.service.skyxplore.game.service.save.GameSaverService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class GameDaoTest {
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();

    @Mock
    private GameSaverService gameSaverService;

    @InjectMocks
    private GameDao underTest;

    @Mock
    private Game game;

    @Mock
    private Player player;

    @Before
    public void setUp() {
        given(game.getGameId()).willReturn(GAME_ID);
    }

    @Test
    public void save() {
        underTest.save(game);

        verify(gameSaverService).save(game);
        assertThat(underTest.getRepository()).containsEntry(GAME_ID, game);
    }

    @Test
    public void findByUserIdValidated_notFound() {
        Throwable ex = catchThrowable(() -> underTest.findByUserIdValidated(UUID.randomUUID()));

        assertThat(ex).isInstanceOf(NotFoundException.class);
        NotFoundException exception = (NotFoundException) ex;
        assertThat(exception.getErrorMessage().getErrorCode()).isEqualTo(ErrorCode.GAME_NOT_FOUND.name());
    }

    @Test
    public void findByUserId() {
        underTest.save(game);

        given(game.getPlayers()).willReturn(CollectionUtils.singleValueMap(USER_ID, player));

        Optional<Game> result = underTest.findByUserId(USER_ID);

        assertThat(result).contains(game);
    }

    @Test
    public void delete() {
        underTest.save(game);

        underTest.delete(game);

        assertThat(underTest.findByUserId(USER_ID)).isEmpty();
    }
}