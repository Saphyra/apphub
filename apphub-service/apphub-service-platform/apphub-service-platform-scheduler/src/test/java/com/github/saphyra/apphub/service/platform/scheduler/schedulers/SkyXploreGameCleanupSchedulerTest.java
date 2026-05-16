package com.github.saphyra.apphub.service.platform.scheduler.schedulers;

import com.github.saphyra.apphub.api.platform.event_gateway.client.EventGatewayApiClient;
import com.github.saphyra.apphub.api.platform.event_gateway.model.request.SendEventRequest;
import com.github.saphyra.apphub.lib.event.EmptyEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class SkyXploreGameCleanupSchedulerTest {
    @Mock
    private EventGatewayApiClient eventGatewayApi;

    @InjectMocks
    private SkyXploreGameCleanupScheduler underTest;

    @Test
    public void pingRequest() {
        underTest.gameCleanup();

        verify(eventGatewayApi).sendEvent(SendEventRequest.builder().eventName(EmptyEvent.SKYXPLORE_GAME_CLEANUP_EVENT_NAME).build());
    }
}