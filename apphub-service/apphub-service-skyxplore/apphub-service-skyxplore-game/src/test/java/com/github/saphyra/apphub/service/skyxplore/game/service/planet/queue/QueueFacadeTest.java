package com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue;

import com.github.saphyra.apphub.api.skyxplore.response.game.planet.overview.QueueResponse;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.QueueItemType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planets;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue.service.QueueService;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class QueueFacadeTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID PLANET_ID = UUID.randomUUID();
    private static final UUID ITEM_ID = UUID.randomUUID();
    private static final Integer PRIORITY = 2;

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
    private GameData gameData;

    @Mock
    private QueueItem queueItem;

    @Mock
    private QueueResponse queueResponse;

    @Mock
    private Planets planets;

    @Mock
    private Planet planet;

    @BeforeEach
    public void setUp() {
        underTest = QueueFacade.builder()
            .gameDao(gameDao)
            .services(List.of(queueService))
            .converter(converter)
            .build();
    }

    @Test
    void getQueueOfPlanet_gameNotFound() {
        given(gameDao.findByUserId(USER_ID)).willReturn(Optional.empty());

        assertThat(underTest.getQueueOfPlanet(USER_ID, PLANET_ID)).isEmpty();
    }

    @Test
    public void getQueueOfPlanet() {
        given(gameDao.findByUserId(USER_ID)).willReturn(Optional.of(game));
        given(game.getData()).willReturn(gameData);
        given(queueService.getQueue(gameData, PLANET_ID)).willReturn(List.of(queueItem));
        given(converter.convert(queueItem, gameData, PLANET_ID)).willReturn(queueResponse);

        List<QueueResponse> result = underTest.getQueueOfPlanet(USER_ID, PLANET_ID);

        assertThat(result).containsExactly(queueResponse);
    }

    @Test
    public void setPriority_invalidType() {
        Throwable ex = catchThrowable(() -> underTest.setPriority(USER_ID, PLANET_ID, "asd", ITEM_ID, PRIORITY));

        ExceptionValidator.validateInvalidParam(ex, "type", "invalid value");
    }

    @Test
    public void setPriority_tooLow() {
        Throwable ex = catchThrowable(() -> underTest.setPriority(USER_ID, PLANET_ID, QueueItemType.TERRAFORMATION.name(), ITEM_ID, 0));

        ExceptionValidator.validateInvalidParam(ex, "priority", "too low");
    }

    @Test
    public void setPriority_tooHigh() {
        Throwable ex = catchThrowable(() -> underTest.setPriority(USER_ID, PLANET_ID, QueueItemType.TERRAFORMATION.name(), ITEM_ID, 11));

        ExceptionValidator.validateInvalidParam(ex, "priority", "too high");
    }

    @Test
    void setPriority_forbiddenOperation() {
        given(gameDao.findByUserIdValidated(USER_ID)).willReturn(game);
        given(game.getData()).willReturn(gameData);
        given(gameData.getPlanets()).willReturn(planets);
        given(planets.findByIdValidated(PLANET_ID)).willReturn(planet);
        given(planet.getOwner()).willReturn(UUID.randomUUID());

        ExceptionValidator.validateForbiddenOperation(() -> underTest.setPriority(USER_ID, PLANET_ID, QueueItemType.TERRAFORMATION.name(), ITEM_ID, PRIORITY));
    }

    @Test
    public void setPriority_serviceNotFound() {
        given(queueService.getType()).willReturn(QueueItemType.TERRAFORMATION);
        given(gameDao.findByUserIdValidated(USER_ID)).willReturn(game);
        given(game.getData()).willReturn(gameData);
        given(gameData.getPlanets()).willReturn(planets);
        given(planets.findByIdValidated(PLANET_ID)).willReturn(planet);
        given(planet.getOwner()).willReturn(USER_ID);

        Throwable ex = catchThrowable(() -> underTest.setPriority(USER_ID, PLANET_ID, QueueItemType.CONSTRUCT_CONSTRUCTION_AREA.name(), ITEM_ID, PRIORITY));

        ExceptionValidator.validateLoggedException(ex, HttpStatus.NOT_IMPLEMENTED, ErrorCode.GENERAL_ERROR);
    }

    @Test
    public void setPriority() {
        given(queueService.getType()).willReturn(QueueItemType.TERRAFORMATION);
        given(gameDao.findByUserIdValidated(USER_ID)).willReturn(game);
        given(game.getData()).willReturn(gameData);
        given(gameData.getPlanets()).willReturn(planets);
        given(planets.findByIdValidated(PLANET_ID)).willReturn(planet);
        given(planet.getOwner()).willReturn(USER_ID);

        underTest.setPriority(USER_ID, PLANET_ID, QueueItemType.TERRAFORMATION.name(), ITEM_ID, PRIORITY);

        verify(queueService).setPriority(USER_ID, PLANET_ID, ITEM_ID, PRIORITY);
    }

    @Test
    public void cancelItem_invalidType() {
        Throwable ex = catchThrowable(() -> underTest.cancelItem(USER_ID, PLANET_ID, "asd", ITEM_ID));

        ExceptionValidator.validateInvalidParam(ex, "type", "invalid value");
    }

    @Test
    void cancelItem_forbiddenOperation() {
        given(gameDao.findByUserIdValidated(USER_ID)).willReturn(game);
        given(game.getData()).willReturn(gameData);
        given(gameData.getPlanets()).willReturn(planets);
        given(planets.findByIdValidated(PLANET_ID)).willReturn(planet);
        given(planet.getOwner()).willReturn(UUID.randomUUID());

        ExceptionValidator.validateForbiddenOperation(() -> underTest.cancelItem(USER_ID, PLANET_ID, QueueItemType.CONSTRUCT_CONSTRUCTION_AREA.name(), ITEM_ID));
    }

    @Test
    public void cancelItem_serviceNotFound() {
        given(queueService.getType()).willReturn(QueueItemType.TERRAFORMATION);
        given(gameDao.findByUserIdValidated(USER_ID)).willReturn(game);
        given(game.getData()).willReturn(gameData);
        given(gameData.getPlanets()).willReturn(planets);
        given(planets.findByIdValidated(PLANET_ID)).willReturn(planet);
        given(planet.getOwner()).willReturn(USER_ID);

        Throwable ex = catchThrowable(() -> underTest.cancelItem(USER_ID, PLANET_ID, QueueItemType.CONSTRUCT_CONSTRUCTION_AREA.name(), ITEM_ID));

        ExceptionValidator.validateLoggedException(ex, HttpStatus.NOT_IMPLEMENTED, ErrorCode.GENERAL_ERROR);
    }

    @Test
    public void cancelItem() {
        given(queueService.getType()).willReturn(QueueItemType.TERRAFORMATION);
        given(gameDao.findByUserIdValidated(USER_ID)).willReturn(game);
        given(game.getData()).willReturn(gameData);
        given(gameData.getPlanets()).willReturn(planets);
        given(planets.findByIdValidated(PLANET_ID)).willReturn(planet);
        given(planet.getOwner()).willReturn(USER_ID);

        underTest.cancelItem(USER_ID, PLANET_ID, QueueItemType.TERRAFORMATION.name(), ITEM_ID);

        verify(queueService).cancel(USER_ID, PLANET_ID, ITEM_ID);
    }
}