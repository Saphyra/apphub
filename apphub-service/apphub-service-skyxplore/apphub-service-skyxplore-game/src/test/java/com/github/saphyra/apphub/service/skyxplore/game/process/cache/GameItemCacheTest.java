package com.github.saphyra.apphub.service.skyxplore.game.process.cache;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.service.skyxplore.game.proxy.GameDataProxy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class GameItemCacheTest {
    private static final UUID GAME_ITEM_ID = UUID.randomUUID();

    @Mock
    private GameDataProxy gameDataProxy;

    @InjectMocks
    private GameItemCache underTest;

    @Mock
    private GameItem gameItem;

    @BeforeEach
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

        underTest.process();

        verify(gameDataProxy).saveItems(underTest.getItems().values());
        verify(gameDataProxy).deleteItems(underTest.getDeletedItems());
    }
}