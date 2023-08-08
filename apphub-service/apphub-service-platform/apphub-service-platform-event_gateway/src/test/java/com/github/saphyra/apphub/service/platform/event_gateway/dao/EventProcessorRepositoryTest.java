package com.github.saphyra.apphub.service.platform.event_gateway.dao;

import com.github.saphyra.apphub.test.common.repository.RepositoryTestConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = RepositoryTestConfiguration.class)
public class EventProcessorRepositoryTest {
    private static final String EVENT_PROCESSOR_ID_1 = "event-processor-id-1";
    private static final String EVENT_PROCESSOR_ID_2 = "event-processor-id-2";
    private static final String SERVICE_NAME_1 = "service-name-1";
    private static final String SERVICE_NAME_2 = "service-name-2";
    private static final String EVENT_NAME_1 = "event-name-1";
    private static final String EVENT_NAME_2 = "event-name-2";
    private static final LocalDateTime CURRENT_DATE = LocalDateTime.now().withNano(0);
    private static final LocalDateTime BEFORE_DATE = CURRENT_DATE.minusSeconds(1);
    private static final LocalDateTime AFTER_DATE = CURRENT_DATE.plusSeconds(1);

    @Autowired
    private EventProcessorRepository underTest;

    @AfterEach
    public void clear() {
        underTest.deleteAll();
    }

    @Test
    public void findByServiceNameAndEventName() {
        EventProcessorEntity entity1 = EventProcessorEntity.builder()
            .eventProcessorId(EVENT_PROCESSOR_ID_1)
            .host(SERVICE_NAME_1)
            .eventName(EVENT_NAME_1)
            .build();
        EventProcessorEntity entity2 = EventProcessorEntity.builder()
            .eventProcessorId(EVENT_PROCESSOR_ID_2)
            .host(SERVICE_NAME_2)
            .eventName(EVENT_NAME_2)
            .build();
        underTest.saveAll(Arrays.asList(entity1, entity2));

        Optional<EventProcessorEntity> result = underTest.findByHostAndEventName(SERVICE_NAME_1, EVENT_NAME_1);

        assertThat(result).contains(entity1);
    }

    @Test
    public void getByEventName() {
        EventProcessorEntity entity1 = EventProcessorEntity.builder()
            .eventProcessorId(EVENT_PROCESSOR_ID_1)
            .eventName(EVENT_NAME_1)
            .build();
        EventProcessorEntity entity2 = EventProcessorEntity.builder()
            .eventProcessorId(EVENT_PROCESSOR_ID_2)
            .eventName(EVENT_NAME_2)
            .build();
        underTest.saveAll(Arrays.asList(entity1, entity2));

        List<EventProcessorEntity> result = underTest.getByEventName(EVENT_NAME_1);

        assertThat(result).containsExactly(entity1);
    }

    @Test
    public void getByServiceName() {
        EventProcessorEntity entity1 = EventProcessorEntity.builder()
            .eventProcessorId(EVENT_PROCESSOR_ID_1)
            .host(SERVICE_NAME_1)
            .build();
        EventProcessorEntity entity2 = EventProcessorEntity.builder()
            .eventProcessorId(EVENT_PROCESSOR_ID_2)
            .host(SERVICE_NAME_2)
            .build();
        underTest.saveAll(Arrays.asList(entity1, entity2));

        List<EventProcessorEntity> result = underTest.getByHost(SERVICE_NAME_1);

        assertThat(result).containsExactly(entity1);
    }

    @Test
    public void getByLastAccessBefore() {
        EventProcessorEntity entity1 = EventProcessorEntity.builder()
            .eventProcessorId(EVENT_PROCESSOR_ID_1)
            .lastAccess(BEFORE_DATE)
            .build();
        EventProcessorEntity entity2 = EventProcessorEntity.builder()
            .eventProcessorId(EVENT_PROCESSOR_ID_2)
            .lastAccess(AFTER_DATE)
            .build();
        underTest.saveAll(Arrays.asList(entity1, entity2));

        List<EventProcessorEntity> result = underTest.getByLastAccessBefore(CURRENT_DATE);

        assertThat(result).containsExactly(entity1);
    }
}