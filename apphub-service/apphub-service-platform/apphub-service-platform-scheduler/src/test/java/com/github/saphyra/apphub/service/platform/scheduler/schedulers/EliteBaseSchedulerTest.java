package com.github.saphyra.apphub.service.platform.scheduler.schedulers;

import com.github.saphyra.apphub.api.platform.event_gateway.client.EventGatewayApiClient;
import com.github.saphyra.apphub.api.platform.event_gateway.model.request.SendEventRequest;
import com.github.saphyra.apphub.lib.common_util.CommonConfigProperties;
import com.github.saphyra.apphub.lib.event.EmptyEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class EliteBaseSchedulerTest {
    private static final String LOCALE = "locale";

    @Mock
    private CommonConfigProperties commonConfigProperties;

    @Mock
    private EventGatewayApiClient eventGatewayApi;

    @InjectMocks
    private EliteBaseScheduler underTest;

    @BeforeEach
    void setUp() {
        given(commonConfigProperties.getDefaultLocale()).willReturn(LOCALE);
    }

    @Test
    void processMessages() {
        underTest.processMessages();

        then(eventGatewayApi).should().sendEvent(SendEventRequest.builder().eventName(EmptyEvent.ELITE_BASE_PROCESS_MESSAGES).build(), LOCALE);
    }

    @Test
    void resetUnhandledMessages() {
        underTest.resetUnhandledMessages();

        then(eventGatewayApi).should().sendEvent(SendEventRequest.builder().eventName(EmptyEvent.ELITE_BASE_RESET_UNHANDLED_MESSAGES).build(), LOCALE);
    }

    @Test
    void deleteExpiredMessages() {
        underTest.deleteExpiredMessages();

        then(eventGatewayApi).should().sendEvent(SendEventRequest.builder().eventName(EmptyEvent.ELITE_BASE_DELETE_EXPIRED_MESSAGES).build(), LOCALE);
    }
}