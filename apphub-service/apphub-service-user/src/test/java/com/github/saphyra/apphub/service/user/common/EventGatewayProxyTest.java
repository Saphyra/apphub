package com.github.saphyra.apphub.service.user.common;

import com.github.saphyra.apphub.api.platform.event_gateway.client.EventGatewayApiClient;
import com.github.saphyra.apphub.api.platform.event_gateway.model.request.SendEventRequest;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.web_utils.LocaleProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class EventGatewayProxyTest {
    private static final String EVENT_NAME = "event-name";
    private static final Object PAYLOAD = "payload";
    private static final String LOCALE = "locale";

    @Mock
    private EventGatewayApiClient eventGatewayClient;

    @Mock
    private LocaleProvider localeProvider;

    @InjectMocks
    private EventGatewayProxy underTest;

    @Captor
    private ArgumentCaptor<SendEventRequest<Object>> argumentCaptor;

    @Test
    void sendEvent() {
        given(localeProvider.getOrDefault()).willReturn(LOCALE);

        underTest.sendEvent(EVENT_NAME, PAYLOAD, true);

        then(eventGatewayClient).should().sendEvent(argumentCaptor.capture(), eq(LOCALE));
        assertThat(argumentCaptor)
            .extracting(ArgumentCaptor::getValue)
            .returns(EVENT_NAME, SendEventRequest::getEventName)
            .returns(PAYLOAD, SendEventRequest::getPayload)
            .returns(String.valueOf(true), sendEventRequest -> sendEventRequest.getMetadata().get(Constants.SEND_EVENT_REQUEST_METADATA_KEY_BLOCKING_REQUEST));
    }
}