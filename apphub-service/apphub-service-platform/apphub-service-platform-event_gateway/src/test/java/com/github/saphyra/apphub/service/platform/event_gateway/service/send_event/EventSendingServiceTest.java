package com.github.saphyra.apphub.service.platform.event_gateway.service.send_event;

import com.github.saphyra.apphub.api.platform.event_gateway.model.request.SendEventRequest;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBean;
import com.github.saphyra.apphub.lib.web_utils.LocaleProvider;
import com.github.saphyra.apphub.test.common.TestConstants;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class EventSendingServiceTest {
    @Mock
    private ExecutorServiceBean executorServiceBean;

    @Mock
    private LocaleProvider localeProvider;

    @Mock
    private SendEventRequestValidator sendEventRequestValidator;

    @Mock
    private SendEventTaskFactory sendEventTaskFactory;

    private EventSendingService underTest;

    @Mock
    private SendEventRequest<?> sendEventRequest;

    @Mock
    private SendEventTask task;

    @Before
    public void setUp() {
        given(localeProvider.getLocaleValidated()).willReturn(TestConstants.DEFAULT_LOCALE);
        given(sendEventTaskFactory.create(sendEventRequest, TestConstants.DEFAULT_LOCALE)).willReturn(task);
    }

    @Test
    public void sendEvent_background() {
        underTest = EventSendingService.builder()
            .executorServiceBean(executorServiceBean)
            .sendEventRequestValidator(sendEventRequestValidator)
            .sendEventTaskFactory(sendEventTaskFactory)
            .backgroundEventSendingEnabled(true)
            .localeProvider(localeProvider)
            .build();

        given(sendEventRequest.isBlockingRequest()).willReturn(false);

        underTest.sendEvent(sendEventRequest);

        verify(sendEventRequestValidator).validate(sendEventRequest);
        verify(executorServiceBean).execute(task);
        verify(task, times(0)).run();
    }

    @Test
    public void sendEvent_backgroundDisabled() {
        underTest = EventSendingService.builder()
            .executorServiceBean(executorServiceBean)
            .sendEventRequestValidator(sendEventRequestValidator)
            .sendEventTaskFactory(sendEventTaskFactory)
            .backgroundEventSendingEnabled(false)
            .localeProvider(localeProvider)
            .build();

        underTest.sendEvent(sendEventRequest);

        verify(sendEventRequestValidator).validate(sendEventRequest);
        verify(executorServiceBean, times(0)).execute(task);
        verify(task).run();
    }

    @Test
    public void sendEvent_blockingRequest() {
        underTest = EventSendingService.builder()
            .executorServiceBean(executorServiceBean)
            .sendEventRequestValidator(sendEventRequestValidator)
            .sendEventTaskFactory(sendEventTaskFactory)
            .backgroundEventSendingEnabled(true)
            .localeProvider(localeProvider)
            .build();

        given(sendEventRequest.isBlockingRequest()).willReturn(true);

        underTest.sendEvent(sendEventRequest);

        verify(sendEventRequestValidator).validate(sendEventRequest);
        verify(executorServiceBean, times(0)).execute(task);
        verify(task).run();
    }
}