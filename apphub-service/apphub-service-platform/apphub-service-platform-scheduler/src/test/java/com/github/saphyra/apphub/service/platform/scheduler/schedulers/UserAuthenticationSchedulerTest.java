package com.github.saphyra.apphub.service.platform.scheduler.schedulers;

import com.github.saphyra.apphub.api.platform.event_gateway.client.EventGatewayApiClient;
import com.github.saphyra.apphub.api.platform.event_gateway.model.request.SendEventRequest;
import com.github.saphyra.apphub.lib.common_util.CommonConfigProperties;
import com.github.saphyra.apphub.lib.event.EmptyEvent;
import com.github.saphyra.apphub.test.common.TestConstants;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class UserAuthenticationSchedulerTest {
    @Mock
    private CommonConfigProperties commonConfigProperties;

    @Mock
    private EventGatewayApiClient eventGatewayApiClient;

    @InjectMocks
    private UserAuthenticationScheduler underTest;

    @Test
    public void accessTokenCleanup() {
        given(commonConfigProperties.getDefaultLocale()).willReturn(TestConstants.DEFAULT_LOCALE);

        underTest.accessTokenCleanup();

        ArgumentCaptor<SendEventRequest> argumentCaptor = ArgumentCaptor.forClass(SendEventRequest.class);
        verify(eventGatewayApiClient).sendEvent(argumentCaptor.capture(), eq(TestConstants.DEFAULT_LOCALE));
        assertThat(argumentCaptor.getValue().getEventName()).isEqualTo(EmptyEvent.DELETE_EXPIRED_ACCESS_TOKENS_EVENT_NAME);
    }

}