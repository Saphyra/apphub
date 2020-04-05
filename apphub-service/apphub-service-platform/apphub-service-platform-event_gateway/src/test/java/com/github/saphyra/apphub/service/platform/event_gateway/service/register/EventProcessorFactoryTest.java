package com.github.saphyra.apphub.service.platform.event_gateway.service.register;

import com.github.saphyra.apphub.api.platform.event_gateway.model.request.RegisterProcessorRequest;
import com.github.saphyra.apphub.service.platform.event_gateway.dao.EventProcessor;
import com.github.saphyra.util.IdGenerator;
import com.github.saphyra.util.OffsetDateTimeProvider;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class EventProcessorFactoryTest {
    private static final String SERVICE_NAME = "service-name";
    private static final String EVENT_NAME = "event-name";
    private static final String URL = "url";
    private static final UUID EVENT_PROCESSOR_ID = UUID.randomUUID();
    private static final OffsetDateTime CURRENT_DATE = OffsetDateTime.now();

    @Mock
    private IdGenerator idGenerator;

    @Mock
    private OffsetDateTimeProvider offsetDateTimeProvider;

    @InjectMocks
    private EventProcessorFactory underTest;

    @Test
    public void create() {
        RegisterProcessorRequest request = RegisterProcessorRequest.builder()
            .serviceName(SERVICE_NAME)
            .eventName(EVENT_NAME)
            .url(URL)
            .build();
        given(idGenerator.randomUUID()).willReturn(EVENT_PROCESSOR_ID);
        given(offsetDateTimeProvider.getCurrentDate()).willReturn(CURRENT_DATE);

        EventProcessor result = underTest.create(request);

        assertThat(result.getEventProcessorId()).isEqualTo(EVENT_PROCESSOR_ID);
        assertThat(result.getServiceName()).isEqualTo(SERVICE_NAME);
        assertThat(result.getEventName()).isEqualTo(EVENT_NAME);
        assertThat(result.getUrl()).isEqualTo(URL);
        assertThat(result.getLastAccess()).isEqualTo(CURRENT_DATE);
    }
}