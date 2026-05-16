package com.github.saphyra.apphub.service.platform.event_gateway.service.send_event;

import com.github.saphyra.apphub.api.platform.event_gateway.model.request.SendEventRequest;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBean;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class EventSendingServiceTest {
    @Mock
    private ExecutorServiceBean executorServiceBean;

    @Mock
    private SendEventRequestValidator sendEventRequestValidator;

    @Mock
    private SendEventTaskFactory sendEventTaskFactory;

    private EventSendingService underTest;

    @Mock
    private SendEventRequest<?> sendEventRequest;

    @Mock
    private SendEventTask task;

    @BeforeEach
    public void setUp() {
        given(sendEventTaskFactory.create(sendEventRequest)).willReturn(task);
    }

    @Test
    public void sendEvent_background() {
        underTest = EventSendingService.builder()
            .executorServiceBean(executorServiceBean)
            .sendEventRequestValidator(sendEventRequestValidator)
            .sendEventTaskFactory(sendEventTaskFactory)
            .backgroundEventSendingEnabled(true)
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
            .build();

        given(sendEventRequest.isBlockingRequest()).willReturn(true);

        underTest.sendEvent(sendEventRequest);

        verify(sendEventRequestValidator).validate(sendEventRequest);
        verify(executorServiceBean, times(0)).execute(task);
        verify(task).run();
    }
}