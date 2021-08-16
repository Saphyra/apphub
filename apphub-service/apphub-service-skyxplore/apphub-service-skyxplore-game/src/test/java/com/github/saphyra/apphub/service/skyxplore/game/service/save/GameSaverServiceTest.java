package com.github.saphyra.apphub.service.skyxplore.game.service.save;

import com.github.saphyra.apphub.api.skyxplore.data.client.SkyXploreSavedGameClient;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBean;
import com.github.saphyra.apphub.lib.web_utils.CustomLocaleProvider;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.service.save.converter.GameToGameItemListConverter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class GameSaverServiceTest {
    private static final String LOCALE = "locale";

    @Mock
    private GameToGameItemListConverter converter;

    @Mock
    private ExecutorServiceBean executorServiceBean;

    @Mock
    private SaverProperties saverProperties;

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
        given(executorServiceBean.processWithWait(eq(game), any())).willReturn(Arrays.asList(gameItem, gameItem));
        given(saverProperties.getMaxChunkSize()).willReturn(1);
        given(customLocaleProvider.getLocale()).willReturn(LOCALE);

        underTest.save(game);

        verify(gameClient, times(2)).saveGameData(Arrays.asList(gameItem), LOCALE);
    }
}