package com.github.saphyra.apphub.service.skyxplore.game.service.creation;

import com.github.saphyra.apphub.api.skyxplore.request.game_creation.SkyXploreGameCreationRequest;
import com.github.saphyra.apphub.api.skyxplore.request.game_creation.SkyXploreLoadGameRequest;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.LoadGameService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.concurrent.BlockingQueue;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class GameCreationControllerImplTest {
    @Mock
    private BlockingQueue<SkyXploreGameCreationRequest> requests;

    @Mock
    private GameCreationRequestValidator validator;

    @Mock
    private LoadGameService loadGameService;

    @InjectMocks
    private GameCreationControllerImpl underTest;

    @Mock
    private SkyXploreGameCreationRequest request;

    @Mock
    private SkyXploreLoadGameRequest loadGameRequest;

    @Test
    public void createGame() throws InterruptedException {
        underTest.createGame(request);

        verify(validator).validate(request);
        verify(requests).put(request);
    }

    @Test
    public void loadGame() {
        underTest.loadGame(loadGameRequest);

        verify(loadGameService).loadGame(loadGameRequest);
    }
}