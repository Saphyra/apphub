package com.github.saphyra.apphub.service.platform.event_gateway.service.send_event;

import com.github.saphyra.apphub.api.platform.event_gateway.model.request.SendEventRequest;
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

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class EventSenderTest {
    private static final String URL = "url";
    private static final OffsetDateTime CURRENT_DATE = OffsetDateTime.now();

    @Mock
    private EventProcessorDao eventProcessorDao;

    @Mock
    private OffsetDateTimeProvider offsetDateTimeProvider;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private UrlAssembler urlAssembler;

    @InjectMocks
    private EventSender underTest;

    @Mock
    private EventProcessor eventProcessor;

    @Mock
    private SendEventRequest<String> sendEventRequest;

    @Test
    public void sendEvent() {
        given(urlAssembler.assemble(eventProcessor)).willReturn(URL);
        given(offsetDateTimeProvider.getCurrentDate()).willReturn(CURRENT_DATE);

        underTest.sendEvent(eventProcessor, sendEventRequest);

        verify(restTemplate).postForEntity(URL, sendEventRequest, Void.class);
        verify(eventProcessor).setLastAccess(CURRENT_DATE);
        verify(eventProcessorDao).save(eventProcessor);
    }
}