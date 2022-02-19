package com.github.saphyra.apphub.service.platform.event_gateway.service.send_event;

import com.github.saphyra.apphub.api.platform.event_gateway.model.request.SendEventRequest;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBean;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBeenTestUtils;
import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.service.platform.event_gateway.dao.EventProcessor;
import com.github.saphyra.apphub.service.platform.event_gateway.dao.EventProcessorDao;
import com.github.saphyra.apphub.service.platform.event_gateway.service.local_event.LocalEventProcessor;
import com.github.saphyra.apphub.test.common.TestConstants;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class SendEventTaskTest {
    private static final String EVENT_NAME = "event-name";

    @Mock
    private EventProcessorDao eventProcessorDao;

    @Mock
    private EventSender eventSender;

    @Mock
    private SendEventRequest<String> sendEventRequest;

    @Mock
    private LocalEventProcessor localEventProcessor;

    @Mock
    private ErrorReporterService errorReporterService;

    private final ExecutorServiceBean executorServiceBean = ExecutorServiceBeenTestUtils.create(Mockito.mock(ErrorReporterService.class));

    private SendEventTask underTest;

    @Before
    public void setUp() {
        underTest = SendEventTask.builder()
            .eventProcessorDao(eventProcessorDao)
            .eventSender(eventSender)
            .sendEventRequest(sendEventRequest)
            .locale(TestConstants.DEFAULT_LOCALE)
            .executorServiceBean(executorServiceBean)
            .localEventProcessors(List.of(localEventProcessor))
            .errorReporterService(errorReporterService)
            .build();
    }

    @Mock
    private EventProcessor eventProcessor;

    @Test
    public void run() {
        given(sendEventRequest.getEventName()).willReturn(EVENT_NAME);
        given(eventProcessorDao.getByEventName(EVENT_NAME)).willReturn(Arrays.asList(eventProcessor));

        given(localEventProcessor.shouldProcess(EVENT_NAME)).willReturn(true);

        underTest.run();

        verify(eventSender).sendEvent(eventProcessor, sendEventRequest, TestConstants.DEFAULT_LOCALE);
        verify(localEventProcessor).process(sendEventRequest);
    }

    @Test
    public void localProcessorThrowsError() {
        given(sendEventRequest.getEventName()).willReturn(EVENT_NAME);
        given(eventProcessorDao.getByEventName(EVENT_NAME)).willReturn(Arrays.asList(eventProcessor));
        doThrow(new RuntimeException()).when(localEventProcessor).process(sendEventRequest);

        given(localEventProcessor.shouldProcess(EVENT_NAME)).willReturn(true);

        underTest.run();

        verify(eventSender).sendEvent(eventProcessor, sendEventRequest, TestConstants.DEFAULT_LOCALE);
        verify(errorReporterService).report(anyString(), any());
    }
}