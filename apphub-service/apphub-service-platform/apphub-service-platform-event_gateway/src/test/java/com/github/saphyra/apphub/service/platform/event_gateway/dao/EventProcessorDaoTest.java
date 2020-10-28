package com.github.saphyra.apphub.service.platform.event_gateway.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class EventProcessorDaoTest {
    private static final String SERVICE_NAME = "service-name";
    private static final String EVENT_NAME = "event-name";
    private static final LocalDateTime LAST_ACCESS = LocalDateTime.now();

    @Mock
    private EventProcessorConverter converter;

    @Mock
    private EventProcessorRepository repository;

    @InjectMocks
    private EventProcessorDao underTest;

    @Mock
    private EventProcessorEntity entity;

    @Mock
    private EventProcessor domain;

    @Test
    public void findByServiceNameAndEventName() {
        given(repository.findByServiceNameAndEventName(SERVICE_NAME, EVENT_NAME)).willReturn(Optional.of(entity));
        given(converter.convertEntity(Optional.of(entity))).willReturn(Optional.of(domain));

        Optional<EventProcessor> result = underTest.findByServiceNameAndEventName(SERVICE_NAME, EVENT_NAME);

        assertThat(result).contains(domain);
    }

    @Test
    public void getByEventName() {
        given(repository.getByEventName(EVENT_NAME)).willReturn(Arrays.asList(entity));
        given(converter.convertEntity(Arrays.asList(entity))).willReturn(Arrays.asList(domain));

        List<EventProcessor> result = underTest.getByEventName(EVENT_NAME);

        assertThat(result).containsExactly(domain);
    }

    @Test
    public void getByServiceName() {
        given(repository.getByServiceName(SERVICE_NAME)).willReturn(Arrays.asList(entity));
        given(converter.convertEntity(Arrays.asList(entity))).willReturn(Arrays.asList(domain));

        List<EventProcessor> result = underTest.getByServiceName(SERVICE_NAME);

        assertThat(result).containsExactly(domain);
    }

    @Test
    public void getByLastAccessBefore() {
        given(repository.getByLastAccessBefore(LAST_ACCESS)).willReturn(Arrays.asList(entity));
        given(converter.convertEntity(Arrays.asList(entity))).willReturn(Arrays.asList(domain));

        List<EventProcessor> result = underTest.getByLastAccessBefore(LAST_ACCESS);

        assertThat(result).containsExactly(domain);
    }
}