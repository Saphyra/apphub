package com.github.saphyra.apphub.service.admin_panel.proxy;

import com.github.saphyra.apphub.api.platform.event_gateway.client.EventGatewayApiClient;
import com.github.saphyra.apphub.api.platform.event_gateway.model.request.SendEventRequest;
import com.github.saphyra.apphub.lib.web_utils.LocaleProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class EventGatewayProxyTest {
    private static final String LOCALE = "locale";

    @Mock
    private EventGatewayApiClient eventGatewayApiClient;

    @Mock
    private LocaleProvider localeProvider;

    @InjectMocks
    private EventGatewayProxy underTest;

    @Mock
    private SendEventRequest<?> sendEventRequest;

    @Test
    void sendEvent() {
        given(localeProvider.getOrDefault()).willReturn(LOCALE);

        underTest.sendEvent(sendEventRequest);

        then(eventGatewayApiClient).should().sendEvent(sendEventRequest, LOCALE);
    }
}