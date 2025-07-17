package com.github.saphyra.apphub.service.calendar.domain.event.dao;

import org.springframework.data.repository.CrudRepository;

interface EventRepository extends CrudRepository<EventEntity, String> {
}
