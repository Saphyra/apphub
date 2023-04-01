package com.github.saphyra.apphub.service.skyxplore.game.proxy;

import com.github.saphyra.apphub.api.skyxplore.data.client.SkyXploreSavedGameClient;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameModel;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.web_utils.LocaleProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class GameDataProxyTest {
    private static final String LOCALE = "locale";
    private static final UUID ID = UUID.randomUUID();
    private static final UUID GAME_ID = UUID.randomUUID();

    @Mock
    private SkyXploreSavedGameClient dataGameClient;

    @Mock
    private LocaleProvider localeProvider;

    @InjectMocks
    private GameDataProxy underTest;

    @Mock
    private GameItem gameItem;

    @Mock
    private GameModel gameModel;

    @BeforeEach
    public void setUp() {
        given(localeProvider.getOrDefault()).willReturn(LOCALE);
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

    @Test
    void getGameModel() {
        given(dataGameClient.getGameModel(GAME_ID, LOCALE)).willReturn(gameModel);

        assertThat(underTest.getGameModel(GAME_ID)).isEqualTo(gameModel);
    }
}