package com.github.saphyra.apphub.service.skyxplore.game.common;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Player;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class GameDaoTest {
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();

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

        assertThat(underTest.getRepository()).containsEntry(GAME_ID, game);
    }

    @Test
    public void findByUserIdValidated_notFound() {
        Throwable ex = catchThrowable(() -> underTest.findByUserIdValidated(UUID.randomUUID()));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.NOT_FOUND, ErrorCode.GAME_NOT_FOUND);
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

    @Test
    public void getAll() {
        underTest.save(game);

        List<Game> result = underTest.getAll();

        assertThat(result).containsExactly(game);
        result.clear();
        assertThat(underTest.getAll()).containsExactly(game);
    }
}