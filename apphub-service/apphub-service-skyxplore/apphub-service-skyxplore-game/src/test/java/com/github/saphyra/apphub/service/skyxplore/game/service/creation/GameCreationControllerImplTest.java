package com.github.saphyra.apphub.service.skyxplore.game.service.creation;

import com.github.saphyra.apphub.api.skyxplore.request.game_creation.SkyXploreGameCreationRequest;
import com.github.saphyra.apphub.api.skyxplore.request.game_creation.SkyXploreLoadGameRequest;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.LoadGameService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;
import java.util.concurrent.BlockingQueue;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class GameCreationControllerImplTest {
    private static final UUID GAME_ID = UUID.randomUUID();

    @Mock
    private BlockingQueue<BiWrapper<SkyXploreGameCreationRequest, UUID>> requests;

    @Mock
    private GameCreationRequestValidator validator;

    @Mock
    private LoadGameService loadGameService;

    @Mock
    private IdGenerator idGenerator;

    @InjectMocks
    private GameCreationControllerImpl underTest;

    @Mock
    private SkyXploreGameCreationRequest request;

    @Mock
    private SkyXploreLoadGameRequest loadGameRequest;

    @Test
    public void createGame() throws InterruptedException {
        given(idGenerator.randomUuid()).willReturn(GAME_ID);

        assertThat(underTest.createGame(request)).isEqualTo(GAME_ID);

        verify(validator).validate(request);
        verify(requests).put(new BiWrapper<>(request, GAME_ID));
    }

    @Test
    public void loadGame() {
        underTest.loadGame(loadGameRequest);

        verify(loadGameService).loadGame(loadGameRequest);
    }
}