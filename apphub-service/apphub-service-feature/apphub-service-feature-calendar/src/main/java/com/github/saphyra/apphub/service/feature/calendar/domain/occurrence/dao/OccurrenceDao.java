package com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
//TODO unit test
public class OccurrenceDao {
    public void save(UUID userId, Occurrence occurrence) {

    }

    public List<Occurrence> getByEventId(UUID userId, UUID eventId) {
        return null;
    }

    public void delete(UUID userId, Collection<UUID> occurrences) {

    }

    public void save(UUID userId, List<Occurrence> occurrences) {

    }

    public Occurrence findByIdValidated(UUID userId, UUID eventId, UUID occurrenceId) {
        return null;
    }

    public List<Occurrence> getByBuckets(UUID userId, List<String> buckets) {
        return null;
    }
}
