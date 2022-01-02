package com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue.service.terraformation;

import com.github.saphyra.apphub.api.skyxplore.model.game.ConstructionModel;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameConstants;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Construction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Surface;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.SurfaceMap;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Universe;
import com.github.saphyra.apphub.service.skyxplore.game.proxy.GameDataProxy;
import com.github.saphyra.apphub.service.skyxplore.game.service.save.converter.ConstructionToModelConverter;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;

import java.util.UUID;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class TerraformationQueueItemPriorityUpdateServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID PLANET_ID = UUID.randomUUID();
    private static final UUID CONSTRUCTION_ID = UUID.randomUUID();
    private static final Integer PRIORITY = 5;
    private static final UUID GAME_ID = UUID.randomUUID();

    @Mock
    private GameDao gameDao;

    @Mock
    private GameDataProxy gameDataProxy;

    @Mock
    private ConstructionToModelConverter constructionToModelConverter;

    @InjectMocks
    private TerraformationQueueItemPriorityUpdateService underTest;

    @Mock
    private Game game;

    @Mock
    private Universe universe;

    @Mock
    private Planet planet;

    @Mock
    private Surface surface;

    @Mock
    private Construction construction;

    @Mock
    private ConstructionModel constructionModel;

    @Before
    public void setUp() {
        given(gameDao.findByUserIdValidated(USER_ID)).willReturn(game);
        given(game.getGameId()).willReturn(GAME_ID);
        given(game.getUniverse()).willReturn(universe);
        given(universe.findByOwnerAndPlanetIdValidated(USER_ID, PLANET_ID)).willReturn(planet);
        given(planet.getSurfaces()).willReturn(new SurfaceMap(CollectionUtils.singleValueMap(GameConstants.ORIGO, surface)));
    }

    @Test
    public void priorityTooLow() {
        Throwable ex = catchThrowable(() -> underTest.updatePriority(USER_ID, PLANET_ID, CONSTRUCTION_ID, 0));

        ExceptionValidator.validateInvalidParam(ex, "priority", "too low");
    }

    @Test
    public void priorityTooHigh() {
        Throwable ex = catchThrowable(() -> underTest.updatePriority(USER_ID, PLANET_ID, CONSTRUCTION_ID, 11));

        ExceptionValidator.validateInvalidParam(ex, "priority", "too high");
    }

    @Test
    public void surfaceNotFound() {
        given(surface.getTerraformation()).willReturn(null);

        Throwable ex = catchThrowable(() -> underTest.updatePriority(USER_ID, PLANET_ID, CONSTRUCTION_ID, PRIORITY));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND);
    }

    @Test
    public void updatePriority() {
        given(surface.getTerraformation()).willReturn(construction);
        given(construction.getConstructionId()).willReturn(CONSTRUCTION_ID);
        given(constructionToModelConverter.convert(construction, GAME_ID)).willReturn(constructionModel);

        underTest.updatePriority(USER_ID, PLANET_ID, CONSTRUCTION_ID, PRIORITY);

        verify(construction).setPriority(PRIORITY);
        verify(gameDataProxy).saveItem(constructionModel);
    }
}