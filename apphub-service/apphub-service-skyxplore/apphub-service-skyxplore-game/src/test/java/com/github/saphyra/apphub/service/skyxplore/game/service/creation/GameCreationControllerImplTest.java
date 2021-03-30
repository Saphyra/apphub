package com.github.saphyra.apphub.service.skyxplore.game.service.creation;

import static org.mockito.Mockito.verify;

import java.util.concurrent.BlockingQueue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.github.saphyra.apphub.api.skyxplore.request.game_creation.SkyXploreGameCreationRequest;

@RunWith(MockitoJUnitRunner.class)
public class GameCreationControllerImplTest {
    @Mock
    private BlockingQueue<SkyXploreGameCreationRequest> requests;

    @Mock
    private GameCreationRequestValidator validator;

    @InjectMocks
    private GameCreationControllerImpl underTest;

    @Mock
    private SkyXploreGameCreationRequest request;

    @Test
    public void createGame() throws InterruptedException {
        underTest.createGame(request);

        verify(validator).validate(request);
        verify(requests).put(request);
    }
}