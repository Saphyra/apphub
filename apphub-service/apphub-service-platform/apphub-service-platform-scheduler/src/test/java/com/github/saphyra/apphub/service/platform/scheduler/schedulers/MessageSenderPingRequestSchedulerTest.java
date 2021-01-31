package com.github.saphyra.apphub.service.platform.scheduler.schedulers;

import com.github.saphyra.apphub.api.platform.event_gateway.client.EventGatewayApiClient;
import com.github.saphyra.apphub.api.platform.event_gateway.model.request.SendEventRequest;
import com.github.saphyra.apphub.lib.common_util.CommonConfigProperties;
import com.github.saphyra.apphub.lib.event.EmptyEvent;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class MessageSenderPingRequestSchedulerTest {
    private static final String LOCALE = "locale";

    @Mock
    private CommonConfigProperties commonConfigProperties;

    @Mock
    private EventGatewayApiClient eventGatewayApi;

    @InjectMocks
    private MessageSenderPingRequestScheduler underTest;

    @Test
    public void pingRequest() {
        given(commonConfigProperties.getDefaultLocale()).willReturn(LOCALE);

        underTest.pingRequest();

        verify(eventGatewayApi).sendEvent(SendEventRequest.builder().eventName(EmptyEvent.MESSAGE_SENDER_PING_REQUEST_EVENT_NAME).build(), LOCALE);
    }
}