package com.github.saphyra.apphub.service.skyxplore.game.process.cache;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.service.skyxplore.game.proxy.GameDataProxy;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class GameItemCacheTest {
    private static final UUID GAME_ITEM_ID = UUID.randomUUID();

    @Mock
    private GameDataProxy gameDataProxy;

    private final GameItemCache underTest = new GameItemCache();

    @Mock
    private GameItem gameItem;

    @Before
    public void setUp() {
        given(gameItem.getId()).willReturn(GAME_ITEM_ID);
    }

    @Test
    public void save() {
        underTest.save(gameItem);

        assertThat(underTest.getItems()).containsEntry(GAME_ITEM_ID, gameItem);
    }

    @Test
    public void delete() {
        underTest.save(gameItem);

        underTest.delete(GAME_ITEM_ID, GameItemType.PRODUCTION_ORDER);

        assertThat(underTest.getItems()).isEmpty();
        assertThat(underTest.getDeletedItems()).containsExactly(new BiWrapper<>(GAME_ITEM_ID, GameItemType.PRODUCTION_ORDER));
    }

    @Test
    public void process() {
        underTest.delete(GAME_ITEM_ID, GameItemType.PRODUCTION_ORDER);
        underTest.save(gameItem);

        underTest.process(gameDataProxy);

        verify(gameDataProxy).saveItems(underTest.getItems().values());
        verify(gameDataProxy).deleteItems(underTest.getDeletedItems());
    }
}