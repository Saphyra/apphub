package com.github.saphyra.apphub.service.platform.event_gateway.service.register;

import com.github.saphyra.apphub.api.platform.event_gateway.model.request.RegisterProcessorRequest;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.platform.event_gateway.dao.EventProcessor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class EventProcessorFactoryTest {
    private static final String SERVICE_NAME = "service-name";
    private static final String EVENT_NAME = "event-name";
    private static final String URL = "url";
    private static final UUID EVENT_PROCESSOR_ID = UUID.randomUUID();
    private static final LocalDateTime CURRENT_DATE = LocalDateTime.now();

    @Mock
    private IdGenerator idGenerator;

    @Mock
    private DateTimeUtil dateTimeUtil;

    @InjectMocks
    private EventProcessorFactory underTest;

    @Test
    public void create() {
        RegisterProcessorRequest request = RegisterProcessorRequest.builder()
            .serviceName(SERVICE_NAME)
            .eventName(EVENT_NAME)
            .url(URL)
            .build();
        given(idGenerator.randomUuid()).willReturn(EVENT_PROCESSOR_ID);
        given(dateTimeUtil.getCurrentDateTime()).willReturn(CURRENT_DATE);

        EventProcessor result = underTest.create(request);

        assertThat(result.getEventProcessorId()).isEqualTo(EVENT_PROCESSOR_ID);
        assertThat(result.getServiceName()).isEqualTo(SERVICE_NAME);
        assertThat(result.getEventName()).isEqualTo(EVENT_NAME);
        assertThat(result.getUrl()).isEqualTo(URL);
        assertThat(result.getLastAccess()).isEqualTo(CURRENT_DATE);
    }
}