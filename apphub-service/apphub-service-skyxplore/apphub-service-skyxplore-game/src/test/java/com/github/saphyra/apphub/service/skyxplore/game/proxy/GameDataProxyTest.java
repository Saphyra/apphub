package com.github.saphyra.apphub.service.skyxplore.game.proxy;

import com.github.saphyra.apphub.api.skyxplore.data.client.SkyXploreSavedGameClient;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
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
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class GameDataProxyTest {
    private static final String LOCALE = "locale";
    private static final UUID ID = UUID.randomUUID();
    private static final String RESULT = "result";

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
        given(dataGameClient.loadGameItem(ID, GameItemType.GAME, LOCALE)).willReturn(RESULT);

        String result = underTest.loadItem(ID, GameItemType.GAME);

        assertThat(result).isEqualTo(RESULT);
    }

    @Test
    public void loadChildren() {
        given(dataGameClient.loadChildrenOfGameItem(ID, GameItemType.GAME, LOCALE)).willReturn(RESULT);

        String result = underTest.loadChildren(ID, GameItemType.GAME);

        assertThat(result).isEqualTo(RESULT);
    }

    @Test
    public void saveGameData() {
        underTest.saveItem(gameItem);

        verify(dataGameClient).saveGameData(Arrays.asList(gameItem), LOCALE);
    }

    @Test
    public void deleteItem() {
        underTest.deleteItem(ID, GameItemType.PLAYER);

        verify(dataGameClient).deleteGameItem(List.of(new BiWrapper<>(ID, GameItemType.PLAYER)), LOCALE);
    }

    @Test
    public void deleteItems() {
        underTest.deleteItems(List.of(new BiWrapper<>(ID, GameItemType.ALLIANCE)));

        verify(dataGameClient).deleteGameItem(List.of(new BiWrapper<>(ID, GameItemType.ALLIANCE)), LOCALE);
    }
}