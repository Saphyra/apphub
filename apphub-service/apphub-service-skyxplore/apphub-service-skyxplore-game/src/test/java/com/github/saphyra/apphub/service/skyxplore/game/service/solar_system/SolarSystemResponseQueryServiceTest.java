package com.github.saphyra.apphub.service.skyxplore.game.service.solar_system;

import com.github.saphyra.apphub.api.skyxplore.response.game.solar_system.PlanetLocationResponse;
import com.github.saphyra.apphub.api.skyxplore.response.game.solar_system.SolarSystemResponse;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.solar_system.SolarSystem;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.solar_system.SolarSystems;
import com.github.saphyra.apphub.service.skyxplore.game.service.visibility.VisibilityFacade;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class SolarSystemResponseQueryServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID SOLAR_SYSTEM_ID = UUID.randomUUID();
    private static final String DEFAULT_NAME = "default-name";
    private static final Integer RADIUS = 235;

    @Mock
    private VisibilityFacade visibilityFacade;

    @Mock
    private GameDao gameDao;

    @Mock
    private PlanetToLocationResponseConverter planetToLocationResponseConverter;

    @InjectMocks
    private SolarSystemResponseQueryService underTest;

    @Mock
    private Game game;

    @Mock
    private GameData gameData;

    @Mock
    private SolarSystem solarSystem;

    @Mock
    private PlanetLocationResponse planetLocationResponse;

    @Mock
    private SolarSystems solarSystems;

    @Test
    public void getSolarSystem_notVisible() {
        given(gameDao.findByUserIdValidated(USER_ID)).willReturn(game);
        given(game.getData()).willReturn(gameData);
        given(gameData.getSolarSystems()).willReturn(solarSystems);
        given(solarSystems.findByIdValidated(SOLAR_SYSTEM_ID)).willReturn(solarSystem);

        given(solarSystem.getSolarSystemId()).willReturn(SOLAR_SYSTEM_ID);
        given(visibilityFacade.isVisible(gameData, USER_ID, SOLAR_SYSTEM_ID)).willReturn(false);

        Throwable ex = catchThrowable(() -> underTest.getSolarSystem(USER_ID, SOLAR_SYSTEM_ID));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.FORBIDDEN, ErrorCode.ITEM_NOT_VISIBLE);
    }

    @Test
    public void getSolarSystem() {
        given(gameDao.findByUserIdValidated(USER_ID)).willReturn(game);
        given(game.getData()).willReturn(gameData);
        given(gameData.getSolarSystems()).willReturn(solarSystems);
        given(solarSystems.findByIdValidated(SOLAR_SYSTEM_ID)).willReturn(solarSystem);

        given(solarSystem.getSolarSystemId()).willReturn(SOLAR_SYSTEM_ID);
        given(visibilityFacade.isVisible(gameData, USER_ID, SOLAR_SYSTEM_ID)).willReturn(true);

        given(planetToLocationResponseConverter.mapPlanets(game, SOLAR_SYSTEM_ID, USER_ID));

        SolarSystemResponse result = underTest.getSolarSystem(USER_ID, SOLAR_SYSTEM_ID);

        assertThat(result.getSolarSystemId()).isEqualTo(SOLAR_SYSTEM_ID);
        assertThat(result.getSystemName()).isEqualTo(DEFAULT_NAME);
        assertThat(result.getRadius()).isEqualTo(RADIUS);
        assertThat(result.getPlanets()).containsExactly(planetLocationResponse);
    }
}