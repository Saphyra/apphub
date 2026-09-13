package com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.dao;

import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OccurrenceDao {
    private final UuidConverter uuidConverter;
    private final OccurrenceConverter converter;
    private final OccurrenceRepository repository;
    private final OccurrenceCache occurrenceCache;

    public void save(Occurrence occurrence) {
        repository.save(converter.convertDomain(occurrence));
        occurrenceCache.invalidate(occurrence.getEventId());
    }

    public Map<UUID, Occurrence> getByEventId(UUID eventId) {
        return occurrenceCache.get(
            eventId,
            () -> converter.convertEntity(repository.getByEventId(uuidConverter.convertDomain(eventId)))
                .stream()
                .collect(Collectors.toMap(Occurrence::getOccurrenceId, o -> o))
        );
    }

    public void delete(UUID eventId, Collection<UUID> occurrences) {
        repository.delete(uuidConverter.convertDomain(eventId), uuidConverter.convertDomain(occurrences));
        occurrenceCache.invalidate(eventId);
    }

    public void save(List<Occurrence> occurrences) {
        repository.save(converter.convertDomain(occurrences));
        occurrences.stream()
            .map(Occurrence::getEventId)
            .distinct()
            .forEach(occurrenceCache::invalidate);
    }

    public Occurrence findByIdValidated(UUID eventId, UUID occurrenceId) {
        return findById(eventId, occurrenceId)
            .orElseThrow(() -> ExceptionFactory.notFound("Occurrence not found by id " + occurrenceId + " in event " + eventId));
    }

    public Optional<Occurrence> findById(UUID eventId, UUID occurrenceId) {
        return Optional.ofNullable(getByEventId(eventId).get(occurrenceId));
    }

    @Deprecated(forRemoval = true)
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
        occurrences.stream()
            .map(Occurrence::getEventId)
            .distinct()
            .forEach(occurrenceCache::invalidate);
    }

    public void deleteByEventId(UUID eventId) {
        String eventIdString = uuidConverter.convertDomain(eventId);
        List<OccurrenceEntity> occurrences = repository.getByEventId(eventIdString);

        repository.delete(eventIdString, occurrences.stream().map(OccurrenceEntity::getOccurrenceId).toList());
        occurrenceCache.invalidate(eventId);
    }
}
