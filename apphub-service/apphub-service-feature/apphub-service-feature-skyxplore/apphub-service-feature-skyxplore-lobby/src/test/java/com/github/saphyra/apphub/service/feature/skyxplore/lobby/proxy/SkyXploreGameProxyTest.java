package com.github.saphyra.apphub.service.feature.skyxplore.lobby.proxy;

import com.github.saphyra.apphub.api.feature.skyxplore.game.client.SkyXploreGameCreationApiClient;
import com.github.saphyra.apphub.api.feature.skyxplore.request.game_creation.SkyXploreLoadGameRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class SkyXploreGameProxyTest {
    @Mock
    private SkyXploreGameCreationApiClient gameCreationClient;

    @InjectMocks
    private SkyXploreGameProxy underTest;

    @Mock
    private SkyXploreLoadGameRequest loadGameRequest;

    @Test
    public void loadGame() {
        underTest.loadGame(loadGameRequest);

        verify(gameCreationClient).loadGame(loadGameRequest);
    }
}