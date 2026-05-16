package com.github.saphyra.apphub.service.feature.skyxplore.game.proxy;

import com.github.saphyra.apphub.api.feature.skyxplore.data.client.SkyXploreSavedGameClient;
import com.github.saphyra.apphub.api.feature.skyxplore.model.game.GameItem;
import com.github.saphyra.apphub.api.feature.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.feature.skyxplore.model.game.GameModel;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.service.feature.skyxplore.game.config.properties.GameProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class GameDataProxyTest {
    private static final UUID ID = UUID.randomUUID();
    private static final UUID GAME_ID = UUID.randomUUID();

    @Mock
    private SkyXploreSavedGameClient dataGameClient;

    @Mock
    private GameProperties gameProperties;

    @InjectMocks
    private GameDataProxy underTest;

    @Mock
    private GameItem gameItem;

    @Mock
    private GameModel gameModel;

    @Test
    public void saveGameData() {
        given(gameProperties.getItemSaverMaxChunkSize()).willReturn(1);

        underTest.saveItem(gameItem);

        verify(dataGameClient).saveGameData(List.of(gameItem));
    }

    @Test
    public void deleteItem() {
        underTest.deleteItem(ID, GameItemType.PLAYER);

        verify(dataGameClient).deleteGameItem(List.of(new BiWrapper<>(ID, GameItemType.PLAYER)));
    }

    @Test
    public void deleteItems() {
        underTest.deleteItems(List.of(new BiWrapper<>(ID, GameItemType.ALLIANCE)));

        verify(dataGameClient).deleteGameItem(List.of(new BiWrapper<>(ID, GameItemType.ALLIANCE)));
    }

    @Test
    void getGameModel() {
        given(dataGameClient.getGameModel(GAME_ID)).willReturn(gameModel);

        assertThat(underTest.getGameModel(GAME_ID)).isEqualTo(gameModel);
    }
}