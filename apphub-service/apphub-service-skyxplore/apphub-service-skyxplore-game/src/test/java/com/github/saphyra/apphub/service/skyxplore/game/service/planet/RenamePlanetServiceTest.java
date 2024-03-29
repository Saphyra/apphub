package com.github.saphyra.apphub.service.skyxplore.game.service.planet;

import com.github.saphyra.apphub.api.skyxplore.model.game.PlanetModel;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.common_util.collection.OptionalHashMap;
import com.github.saphyra.apphub.lib.common_util.collection.OptionalMap;
import com.github.saphyra.apphub.lib.concurrency.ExecutionResult;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.GameProgressDiff;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.PlanetConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planets;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.event_loop.EventLoop;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class RenamePlanetServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID PLANET_ID = UUID.randomUUID();
    private static final String NEW_NAME = "new-name";
    private static final UUID GAME_ID = UUID.randomUUID();

    @Mock
    private GameDao gameDao;

    @Mock
    private PlanetConverter planetConverter;

    @InjectMocks
    private RenamePlanetService underTest;

    @Mock
    private Game game;

    @Mock
    private GameData gameData;

    @Mock
    private Planet planet;

    @Mock
    private PlanetModel model;

    @Mock
    private GameProgressDiff progressDiff;

    @Mock
    private EventLoop eventLoop;

    @Mock
    private ExecutionResult<Void> executionResult;

    @Test
    public void blankName() {
        Throwable ex = catchThrowable(() -> underTest.rename(USER_ID, PLANET_ID, " "));

        ExceptionValidator.validateInvalidParam(ex, "newName", "must not be null or blank");
    }

    @Test
    public void tooLongName() {
        Throwable ex = catchThrowable(() -> underTest.rename(USER_ID, PLANET_ID, Stream.generate(() -> "a").limit(30).collect(Collectors.joining())));

        ExceptionValidator.validateInvalidParam(ex, "newName", "too long");
    }

    @Test
    public void rename() {
        given(gameDao.findByUserIdValidated(USER_ID)).willReturn(game);
        given(game.getData()).willReturn(gameData);
        given(gameData.getPlanets()).willReturn(CollectionUtils.singleValueMap(PLANET_ID, planet, new Planets()));
        given(game.getGameId()).willReturn(GAME_ID);

        OptionalMap<UUID, String> customNapes = new OptionalHashMap<>();
        given(planet.getCustomNames()).willReturn(customNapes);
        given(planetConverter.toModel(GAME_ID, planet)).willReturn(model);
        given(game.getProgressDiff()).willReturn(progressDiff);
        given(game.getEventLoop()).willReturn(eventLoop);
        given(eventLoop.processWithWait(any())).willReturn(executionResult);

        underTest.rename(USER_ID, PLANET_ID, NEW_NAME);

        ArgumentCaptor<Runnable> argumentCaptor = ArgumentCaptor.forClass(Runnable.class);
        then(eventLoop).should().processWithWait(argumentCaptor.capture());
        argumentCaptor.getValue()
                .run();

        verify(progressDiff).save(model);
        assertThat(customNapes).containsEntry(USER_ID, NEW_NAME);
        then(executionResult).should().getOrThrow();
    }
}