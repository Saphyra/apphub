package com.github.saphyra.apphub.service.platform.main_gateway.service.authentication;

import com.github.saphyra.apphub.api.platform.event_gateway.client.EventGatewayApiClient;
import com.github.saphyra.apphub.api.platform.event_gateway.model.request.SendEventRequest;
import com.github.saphyra.apphub.lib.common_domain.WhiteListedEndpoint;
import com.github.saphyra.apphub.lib.common_util.CommonConfigProperties;
import com.github.saphyra.apphub.lib.event.RefreshAccessTokenExpirationEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.util.AntPathMatcher;

import java.util.Arrays;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@RunWith(MockitoJUnitRunner.class)
public class AccessTokenExpirationUpdateServiceTest {
    private static final String METHOD = "method";
    private static final String PATTERN = "pattern";
    private static final UUID ACCESS_TOKEN_ID = UUID.randomUUID();
    private static final String PATH = "path";
    private static final String LOCALE = "locale";

    @Mock
    private AntPathMatcher antPathMatcher;

    @Mock
    private CommonConfigProperties commonConfigProperties;

    @Mock
    private EventGatewayApiClient eventGatewayApi;

    @Mock
    private NonSessionExtendingUriProperties nonSessionExtendingUriProperties;

    @InjectMocks
    private AccessTokenExpirationUpdateService underTest;

    @Captor
    private ArgumentCaptor<SendEventRequest<RefreshAccessTokenExpirationEvent>> argumentCaptor;

    @Before
    public void setUp() {
        given(nonSessionExtendingUriProperties.getNonSessionExtendingUris()).willReturn(Arrays.asList(new WhiteListedEndpoint(PATTERN, METHOD)));
    }

    @Test
    public void nonSessionExtendingUri() {
        given(antPathMatcher.match(PATTERN, PATH)).willReturn(true);

        underTest.updateExpiration(METHOD, PATH, ACCESS_TOKEN_ID);

        verifyNoInteractions(eventGatewayApi);
    }

    @Test
    public void sessionExtendingUri() {
        given(antPathMatcher.match(PATTERN, PATH)).willReturn(false);
        given(commonConfigProperties.getDefaultLocale()).willReturn(LOCALE);

        underTest.updateExpiration(METHOD, PATH, ACCESS_TOKEN_ID);

        verify(eventGatewayApi).sendEvent(argumentCaptor.capture(), eq(LOCALE));

        RefreshAccessTokenExpirationEvent payload = argumentCaptor.getValue().getPayload();
        assertThat(payload.getAccessTokenId()).isEqualTo(ACCESS_TOKEN_ID);
    }
}