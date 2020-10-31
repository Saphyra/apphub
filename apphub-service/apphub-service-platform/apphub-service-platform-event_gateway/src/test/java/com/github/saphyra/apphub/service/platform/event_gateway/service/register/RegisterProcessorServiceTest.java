package com.github.saphyra.apphub.service.platform.event_gateway.service.register;

import com.github.saphyra.apphub.api.platform.event_gateway.model.request.RegisterProcessorRequest;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.service.platform.event_gateway.dao.EventProcessor;
import com.github.saphyra.apphub.service.platform.event_gateway.dao.EventProcessorDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class RegisterProcessorServiceTest {
    private static final String EVENT_NAME = "event-name";
    private static final String SERVICE_NAME = "service-name";
    private static final String URL_1 = "url-1";
    private static final String URL_2 = "url-2";
    private static final LocalDateTime CURRENT_DATE = LocalDateTime.now();

    @Mock
    private EventProcessorDao eventProcessorDao;

    @Mock
    private EventProcessorFactory eventProcessorFactory;

    @Mock
    private DateTimeUtil dateTimeUtil;

    @Mock
    private RegisterProcessorRequestValidator registerProcessorRequestValidator;

    @InjectMocks
    private RegisterProcessorService underTest;

    @Mock
    private EventProcessor eventProcessor;

    @Mock
    private EventProcessor newEventProcessor;

    @Test
    public void registerProcessor_alreadyExists() {
        given(eventProcessorDao.findByServiceNameAndEventName(SERVICE_NAME, EVENT_NAME)).willReturn(Optional.of(eventProcessor));
        given(dateTimeUtil.getCurrentDate()).willReturn(CURRENT_DATE);

        RegisterProcessorRequest request = RegisterProcessorRequest.builder()
            .serviceName(SERVICE_NAME)
            .eventName(EVENT_NAME)
            .url(URL_1)
            .build();

        underTest.registerProcessor(request);

        verify(registerProcessorRequestValidator).validate(request);
        verify(eventProcessor).setLastAccess(CURRENT_DATE);
        verify(eventProcessorDao).save(eventProcessor);
    }

    @Test
    public void registerProcessor_differentUrl() {
        given(eventProcessorDao.findByServiceNameAndEventName(SERVICE_NAME, EVENT_NAME)).willReturn(Optional.of(eventProcessor));
        given(dateTimeUtil.getCurrentDate()).willReturn(CURRENT_DATE);

        RegisterProcessorRequest request = RegisterProcessorRequest.builder()
            .serviceName(SERVICE_NAME)
            .eventName(EVENT_NAME)
            .url(URL_2)
            .build();

        underTest.registerProcessor(request);

        verify(registerProcessorRequestValidator).validate(request);
        verify(eventProcessor).setLastAccess(CURRENT_DATE);
        verify(eventProcessor).setUrl(URL_2);
        verify(eventProcessorDao).save(eventProcessor);
    }

    @Test
    public void registerProcessor_notPresent() {
        given(eventProcessorDao.findByServiceNameAndEventName(SERVICE_NAME, EVENT_NAME)).willReturn(Optional.empty());
        given(dateTimeUtil.getCurrentDate()).willReturn(CURRENT_DATE);

        RegisterProcessorRequest request = RegisterProcessorRequest.builder()
            .serviceName(SERVICE_NAME)
            .eventName(EVENT_NAME)
            .url(URL_1)
            .build();

        given(eventProcessorFactory.create(request)).willReturn(newEventProcessor);

        underTest.registerProcessor(request);

        verify(registerProcessorRequestValidator).validate(request);
        verify(newEventProcessor).setLastAccess(CURRENT_DATE);
        verify(eventProcessorDao).save(newEventProcessor);
    }
}