package com.github.saphyra.apphub.service.platform.message_sender.controller;

import com.github.saphyra.apphub.service.platform.message_sender.connection.WebSocketHandler;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class MessageSenderEventControllerImplTest {
    @Mock
    private WebSocketHandler webSocketHandler;

    private MessageSenderEventControllerImpl underTest;

    @Before
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