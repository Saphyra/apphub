package com.github.saphyra.apphub.service.skyxplore.game.service.solar_system;

import com.github.saphyra.apphub.api.skyxplore.model.game.SolarSystemModel;
import com.github.saphyra.apphub.lib.common_util.collection.OptionalHashMap;
import com.github.saphyra.apphub.lib.common_util.collection.OptionalMap;
import com.github.saphyra.apphub.lib.concurrency.ExecutionResult;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.solar_system.SolarSystem;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.solar_system.SolarSystems;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.event_loop.EventLoop;
import com.github.saphyra.apphub.service.skyxplore.game.proxy.GameDataProxy;
import com.github.saphyra.apphub.service.skyxplore.game.service.save.converter.SolarSystemToModelConverter;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class RenameSolarSystemServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID SOLAR_SYSTEM_ID = UUID.randomUUID();
    private static final String NEW_NAME = "new-name";
    private static final UUID GAME_ID = UUID.randomUUID();

    @Mock
    private GameDao gameDao;

    @Mock
    private GameDataProxy gameDataProxy;

    @Mock
    private SolarSystemToModelConverter solarSystemToModelConverter;

    @InjectMocks
    private RenameSolarSystemService underTest;

    @Mock
    private Game game;

    @Mock
    private GameData gameData;

    @Mock
    private SolarSystem solarSystem;

    @Mock
    private SolarSystemModel model;

    @Mock
    private EventLoop eventLoop;

    @Mock
    private ExecutionResult<Void> executionResult;

    @Mock
    private SolarSystems solarSystems;

    @Test
    public void blankName() {
        Throwable ex = catchThrowable(() -> underTest.rename(USER_ID, SOLAR_SYSTEM_ID, " "));

        ExceptionValidator.validateInvalidParam(ex, "newName", "must not be null or blank");
    }

    @Test
    public void tooLongName() {
        Throwable ex = catchThrowable(() -> underTest.rename(USER_ID, SOLAR_SYSTEM_ID, Stream.generate(() -> "a").limit(31).collect(Collectors.joining())));

        ExceptionValidator.validateInvalidParam(ex, "newName", "too long");
    }

    @Test
    public void rename() {
        given(gameDao.findByUserIdValidated(USER_ID)).willReturn(game);
        given(game.getData()).willReturn(gameData);
        given(gameData.getSolarSystems()).willReturn(solarSystems);
        given(solarSystems.findByIdValidated(SOLAR_SYSTEM_ID)).willReturn(solarSystem);
        given(game.getEventLoop()).willReturn(eventLoop);
        given(eventLoop.processWithWait(any())).willReturn(executionResult);

        OptionalMap<UUID, String> customNames = new OptionalHashMap<>();
        given(solarSystem.getCustomNames()).willReturn(customNames);
        given(solarSystemToModelConverter.convert(GAME_ID, solarSystem)).willReturn(model);
        given(gameData.getGameId()).willReturn(GAME_ID);

        underTest.rename(USER_ID, SOLAR_SYSTEM_ID, NEW_NAME);

        ArgumentCaptor<Runnable> argumentCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(eventLoop).processWithWait(argumentCaptor.capture());
        argumentCaptor.getValue()
            .run();

        verify(gameDataProxy).saveItem(model);
        assertThat(customNames).containsEntry(USER_ID, NEW_NAME);
    }
}