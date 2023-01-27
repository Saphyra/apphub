package com.github.saphyra.apphub.service.platform.message_sender.controller;

import com.github.saphyra.apphub.service.platform.message_sender.connection.WebSocketHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class MessageSenderEventControllerImplTest {
    @Mock
    private WebSocketHandler webSocketHandler;

    private MessageSenderEventControllerImpl underTest;

    @BeforeEach
    public void setUp() {
        underTest = new MessageSenderEventControllerImpl(Arrays.asList(webSocketHandler));
    }

    @Test
    public void sendPingRequests() {
        underTest.sendPingRequests();

        verify(webSocketHandler).sendPingRequest();
    }

    @Test
    public void connectionCleanup() {
        underTest.connectionCleanup();

        verify(webSocketHandler).cleanUp();
    }
}