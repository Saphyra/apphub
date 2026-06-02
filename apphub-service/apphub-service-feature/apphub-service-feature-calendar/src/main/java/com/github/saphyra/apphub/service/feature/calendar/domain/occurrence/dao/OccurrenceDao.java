package com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.dao;

import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
//TODO unit test
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
        Lists.partition(uuidConverter.convertDomain(occurrences), Constants.DYNAMO_DB_DELETE_MAX_BATCH_SIZE)
            .forEach(batch -> repository.delete(uuidConverter.convertDomain(eventId), batch));
    }

    public void save(List<Occurrence> occurrences) {
        Lists.partition(converter.convertDomain(occurrences), Constants.DYNAMO_DB_INSERT_MAX_BATCH_SIZE)
            .forEach(repository::save);
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

        Lists.partition(ids, Constants.DYNAMO_DB_DELETE_MAX_BATCH_SIZE)
            .forEach(repository::delete);
    }

    public void deleteByEventId(UUID eventId) {
        String eventIdString = uuidConverter.convertDomain(eventId);
        List<OccurrenceEntity> occurrences = repository.getByEventId(eventIdString);
        Lists.partition(occurrences, Constants.DYNAMO_DB_DELETE_MAX_BATCH_SIZE)
            .forEach(batch -> repository.delete(eventIdString, batch.stream().map(OccurrenceEntity::getOccurrenceId).toList()));
    }
}
