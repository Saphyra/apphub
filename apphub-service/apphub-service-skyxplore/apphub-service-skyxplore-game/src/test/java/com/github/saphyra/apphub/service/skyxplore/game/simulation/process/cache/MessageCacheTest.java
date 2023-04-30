package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.cache;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketMessage;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.proxy.MessageSenderProxy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.function.Supplier;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MessageCacheTest {
    @Mock
    private MessageSenderProxy messageSenderProxy;

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

    @Test
    void process() {
        underTest.put(keyToBeSent, supplier);
        underTest.put(keyNotToBeSent, supplier);

        given(keyToBeSent.shouldBeSent(game)).willReturn(true);
        given(keyNotToBeSent.shouldBeSent(game)).willReturn(false);

        given(supplier.get()).willReturn(message);

        underTest.process(game);

        verify(messageSenderProxy).bulkSendToGame(List.of(message));
    }
}