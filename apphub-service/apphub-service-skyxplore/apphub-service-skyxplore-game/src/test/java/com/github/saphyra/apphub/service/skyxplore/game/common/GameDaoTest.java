package com.github.saphyra.apphub.service.skyxplore.game.common;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.SleepService;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBean;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBeenTestUtils;
import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.player.Player;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.event_loop.EventLoop;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
public class GameDaoTest {
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();

    @Spy
    private final ExecutorServiceBean executorServiceBean = ExecutorServiceBeenTestUtils.create(Mockito.mock(ErrorReporterService.class));

    @Spy
    private final SleepService sleepService = new SleepService();

    @InjectMocks
    private GameDao underTest;

    @Mock
    private Game game;

    @Mock
    private Player player;

    @Mock
    private EventLoop eventLoop;

    @Test
    public void save() {
        given(game.getGameId()).willReturn(GAME_ID);

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
        given(game.getGameId()).willReturn(GAME_ID);

        underTest.save(game);

        given(game.getPlayers()).willReturn(CollectionUtils.singleValueMap(USER_ID, player));

        Optional<Game> result = underTest.findByUserId(USER_ID);

        assertThat(result).contains(game);
    }

    @Test
    public void delete() {
        given(game.getGameId()).willReturn(GAME_ID);

        underTest.save(game);
        given(game.getEventLoop()).willReturn(eventLoop);

        underTest.delete(game);

        then(game).should().setTerminated(true);
        then(game).should().setGamePaused(true);

        assertThat(underTest.getAll()).hasSize(1);

        given(game.isTerminated()).willReturn(true);

        sleepService.sleep(5000);

        then(eventLoop).should().stop();
        then(sleepService).should().sleep(3000);
        then(sleepService).should().sleep(1000);

        assertThat(underTest.getAll()).isEmpty();
    }

    @Test
    public void delete_notTerminated() {
        given(game.getGameId()).willReturn(GAME_ID);

        underTest.save(game);
        given(game.getEventLoop()).willReturn(eventLoop);

        underTest.delete(game);

        then(game).should().setTerminated(true);
        then(game).should().setGamePaused(true);

        assertThat(underTest.getAll()).hasSize(1);

        given(game.isTerminated()).willReturn(false);

        sleepService.sleep(5000);

        then(eventLoop).should().stop();
        then(sleepService).should().sleep(3000);
        then(sleepService).should().sleep(1000);

        assertThat(underTest.getAll()).hasSize(1);
    }

    @Test
    public void getAll() {
        given(game.getGameId()).willReturn(GAME_ID);

        underTest.save(game);

        List<Game> result = underTest.getAll();

        assertThat(result).containsExactly(game);
        result.clear();
        assertThat(underTest.getAll()).containsExactly(game);
    }

    @Test
    void findById() {
        given(game.getGameId()).willReturn(GAME_ID);

        underTest.save(game);

        assertThat(underTest.findById(GAME_ID)).isEqualTo(game);
    }
}