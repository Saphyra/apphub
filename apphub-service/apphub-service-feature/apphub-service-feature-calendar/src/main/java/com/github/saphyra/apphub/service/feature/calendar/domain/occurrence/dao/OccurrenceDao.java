package com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.dao;

import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OccurrenceDao {
    private final UuidConverter uuidConverter;
    private final OccurrenceConverter converter;
    private final OccurrenceRepository repository;

    public void save(Occurrence occurrence) {
        repository.save(converter.convertDomain(occurrence));
    }

    public List<Occurrence> getByEventId(UUID eventId) {
        return converter.convertEntity(repository.getByEventId(uuidConverter.convertDomain(eventId)));
    }

    public void delete(UUID eventId, Collection<UUID> occurrences) {
        repository.delete(uuidConverter.convertDomain(eventId), uuidConverter.convertDomain(occurrences));
    }

    public void save(List<Occurrence> occurrences) {
        repository.save(converter.convertDomain(occurrences));
    }

    public Occurrence findByIdValidated(UUID eventId, UUID occurrenceId) {
        return converter.convertEntity(repository.findById(uuidConverter.convertDomain(eventId), uuidConverter.convertDomain(occurrenceId)))
            .orElseThrow(() -> ExceptionFactory.notFound("Occurrence not found by id " + occurrenceId + " in event " + eventId));
    }

    public List<Occurrence> getByBuckets(UUID userId, List<String> buckets) {
        String userIdString = uuidConverter.convertDomain(userId);

        return buckets.stream()
            .flatMap(bucket -> repository.getByBucket(userIdString, bucket).stream())
            .map(converter::convertEntity)
            .toList();
    }

    public void delete(List<Occurrence> occurrences) {
        List<BiWrapper<String, String>> ids = occurrences.stream()
            .map(occurrence -> new BiWrapper<>(
                uuidConverter.convertDomain(occurrence.getEventId()),
                uuidConverter.convertDomain(occurrence.getOccurrenceId())
            ))
            .toList();

        repository.delete(ids);
    }

    public void deleteByEventId(UUID eventId) {
        String eventIdString = uuidConverter.convertDomain(eventId);
        List<OccurrenceEntity> occurrences = repository.getByEventId(eventIdString);

        repository.delete(eventIdString, occurrences.stream().map(OccurrenceEntity::getOccurrenceId).toList());
    }
}
