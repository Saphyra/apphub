package com.github.saphyra.apphub.service.platform.event_gateway.dao;

import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

interface EventProcessorRepository extends CrudRepository<EventProcessorEntity, String> {
    Optional<EventProcessorEntity> findByServiceNameAndEventName(String serviceName, String eventName);

    List<EventProcessorEntity> getByEventName(String eventName);

    List<EventProcessorEntity> getByServiceName(String serviceName);

    List<EventProcessorEntity> getByLastAccessBefore(LocalDateTime expiration);
}
