package com.github.saphyra.apphub.service.platform.event_gateway.service.send_event;

import com.github.saphyra.apphub.api.platform.event_gateway.model.request.SendEventRequest;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBean;
import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.service.platform.event_gateway.dao.EventProcessorDao;
import com.github.saphyra.apphub.service.platform.event_gateway.service.local_event.LocalEventProcessor;
import com.github.saphyra.apphub.test.common.TestConstants;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class SendEventTaskFactoryTest {
    @Mock
    private EventProcessorDao eventProcessorDao;

    @Mock
    private EventSender eventSender;

    @Mock
    private ExecutorServiceBean executorServiceBean;

    @Mock
    private LocalEventProcessor localEventProcessor;

    @Mock
    private ErrorReporterService errorReporterService;

    private SendEventTaskFactory underTest;

    @Mock
    private SendEventRequest<?> sendEventRequest;

    @Before
    public void setUp() {
        underTest = SendEventTaskFactory.builder()
            .eventProcessorDao(eventProcessorDao)
            .eventSender(eventSender)
            .executorServiceBean(executorServiceBean)
            .localEventProcessors(List.of(localEventProcessor))
            .errorReporterService(errorReporterService)
            .build();
    }

    @Test
    public void create() {
        SendEventTask result = underTest.create(sendEventRequest, TestConstants.DEFAULT_LOCALE);

        assertThat(result.getSendEventRequest()).isEqualTo(sendEventRequest);
        assertThat(result.getEventProcessorDao()).isEqualTo(eventProcessorDao);
        assertThat(result.getEventSender()).isEqualTo(eventSender);
        assertThat(result.getExecutorServiceBean()).isEqualTo(executorServiceBean);
        assertThat(result.getLocale()).isEqualTo(TestConstants.DEFAULT_LOCALE);
        assertThat(result.getLocalEventProcessors()).containsExactly(localEventProcessor);
        assertThat(result.getErrorReporterService()).isEqualTo(errorReporterService);
    }
}