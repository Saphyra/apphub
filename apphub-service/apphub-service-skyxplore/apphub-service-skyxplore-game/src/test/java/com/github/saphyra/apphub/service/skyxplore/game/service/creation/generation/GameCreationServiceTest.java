package com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation;

import com.github.saphyra.apphub.api.skyxplore.lobby.client.SkyXploreLobbyApiClient;
import com.github.saphyra.apphub.api.skyxplore.request.game_creation.SkyXploreGameCreationRequest;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_util.CommonConfigProperties;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBeenTestUtils;
import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory.GameFactory;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.tick.TickSchedulerLauncher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class GameCreationServiceTest {
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final String LOCALE = "locale";

    @Mock
    private SkyXploreLobbyApiClient lobbyClient;

    @Mock
    private GameFactory gameFactory;

    @Mock
    private GameDao gameDao;

    @Mock
    private GameSaverService gameSaverService;

    @Mock
    private TickSchedulerLauncher tickSchedulerLauncher;

    private final BlockingQueue<BiWrapper<SkyXploreGameCreationRequest, UUID>> requests = new ArrayBlockingQueue<>(1);

    @Mock
    private ErrorReporterService errorReporterService;

    @Mock
    private CommonConfigProperties commonConfigProperties;

    private GameCreationService underTest;

    @Mock
    private SkyXploreGameCreationRequest request;

    @Mock
    private Game game;

    @BeforeEach
    public void setUp() {
        underTest = GameCreationService.builder()
            .lobbyClient(lobbyClient)
            .gameFactory(gameFactory)
            .gameDao(gameDao)
            .requests(requests)
            .gameSaverService(gameSaverService)
            .executorServiceBeanFactory(ExecutorServiceBeenTestUtils.createFactory(Mockito.mock(ErrorReporterService.class)))
            .errorReporterService(errorReporterService)
            .tickSchedulerLauncher(tickSchedulerLauncher)
            .commonConfigProperties(commonConfigProperties)
            .build();
    }

    @Test
    public void create() throws InterruptedException {
        given(gameFactory.create(request, GAME_ID)).willReturn(game);
        given(commonConfigProperties.getDefaultLocale()).willReturn(LOCALE);

        underTest.createGames();

        requests.put(new BiWrapper<>(request, GAME_ID));

        verify(gameDao, timeout(1000)).save(game);

        then(lobbyClient).should(timeout(1000)).gameLoaded(GAME_ID, LOCALE);
        verify(gameSaverService).save(game);
        verify(tickSchedulerLauncher).launch(game);
    }
}