package com.github.saphyra.apphub.service.platform.event_gateway.service.send_event;

import com.github.saphyra.apphub.api.platform.event_gateway.model.request.SendEventRequest;
import com.github.saphyra.apphub.lib.common_util.ExecutorServiceBean;
import com.github.saphyra.apphub.service.platform.event_gateway.dao.EventProcessor;
import com.github.saphyra.apphub.service.platform.event_gateway.dao.EventProcessorDao;
import com.github.saphyra.util.OffsetDateTimeProvider;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.client.RestTemplate;

import java.time.OffsetDateTime;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class EventSendingServiceTest {
    private static final String EVENT_NAME = "event-name";
    private static final String ASSEMBLED_URL = "assembled-url";
    private static final OffsetDateTime CURRENT_DATE = OffsetDateTime.now();

    @Mock
    private ExecutorServiceBean executorServiceBean;

    @Mock
    private EventProcessorDao eventProcessorDao;

    @Mock
    private OffsetDateTimeProvider offsetDateTimeProvider;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private UrlAssembler urlAssembler;

    @InjectMocks
    private EventSendingService underTest;

    @Mock
    private SendEventRequest<?> sendEventRequest;

    @Mock
    private EventProcessor eventProcessor;

    @Test
    public void sendEvent() {
        doAnswer(invocationOnMock -> {
            ((Runnable) invocationOnMock.getArgument(0)).run();
            return null;
        }).when(executorServiceBean).execute(any());

        given(sendEventRequest.getEventName()).willReturn(EVENT_NAME);
        given(eventProcessorDao.getByEventName(EVENT_NAME)).willReturn(Arrays.asList(eventProcessor));
        given(urlAssembler.assemble(eventProcessor)).willReturn(ASSEMBLED_URL);
        given(offsetDateTimeProvider.getCurrentDate()).willReturn(CURRENT_DATE);

        underTest.sendEvent(sendEventRequest);

        verify(restTemplate).postForEntity(ASSEMBLED_URL, sendEventRequest, Void.class);
        verify(eventProcessor).setLastAccess(CURRENT_DATE);
        verify(eventProcessorDao).save(eventProcessor);
    }
}