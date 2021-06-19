package com.github.saphyra.apphub.service.skyxplore.game.service.planet.population;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.common_util.collection.OptionalHashMap;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.Citizen;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Universe;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;

import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class RenameCitizenServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID PLANET_ID = UUID.randomUUID();
    private static final UUID CITIZEN_ID = UUID.randomUUID();
    private static final String NEW_NAME = "new-name";

    @Mock
    private GameDao gameDao;

    @InjectMocks
    private RenameCitizenService underTest;

    @Mock
    private Game game;

    @Mock
    private Universe universe;

    @Mock
    private Planet planet;

    @Mock
    private Citizen citizen;

    @Test
    public void blankCitizenName() {
        Throwable ex = catchThrowable(() -> underTest.renameCitizen(USER_ID, PLANET_ID, CITIZEN_ID, " "));

        ExceptionValidator.validateInvalidParam(ex, "value", "must not be null or blank");
    }

    @Test
    public void citizenNameTooLong() {
        Throwable ex = catchThrowable(() -> underTest.renameCitizen(USER_ID, PLANET_ID, CITIZEN_ID, Stream.generate(() -> "a").limit(31).collect(Collectors.joining())));

        ExceptionValidator.validateInvalidParam(ex, "value", "too long");
    }

    @Test
    public void citizenNotFound() {
        given(gameDao.findByUserIdValidated(USER_ID)).willReturn(game);
        given(game.getUniverse()).willReturn(universe);
        given(universe.findPlanetByIdValidated(PLANET_ID)).willReturn(planet);
        given(planet.getPopulation()).willReturn(new OptionalHashMap<>());

        Throwable ex = catchThrowable(() -> underTest.renameCitizen(USER_ID, PLANET_ID, CITIZEN_ID, NEW_NAME));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.NOT_FOUND, ErrorCode.GENERAL_ERROR);
    }

    @Test
    public void renameCitizen() {
        given(gameDao.findByUserIdValidated(USER_ID)).willReturn(game);
        given(game.getUniverse()).willReturn(universe);
        given(universe.findPlanetByIdValidated(PLANET_ID)).willReturn(planet);
        given(planet.getPopulation()).willReturn(new OptionalHashMap<>(CollectionUtils.singleValueMap(CITIZEN_ID, citizen)));

        underTest.renameCitizen(USER_ID, PLANET_ID, CITIZEN_ID, NEW_NAME);

        verify(citizen).setName(NEW_NAME);
    }
}