package com.github.saphyra.apphub.service.platform.main_gateway.service.authentication;

import com.github.saphyra.apphub.api.platform.event_gateway.client.EventGatewayApiClient;
import com.github.saphyra.apphub.api.platform.event_gateway.model.request.SendEventRequest;
import com.github.saphyra.apphub.lib.event.RefreshAccessTokenExpirationEvent;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

import java.util.Arrays;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@RunWith(MockitoJUnitRunner.class)
public class AccessTokenExpirationUpdateServiceTest {
    private static final String REQUEST_URI = "request-uri";
    private static final String NON_SESSION_EXTENDING_URI = "non-session-extending-uri";
    private static final UUID ACCESS_TOKEN_ID = UUID.randomUUID();

    @Mock
    private AntPathMatcher antPathMatcher;

    @Mock
    private EventGatewayApiClient eventGatewayApi;

    @Mock
    private NonSessionExtendingUriProperties nonSessionExtendingUriProperties;

    @InjectMocks
    private AccessTokenExpirationUpdateService underTest;

    @Mock
    private HttpServletRequest request;

    @Captor
    ArgumentCaptor<SendEventRequest<RefreshAccessTokenExpirationEvent>> argumentCaptor;

    @Test
    public void updateExpiration_nonSessionExtendingUri() {
        given(request.getRequestURI()).willReturn(REQUEST_URI);
        given(request.getMethod()).willReturn(RequestMethod.GET.name());

        NonSessionExtendingUriProperties.NonSessionExtendingUri nonSessionExtendingUri = new NonSessionExtendingUriProperties.NonSessionExtendingUri(NON_SESSION_EXTENDING_URI, RequestMethod.GET.name());
        given(nonSessionExtendingUriProperties.getNonSessionExtendingUris()).willReturn(Arrays.asList(nonSessionExtendingUri));

        given(antPathMatcher.match(NON_SESSION_EXTENDING_URI, REQUEST_URI)).willReturn(true);

        underTest.updateExpiration(request, ACCESS_TOKEN_ID);

        verifyNoInteractions(eventGatewayApi);
    }

    @Test
    public void updateExpiration() {
        given(request.getRequestURI()).willReturn(REQUEST_URI);
        given(request.getMethod()).willReturn(RequestMethod.GET.name());

        NonSessionExtendingUriProperties.NonSessionExtendingUri nonSessionExtendingUri = new NonSessionExtendingUriProperties.NonSessionExtendingUri(NON_SESSION_EXTENDING_URI, RequestMethod.GET.name());
        given(nonSessionExtendingUriProperties.getNonSessionExtendingUris()).willReturn(Arrays.asList(nonSessionExtendingUri));

        given(antPathMatcher.match(NON_SESSION_EXTENDING_URI, REQUEST_URI)).willReturn(false);

        underTest.updateExpiration(request, ACCESS_TOKEN_ID);

        verify(eventGatewayApi).sendEvent(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue().getEventName()).isEqualTo(RefreshAccessTokenExpirationEvent.EVENT_NAME);
        assertThat(argumentCaptor.getValue().getPayload().getAccessTokenId()).isEqualTo(ACCESS_TOKEN_ID);
    }
}