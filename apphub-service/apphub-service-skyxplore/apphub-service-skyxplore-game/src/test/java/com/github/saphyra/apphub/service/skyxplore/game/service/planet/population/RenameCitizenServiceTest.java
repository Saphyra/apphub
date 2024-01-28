package com.github.saphyra.apphub.service.skyxplore.game.service.planet.population;

import com.github.saphyra.apphub.api.skyxplore.model.game.CitizenModel;
import com.github.saphyra.apphub.lib.concurrency.ExecutionResult;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.GameProgressDiff;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen.Citizen;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen.CitizenConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen.Citizens;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.event_loop.EventLoop;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class RenameCitizenServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID CITIZEN_ID = UUID.randomUUID();
    private static final String NEW_NAME = "new-name";
    private static final UUID GAME_ID = UUID.randomUUID();

    @Mock
    private GameDao gameDao;

    @Mock
    private CitizenConverter citizenConverter;

    @InjectMocks
    private RenameCitizenService underTest;

    @Mock
    private Game game;

    @Mock
    private GameData gameData;

    @Mock
    private Citizen citizen;

    @Mock
    private CitizenModel citizenModel;

    @Mock
    private EventLoop eventLoop;

    @Mock
    private ExecutionResult<Void> executionResult;

    @Mock
    private Citizens citizens;

    @Mock
    private GameProgressDiff progressDiff;

    @Captor
    private ArgumentCaptor<Runnable> argumentCaptor;

    @Test
    public void blankCitizenName() {
        Throwable ex = catchThrowable(() -> underTest.renameCitizen(USER_ID, CITIZEN_ID, " "));

        ExceptionValidator.validateInvalidParam(ex, "value", "must not be null or blank");
    }

    @Test
    public void citizenNameTooLong() {
        Throwable ex = catchThrowable(() -> underTest.renameCitizen(USER_ID, CITIZEN_ID, Stream.generate(() -> "a").limit(31).collect(Collectors.joining())));

        ExceptionValidator.validateInvalidParam(ex, "value", "too long");
    }

    @Test
    public void renameCitizen() {
        given(gameDao.findByUserIdValidated(USER_ID)).willReturn(game);
        given(game.getData()).willReturn(gameData);
        given(gameData.getCitizens()).willReturn(citizens);
        given(citizens.findByCitizenIdValidated(CITIZEN_ID)).willReturn(citizen);
        given(game.getEventLoop()).willReturn(eventLoop);
        given(game.getGameId()).willReturn(GAME_ID);
        given(eventLoop.processWithWait(any(Runnable.class))).willReturn(executionResult);
        given(citizenConverter.toModel(GAME_ID, citizen)).willReturn(citizenModel);
        given(game.getProgressDiff()).willReturn(progressDiff);

        underTest.renameCitizen(USER_ID, CITIZEN_ID, NEW_NAME);

        verify(eventLoop).processWithWait(argumentCaptor.capture());
        argumentCaptor.getValue()
            .run();

        verify(citizen).setName(NEW_NAME);
        verify(progressDiff).save(citizenModel);
        then(executionResult).should().getOrThrow();
    }
}