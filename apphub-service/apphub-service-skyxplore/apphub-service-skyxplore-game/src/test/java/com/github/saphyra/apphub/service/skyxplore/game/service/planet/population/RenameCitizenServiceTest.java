package com.github.saphyra.apphub.service.skyxplore.game.service.planet.population;

import com.github.saphyra.apphub.api.skyxplore.model.game.CitizenModel;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.CitizenResponse;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.common_util.collection.OptionalHashMap;
import com.github.saphyra.apphub.lib.concurrency.ExecutionResult;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.common.converter.response.CitizenToResponseConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.citizen.Citizen;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Universe;
import com.github.saphyra.apphub.service.skyxplore.game.process.event_loop.EventLoop;
import com.github.saphyra.apphub.service.skyxplore.game.proxy.GameDataProxy;
import com.github.saphyra.apphub.service.skyxplore.game.service.save.converter.CitizenToModelConverter;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;

import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class RenameCitizenServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID PLANET_ID = UUID.randomUUID();
    private static final UUID CITIZEN_ID = UUID.randomUUID();
    private static final String NEW_NAME = "new-name";
    private static final UUID GAME_ID = UUID.randomUUID();

    @Mock
    private GameDao gameDao;

    @Mock
    private GameDataProxy gameDataProxy;

    @Mock
    private CitizenToModelConverter citizenToModelConverter;

    @Mock
    private CitizenToResponseConverter citizenToResponseConverter;

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

    @Mock
    private CitizenModel citizenModel;

    @Mock
    private EventLoop eventLoop;

    @Mock
    private ExecutionResult<CitizenResponse> executionResult;

    @Mock
    private CitizenResponse citizenResponse;

    @Captor
    private ArgumentCaptor<Callable<CitizenResponse>> argumentCaptor;

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
        given(universe.findByOwnerAndPlanetIdValidated(USER_ID, PLANET_ID)).willReturn(planet);
        given(planet.getPopulation()).willReturn(new OptionalHashMap<>());

        Throwable ex = catchThrowable(() -> underTest.renameCitizen(USER_ID, PLANET_ID, CITIZEN_ID, NEW_NAME));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.NOT_FOUND, ErrorCode.GENERAL_ERROR);
    }

    @Test
    public void renameCitizen() throws Exception {
        given(gameDao.findByUserIdValidated(USER_ID)).willReturn(game);
        given(game.getUniverse()).willReturn(universe);
        given(game.getEventLoop()).willReturn(eventLoop);
        given(universe.findByOwnerAndPlanetIdValidated(USER_ID, PLANET_ID)).willReturn(planet);
        given(planet.getPopulation()).willReturn(new OptionalHashMap<>(CollectionUtils.singleValueMap(CITIZEN_ID, citizen)));
        given(citizenToModelConverter.convert(citizen, GAME_ID)).willReturn(citizenModel);
        given(game.getGameId()).willReturn(GAME_ID);
        given(eventLoop.processWithResponseAndWait(any(Callable.class))).willReturn(executionResult);
        given(executionResult.getOrThrow()).willReturn(citizenResponse);
        given(citizenToResponseConverter.convert(citizen)).willReturn(citizenResponse);

        CitizenResponse result = underTest.renameCitizen(USER_ID, PLANET_ID, CITIZEN_ID, NEW_NAME);

        verify(eventLoop).processWithResponseAndWait(argumentCaptor.capture());
        CitizenResponse mapperResult = argumentCaptor.getValue().call();
        assertThat(mapperResult).isEqualTo(citizenResponse);

        verify(citizen).setName(NEW_NAME);
        verify(gameDataProxy).saveItem(citizenModel);

        assertThat(result).isEqualTo(citizenResponse);
    }
}