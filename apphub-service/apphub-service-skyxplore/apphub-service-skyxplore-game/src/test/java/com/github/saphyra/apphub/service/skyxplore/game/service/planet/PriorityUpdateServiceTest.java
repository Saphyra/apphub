package com.github.saphyra.apphub.service.skyxplore.game.service.planet;

import com.github.saphyra.apphub.api.skyxplore.model.game.PriorityModel;
import com.github.saphyra.apphub.lib.concurrency.ExecutionResult;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.common.PriorityValidator;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.priority.PriorityType;
import com.github.saphyra.apphub.service.skyxplore.game.process.event_loop.EventLoop;
import com.github.saphyra.apphub.service.skyxplore.game.proxy.GameDataProxy;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.priority.PriorityUpdateService;
import com.github.saphyra.apphub.service.skyxplore.game.service.save.converter.PriorityToModelConverter;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class PriorityUpdateServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID PLANET_ID = UUID.randomUUID();
    private static final Integer NEW_PRIORITY = 3421;
    private static final UUID GAME_ID = UUID.randomUUID();

    @Mock
    private GameDao gameDao;

    @Mock
    private PriorityValidator priorityValidator;

    @Mock
    private PriorityToModelConverter priorityToModelConverter;

    @Mock
    private GameDataProxy gameDataProxy;

    @InjectMocks
    private PriorityUpdateService underTest;

    @Mock
    private Game game;

    @Mock
    private Universe universe;

    @Mock
    private Planet planet;

    @Mock
    private PriorityModel model;

    @Mock
    private EventLoop eventLoop;

    @Mock
    private ExecutionResult<Void> executionResult;

    @AfterEach
    public void validate() {
        verify(priorityValidator).validate(NEW_PRIORITY);
    }

    @Test
    public void unknownPriorityType() {
        Throwable ex = catchThrowable(() -> underTest.updatePriority(USER_ID, PLANET_ID, "asd", NEW_PRIORITY));

        ExceptionValidator.validateInvalidParam(ex, "priorityType", "unknown value");
    }

    @Test
    public void setPriority() {
        Map<PriorityType, Integer> priorities = new HashMap<>();
        given(gameDao.findByUserIdValidated(USER_ID)).willReturn(game);
        given(game.getUniverse()).willReturn(universe);
        given(universe.findByOwnerAndPlanetIdValidated(USER_ID, PLANET_ID)).willReturn(planet);
        given(planet.getPriorities()).willReturn(priorities);
        given(game.getGameId()).willReturn(GAME_ID);
        given(priorityToModelConverter.convert(PriorityType.CONSTRUCTION, NEW_PRIORITY, PLANET_ID, LocationType.PLANET, GAME_ID)).willReturn(model);
        given(game.getEventLoop()).willReturn(eventLoop);
        given(eventLoop.processWithWait(any())).willReturn(executionResult);

        underTest.updatePriority(USER_ID, PLANET_ID, PriorityType.CONSTRUCTION.toString(), NEW_PRIORITY);

        ArgumentCaptor<Runnable> argumentCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(eventLoop).processWithWait(argumentCaptor.capture());
        argumentCaptor.getValue()
            .run();

        assertThat(priorities).containsEntry(PriorityType.CONSTRUCTION, NEW_PRIORITY);

        verify(gameDataProxy).saveItem(model);
    }
}