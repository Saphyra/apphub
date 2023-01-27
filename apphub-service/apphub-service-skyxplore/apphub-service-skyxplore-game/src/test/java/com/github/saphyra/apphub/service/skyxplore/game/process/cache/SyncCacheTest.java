package com.github.saphyra.apphub.service.skyxplore.game.process.cache;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
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
    private static final UUID RECIPIENT = UUID.randomUUID();

    @Mock
    private MessageCache messageCache;

    @Mock
    private GameItemCache gameItemCache;

    @InjectMocks
    private SyncCache underTest;

    @Mock
    private GameItem gameItem;

    @Mock
    private Runnable runnable;

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
    public void addMessage() {
        underTest.addMessage(RECIPIENT, WebSocketEventName.SKYXPLORE_GAME_PLANET_CITIZEN_MODIFIED, GAME_ITEM_ID, runnable);

        verify(messageCache).add(RECIPIENT, WebSocketEventName.SKYXPLORE_GAME_PLANET_CITIZEN_MODIFIED, GAME_ITEM_ID, runnable);
    }

    @Test
    public void process() {
        underTest.process();

        verify(gameItemCache).process();
        verify(messageCache).process();
    }
}