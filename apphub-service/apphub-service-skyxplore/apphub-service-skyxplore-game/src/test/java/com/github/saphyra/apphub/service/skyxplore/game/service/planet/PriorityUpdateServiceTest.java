package com.github.saphyra.apphub.service.skyxplore.game.service.planet;

import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.common.PriorityValidator;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.PriorityType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Universe;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.priority.PriorityUpdateService;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class PriorityUpdateServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID PLANET_ID = UUID.randomUUID();
    private static final Integer NEW_PRIORITY = 3421;

    @Mock
    private GameDao gameDao;

    @Mock
    private PriorityValidator priorityValidator;

    @InjectMocks
    private PriorityUpdateService underTest;

    @Mock
    private Game game;

    @Mock
    private Universe universe;

    @Mock
    private Planet planet;

    @After
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
        given(universe.findPlanetByIdValidated(PLANET_ID)).willReturn(planet);
        given(planet.getPriorities()).willReturn(priorities);

        underTest.updatePriority(USER_ID, PLANET_ID, PriorityType.CONSTRUCTION.toString(), NEW_PRIORITY);

        assertThat(priorities).containsEntry(PriorityType.CONSTRUCTION, NEW_PRIORITY);
    }
}