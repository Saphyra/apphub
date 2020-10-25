package com.github.saphyra.apphub.service.platform.event_gateway.service.send_event;

import com.github.saphyra.apphub.api.platform.event_gateway.model.request.SendEventRequest;
import com.github.saphyra.apphub.lib.common_util.Constants;
import com.github.saphyra.apphub.lib.common_util.OffsetDateTimeProvider;
import com.github.saphyra.apphub.service.platform.event_gateway.dao.EventProcessor;
import com.github.saphyra.apphub.service.platform.event_gateway.dao.EventProcessorDao;
import com.github.saphyra.apphub.test.common.TestConstants;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpEntity;
import org.springframework.web.client.RestTemplate;

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
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

    @Captor
    private ArgumentCaptor<HttpEntity<SendEventRequest<?>>> argumentCaptor;

    @Test
    public void sendEvent() {
        given(urlAssembler.assemble(eventProcessor)).willReturn(URL);
        given(offsetDateTimeProvider.getCurrentDate()).willReturn(CURRENT_DATE);

        underTest.sendEvent(eventProcessor, sendEventRequest, TestConstants.DEFAULT_LOCALE);

        verify(restTemplate).postForEntity(eq(URL), argumentCaptor.capture(), eq(Void.class));
        assertThat(argumentCaptor.getValue().getHeaders().get(Constants.LOCALE_HEADER)).containsExactly(TestConstants.DEFAULT_LOCALE);
        assertThat(argumentCaptor.getValue().getBody()).isEqualTo(sendEventRequest);
        verify(eventProcessor).setLastAccess(CURRENT_DATE);
        verify(eventProcessorDao).save(eventProcessor);
    }
}