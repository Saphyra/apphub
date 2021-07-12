package com.github.saphyra.apphub.service.skyxplore.game.proxy;

import com.github.saphyra.apphub.api.skyxplore.data.client.SkyXploreSavedGameClient;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.lib.web_utils.LocaleProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class GameDataProxyTest {
    private static final String LOCALE = "locale";
    private static final UUID ID = UUID.randomUUID();

    @Mock
    private SkyXploreSavedGameClient dataGameClient;

    @Mock
    private LocaleProvider localeProvider;

    @InjectMocks
    private GameDataProxy underTest;

    @Mock
    private GameItem gameItem;

    @Before
    public void setUp() {
        given(localeProvider.getOrDefault()).willReturn(LOCALE);
    }

    @Test
    public void loadItem() {
        given(dataGameClient.loadGameItem(ID, GameItemType.GAME, LOCALE)).willReturn(gameItem);

        GameItem result = underTest.loadItem(ID, GameItemType.GAME);

        assertThat(result).isEqualTo(gameItem);
    }

    @Test
    public void loadChildren() {
        given(dataGameClient.loadChildrenOfGameItem(ID, GameItemType.GAME, LOCALE)).willReturn(Arrays.asList(gameItem));

        List<GameItem> result = underTest.loadChildren(ID, GameItemType.GAME);

        assertThat(result).containsExactly(gameItem);
    }
}