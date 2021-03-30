package com.github.saphyra.apphub.service.skyxplore.game.service.planet;

import com.github.saphyra.apphub.lib.common_util.ErrorCode;
import com.github.saphyra.apphub.lib.exception.BadRequestException;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.PriorityType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Universe;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.priority.PriorityUpdateService;
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

@RunWith(MockitoJUnitRunner.class)
public class PriorityUpdateServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID PLANET_ID = UUID.randomUUID();

    @Mock
    private GameDao gameDao;

    @InjectMocks
    private PriorityUpdateService underTest;

    @Mock
    private Game game;

    @Mock
    private Universe universe;

    @Mock
    private Planet planet;

    @Test
    public void unknownPriorityType() {
        Throwable ex = catchThrowable(() -> underTest.updatePriority(USER_ID, PLANET_ID, "asd", 10));

        assertThat(ex).isInstanceOf(BadRequestException.class);
        BadRequestException exception = (BadRequestException) ex;
        assertThat(exception.getErrorMessage().getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());
        assertThat(exception.getErrorMessage().getParams()).containsEntry("priorityType", "unknown value");
    }

    @Test
    public void priorityTooLow() {
        Throwable ex = catchThrowable(() -> underTest.updatePriority(USER_ID, PLANET_ID, PriorityType.CONSTRUCTION.toString(), 0));

        assertThat(ex).isInstanceOf(BadRequestException.class);
        BadRequestException exception = (BadRequestException) ex;
        assertThat(exception.getErrorMessage().getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());
        assertThat(exception.getErrorMessage().getParams()).containsEntry("value", "too low");
    }

    @Test
    public void priorityTooHigh() {
        Throwable ex = catchThrowable(() -> underTest.updatePriority(USER_ID, PLANET_ID, PriorityType.CONSTRUCTION.toString(), 110));

        assertThat(ex).isInstanceOf(BadRequestException.class);
        BadRequestException exception = (BadRequestException) ex;
        assertThat(exception.getErrorMessage().getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());
        assertThat(exception.getErrorMessage().getParams()).containsEntry("value", "too high");
    }

    @Test
    public void setPriority() {
        Map<PriorityType, Integer> priorities = new HashMap<>();
        given(gameDao.findByUserIdValidated(USER_ID)).willReturn(game);
        given(game.getUniverse()).willReturn(universe);
        given(universe.findPlanetByIdValidated(PLANET_ID)).willReturn(planet);
        given(planet.getPriorities()).willReturn(priorities);

        underTest.updatePriority(USER_ID, PLANET_ID, PriorityType.CONSTRUCTION.toString(), 5);

        assertThat(priorities).containsEntry(PriorityType.CONSTRUCTION, 5);
    }
}