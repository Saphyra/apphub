package com.github.saphyra.apphub.service.skyxplore.lobby.proxy;

import com.github.saphyra.apphub.api.skyxplore.game.client.SkyXploreGameCreationApiClient;
import com.github.saphyra.apphub.api.skyxplore.request.game_creation.SkyXploreLoadGameRequest;
import com.github.saphyra.apphub.lib.web_utils.LocaleProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class SkyXploreGameProxyTest {
    private static final String LOCALE = "locale";

    @Mock
    private SkyXploreGameCreationApiClient gameCreationClient;

    @Mock
    private LocaleProvider localeProvider;

    @InjectMocks
    private SkyXploreGameProxy underTest;

    @Mock
    private SkyXploreLoadGameRequest loadGameRequest;

    @Test
    public void loadGame() {
        given(localeProvider.getLocaleValidated()).willReturn(LOCALE);

        underTest.loadGame(loadGameRequest);

        verify(gameCreationClient).loadGame(loadGameRequest, LOCALE);
    }
}