package com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation;

import com.github.saphyra.apphub.api.skyxplore.data.client.SkyXploreSavedGameClient;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
import com.github.saphyra.apphub.lib.web_utils.CustomLocaleProvider;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.GameProperties;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.GameConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class GameSaverServiceTest {
    private static final String LOCALE = "locale";

    @Mock
    private GameConverter converter;

    @Mock
    private GameProperties gameProperties;

    @Mock
    private SkyXploreSavedGameClient gameClient;

    @Mock
    private CustomLocaleProvider customLocaleProvider;

    @InjectMocks
    private GameSaverService underTest;

    @Mock
    private Game game;

    @Mock
    private GameItem gameItem;

    @Test
    public void save() {
        given(converter.convertDeep(game)).willReturn(Arrays.asList(gameItem, gameItem));
        given(gameProperties.getItemSaverMaxChunkSize()).willReturn(1);
        given(customLocaleProvider.getLocale()).willReturn(LOCALE);

        underTest.save(game);

        verify(gameClient, times(2)).saveGameData(Arrays.asList(gameItem), LOCALE);
    }
}