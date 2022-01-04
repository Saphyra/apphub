package com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue;

import com.github.saphyra.apphub.api.skyxplore.response.game.planet.QueueResponse;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.QueueItemType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Universe;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue.service.QueueService;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class QueueFacadeTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID PLANET_ID = UUID.randomUUID();
    private static final UUID ITEM_ID = UUID.randomUUID();
    private static final Integer PRIORITY = 2354;

    @Mock
    private GameDao gameDao;

    @Mock
    private QueueService queueService;

    @Mock
    private QueueItemToResponseConverter converter;

    private QueueFacade underTest;

    @Mock
    private Game game;

    @Mock
    private Universe universe;

    @Mock
    private Planet planet;

    @Mock
    private QueueItem queueItem;

    @Mock
    private QueueResponse queueResponse;

    @Before
    public void setUp() {
        underTest = QueueFacade.builder()
            .gameDao(gameDao)
            .services(List.of(queueService))
            .converter(converter)
            .build();

        given(queueService.getType()).willReturn(QueueItemType.TERRAFORMATION);
    }

    @Test
    public void getQueueOfPlanet() {
        given(gameDao.findByUserIdValidated(USER_ID)).willReturn(game);
        given(game.getUniverse()).willReturn(universe);
        given(universe.findByOwnerAndPlanetIdValidated(USER_ID, PLANET_ID)).willReturn(planet);
        given(queueService.getQueue(planet)).willReturn(List.of(queueItem));
        given(converter.convert(queueItem, planet)).willReturn(queueResponse);

        List<QueueResponse> result = underTest.getQueueOfPlanet(USER_ID, PLANET_ID);

        assertThat(result).containsExactly(queueResponse);
    }

    @Test
    public void setPriority_invalidType() {
        Throwable ex = catchThrowable(() -> underTest.setPriority(USER_ID, PLANET_ID, "asd", ITEM_ID, PRIORITY));

        ExceptionValidator.validateInvalidParam(ex, "type", "invalid value");
    }

    @Test
    public void setPriority_serviceNotFound() {
        Throwable ex = catchThrowable(() -> underTest.setPriority(USER_ID, PLANET_ID, QueueItemType.CONSTRUCTION.name(), ITEM_ID, PRIORITY));

        ExceptionValidator.validateLoggedException(ex, HttpStatus.NOT_IMPLEMENTED, ErrorCode.GENERAL_ERROR);
    }

    @Test
    public void setPriority() {
        underTest.setPriority(USER_ID, PLANET_ID, QueueItemType.TERRAFORMATION.name(), ITEM_ID, PRIORITY);

        verify(queueService).setPriority(USER_ID, PLANET_ID, ITEM_ID, PRIORITY);
    }

    @Test
    public void cancelItem_invalidType() {
        Throwable ex = catchThrowable(() -> underTest.cancelItem(USER_ID, PLANET_ID, "asd", ITEM_ID));

        ExceptionValidator.validateInvalidParam(ex, "type", "invalid value");
    }

    @Test
    public void cancelItem_serviceNotFound() {
        Throwable ex = catchThrowable(() -> underTest.cancelItem(USER_ID, PLANET_ID, QueueItemType.CONSTRUCTION.name(), ITEM_ID));

        ExceptionValidator.validateLoggedException(ex, HttpStatus.NOT_IMPLEMENTED, ErrorCode.GENERAL_ERROR);
    }

    @Test
    public void cancelItem() {
        underTest.cancelItem(USER_ID, PLANET_ID, QueueItemType.TERRAFORMATION.name(), ITEM_ID);

        verify(queueService).cancel(USER_ID, PLANET_ID, ITEM_ID);
    }
}