package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.cache;

import com.github.saphyra.apphub.service.skyxplore.game.ws.main.SkyXploreGameMainWebSocketHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class MessageCacheFactoryTest {
    @Mock
    private SkyXploreGameMainWebSocketHandler webSocketHandler;

    @InjectMocks
    private MessageCacheFactory underTest;

    @Test
    public void create() {
        assertThat(underTest.create()).isNotNull();
    }
}