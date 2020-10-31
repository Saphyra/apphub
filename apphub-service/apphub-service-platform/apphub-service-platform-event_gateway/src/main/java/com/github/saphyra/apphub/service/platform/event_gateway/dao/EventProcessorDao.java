package com.github.saphyra.apphub.service.platform.event_gateway.dao;

import com.github.saphyra.apphub.lib.common_util.AbstractDao;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
public class EventProcessorDao extends AbstractDao<EventProcessorEntity, EventProcessor, String, EventProcessorRepository> {
    public EventProcessorDao(EventProcessorConverter converter, EventProcessorRepository repository) {
        super(converter, repository);
    }

    public Optional<EventProcessor> findByServiceNameAndEventName(String serviceName, String eventName) {
        return converter.convertEntity(repository.findByServiceNameAndEventName(serviceName, eventName));
    }

    public List<EventProcessor> getByEventName(String eventName) {
        return converter.convertEntity(repository.getByEventName(eventName));
    }

    public List<EventProcessor> getByLastAccessBefore(LocalDateTime expiration) {
        return converter.convertEntity(repository.getByLastAccessBefore(expiration));
    }

    public List<EventProcessor> getByServiceName(String serviceName) {
        return converter.convertEntity(repository.getByServiceName(serviceName));
    }
}
