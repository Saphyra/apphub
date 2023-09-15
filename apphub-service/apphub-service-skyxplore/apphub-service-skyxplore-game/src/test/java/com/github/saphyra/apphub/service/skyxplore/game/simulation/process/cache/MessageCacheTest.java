package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.cache;

import com.github.saphyra.apphub.lib.common_domain.WebSocketEvent;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.ws.SkyXploreGameWebSocketHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class MessageCacheTest {
    private static final UUID RECIPIENT = UUID.randomUUID();
    @Mock
    private SkyXploreGameWebSocketHandler webSocketHandler;

    @InjectMocks
    private MessageCache underTest;

    @Mock
    private WsMessageKey keyToBeSent;

    @Mock
    private WsMessageKey keyNotToBeSent;

    @Mock
    private Supplier<WebSocketMessage> supplier;

    @Mock
    private WebSocketMessage message;

    @Mock
    private Game game;

    @Mock
    private WebSocketEvent event;

    @Test
    void process() {
        underTest.put(keyToBeSent, supplier);
        underTest.put(keyNotToBeSent, supplier);

        given(keyToBeSent.shouldBeSent(game)).willReturn(true);
        given(keyNotToBeSent.shouldBeSent(game)).willReturn(false);
        given(message.getRecipients()).willReturn(List.of(RECIPIENT));
        given(message.getEvent()).willReturn(event);

        given(supplier.get()).willReturn(message);

        underTest.process(game);

        then(webSocketHandler).should().sendEvent(List.of(RECIPIENT), event);
    }
}