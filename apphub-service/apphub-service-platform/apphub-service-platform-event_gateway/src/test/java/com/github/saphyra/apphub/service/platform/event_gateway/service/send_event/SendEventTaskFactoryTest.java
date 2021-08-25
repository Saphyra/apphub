package com.github.saphyra.apphub.service.platform.event_gateway.service.send_event;

import com.github.saphyra.apphub.api.platform.event_gateway.model.request.SendEventRequest;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBean;
import com.github.saphyra.apphub.service.platform.event_gateway.dao.EventProcessorDao;
import com.github.saphyra.apphub.test.common.TestConstants;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class SendEventTaskFactoryTest {
    @Mock
    private EventProcessorDao eventProcessorDao;

    @Mock
    private EventSender eventSender;

    @Mock
    private ExecutorServiceBean executorServiceBean;

    @InjectMocks
    private SendEventTaskFactory underTest;

    @Mock
    private SendEventRequest<?> sendEventRequest;

    @Test
    public void create() {
        SendEventTask result = underTest.create(sendEventRequest, TestConstants.DEFAULT_LOCALE);

        assertThat(result.getSendEventRequest()).isEqualTo(sendEventRequest);
        assertThat(result.getEventProcessorDao()).isEqualTo(eventProcessorDao);
        assertThat(result.getEventSender()).isEqualTo(eventSender);
        assertThat(result.getExecutorServiceBean()).isEqualTo(executorServiceBean);
        assertThat(result.getLocale()).isEqualTo(TestConstants.DEFAULT_LOCALE);
    }
}