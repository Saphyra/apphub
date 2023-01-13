package com.github.saphyra.apphub.service.platform.scheduler.schedulers;

import com.github.saphyra.apphub.api.platform.event_gateway.client.EventGatewayApiClient;
import com.github.saphyra.apphub.api.platform.event_gateway.model.request.SendEventRequest;
import com.github.saphyra.apphub.lib.common_util.CommonConfigProperties;
import com.github.saphyra.apphub.lib.event.EmptyEvent;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class StoredFileCleanupSchedulerTest {
    private static final String LOCALE = "locale";

    @Mock
    private CommonConfigProperties commonConfigProperties;

    @Mock
    private EventGatewayApiClient eventGatewayApi;

    @InjectMocks
    private StoredFileCleanupScheduler underTest;

    @Captor
    private ArgumentCaptor<SendEventRequest<?>> argumentCaptor;

    @Test
    public void storedFileCleanup() {
        given(commonConfigProperties.getDefaultLocale()).willReturn(LOCALE);

        underTest.storedFileCleanup();

        verify(eventGatewayApi).sendEvent(argumentCaptor.capture(), eq(LOCALE));

        SendEventRequest<?> request = argumentCaptor.getValue();
        assertThat(request.getEventName()).isEqualTo(EmptyEvent.STORAGE_CLEAN_UP_STORED_FILES);
    }
}