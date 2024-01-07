package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.cache;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class SyncCacheTest {
    private static final UUID GAME_ITEM_ID = UUID.randomUUID();

    @Mock
    private GameItemCache gameItemCache;

    @InjectMocks
    private SyncCache underTest;

    @Mock
    private GameItem gameItem;

    @Test
    public void saveGameItem() {
        underTest.saveGameItem(gameItem);

        verify(gameItemCache).save(gameItem);
    }

    @Test
    public void deleteGameItem() {
        underTest.deleteGameItem(GAME_ITEM_ID, GameItemType.CONSTRUCTION);

        verify(gameItemCache).delete(GAME_ITEM_ID, GameItemType.CONSTRUCTION);
    }

    @Test
    public void process() {
        underTest.process();

        verify(gameItemCache).process();
    }
}