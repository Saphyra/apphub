package com.github.saphyra.apphub.service.platform.event_gateway.dao;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class EventProcessorConverterTest {
    private static final String EVENT_PROCESSOR_ID_STRING = "event-processor-id";
    private static final String SERVICE_NAME = "service-name";
    private static final String URL = "url";
    private static final String EVENT_NAME = "event-name";
    private static final LocalDateTime LAST_ACCESS = LocalDateTime.now();
    private static final UUID EVENT_PROCESSOR_ID = UUID.randomUUID();

    @Mock
    private UuidConverter uuidConverter;

    @InjectMocks
    private EventProcessorConverter underTest;

    @Test
    public void convertEntity() {
        EventProcessorEntity entity = EventProcessorEntity.builder()
            .eventProcessorId(EVENT_PROCESSOR_ID_STRING)
            .host(SERVICE_NAME)
            .url(URL)
            .eventName(EVENT_NAME)
            .lastAccess(LAST_ACCESS)
            .build();
        given(uuidConverter.convertEntity(EVENT_PROCESSOR_ID_STRING)).willReturn(EVENT_PROCESSOR_ID);

        EventProcessor result = underTest.convertEntity(entity);

        assertThat(result.getEventProcessorId()).isEqualTo(EVENT_PROCESSOR_ID);
        assertThat(result.getHost()).isEqualTo(SERVICE_NAME);
        assertThat(result.getUrl()).isEqualTo(URL);
        assertThat(result.getEventName()).isEqualTo(EVENT_NAME);
        assertThat(result.getLastAccess()).isEqualTo(LAST_ACCESS);
    }

    @Test
    public void convertDomain() {
        EventProcessor domain = EventProcessor.builder()
            .eventProcessorId(EVENT_PROCESSOR_ID)
            .host(SERVICE_NAME)
            .url(URL)
            .eventName(EVENT_NAME)
            .lastAccess(LAST_ACCESS)
            .build();
        given(uuidConverter.convertDomain(EVENT_PROCESSOR_ID)).willReturn(EVENT_PROCESSOR_ID_STRING);

        EventProcessorEntity result = underTest.convertDomain(domain);

        assertThat(result.getEventProcessorId()).isEqualTo(EVENT_PROCESSOR_ID_STRING);
        assertThat(result.getHost()).isEqualTo(SERVICE_NAME);
        assertThat(result.getUrl()).isEqualTo(URL);
        assertThat(result.getEventName()).isEqualTo(EVENT_NAME);
        assertThat(result.getLastAccess()).isEqualTo(LAST_ACCESS);
    }
}