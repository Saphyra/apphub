package com.github.saphyra.apphub.service.platform.scheduler.schedulers;

import com.github.saphyra.apphub.api.platform.event_gateway.client.EventGatewayApiClient;
import com.github.saphyra.apphub.api.platform.event_gateway.model.request.SendEventRequest;
import com.github.saphyra.apphub.lib.event.EmptyEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class UserDeletionSchedulerTest {
    @Mock
    private EventGatewayApiClient eventGatewayApiClient;

    @InjectMocks
    private UserDeletionScheduler underTest;

    @Test
    public void accessTokenCleanup() {
        underTest.accessTokenCleanup();

        ArgumentCaptor<SendEventRequest> argumentCaptor = ArgumentCaptor.forClass(SendEventRequest.class);
        verify(eventGatewayApiClient).sendEvent(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue().getEventName()).isEqualTo(EmptyEvent.TRIGGER_ACCOUNT_DELETION);
    }
}