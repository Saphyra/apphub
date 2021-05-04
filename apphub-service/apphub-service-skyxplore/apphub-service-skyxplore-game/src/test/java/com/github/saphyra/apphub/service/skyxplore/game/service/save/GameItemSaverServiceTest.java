package com.github.saphyra.apphub.service.skyxplore.game.service.save;

import com.github.saphyra.apphub.api.skyxplore.data.client.SkyXploreDataGameClient;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
import com.github.saphyra.apphub.lib.common_util.SleepService;
import com.github.saphyra.apphub.service.skyxplore.game.common.CustomLocaleProvider;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class GameItemSaverServiceTest {
    private static final String LOCALE = "locale";

    @Mock
    private SaverProperties saverProperties;

    @Mock
    private SkyXploreDataGameClient gameClient;

    @Mock
    private CustomLocaleProvider customLocaleProvider;

    @Mock
    private SleepService sleepService;

    @InjectMocks
    private GameItemSaverService underTest;

    @Mock
    private GameItem gameItem;

    @Test
    public void saveAsync() {
        given(saverProperties.getMaxChunkSize()).willReturn(2);
        given(customLocaleProvider.getLocale()).willReturn(LOCALE);

        underTest.saveAsync(Arrays.asList(gameItem, gameItem, gameItem));

        verify(gameClient, timeout(1000)).saveGameData(Arrays.asList(gameItem, gameItem), LOCALE);
        verify(gameClient, timeout(1000)).saveGameData(Arrays.asList(gameItem), LOCALE);
    }
}