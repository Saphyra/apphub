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

    public void save(UUID userId, Occurrence occurrence) {
        repository.save(uuidConverter.convertDomain(userId), converter.convertDomain(occurrence));
    }

    public List<Occurrence> getByEventId(UUID userId, UUID eventId) {
        return converter.convertEntity(repository.getByEventId(uuidConverter.convertDomain(userId), uuidConverter.convertDomain(eventId)));
    }

    public void delete(UUID userId, UUID eventId, Collection<UUID> occurrences) {
        Lists.partition(uuidConverter.convertDomain(occurrences), Constants.DYNAMO_DB_DELETE_MAX_BATCH_SIZE)
            .forEach(batch -> repository.delete(
                uuidConverter.convertDomain(userId),
                uuidConverter.convertDomain(eventId),
                batch
            ));
    }

    public void save(UUID userId, List<Occurrence> occurrences) {
        String userIdString = uuidConverter.convertDomain(userId);

        Lists.partition(converter.convertDomain(occurrences), Constants.DYNAMO_DB_INSERT_MAX_BATCH_SIZE)
            .forEach(batch -> repository.save(userIdString, batch));
    }

    public Occurrence findByIdValidated(UUID userId, UUID eventId, UUID occurrenceId) {
        return converter.convertEntity(repository.findById(
                uuidConverter.convertDomain(userId),
                uuidConverter.convertDomain(eventId),
                uuidConverter.convertDomain(occurrenceId)
            ))
            .orElseThrow(() -> ExceptionFactory.notFound("Occurrence not found by id " + occurrenceId + " in event " + eventId));
    }

    public List<Occurrence> getByBuckets(UUID userId, List<String> buckets) {
        String userIdString = uuidConverter.convertDomain(userId);

        return buckets.stream()
            .flatMap(bucket -> repository.getByBucket(userIdString, bucket).stream())
            .map(converter::convertEntity)
            .toList();
    }

    public void delete(UUID userId, List<Occurrence> occurrences) {
        List<BiWrapper<String, String>> ids = occurrences.stream()
            .map(occurrence -> new BiWrapper<>(
                uuidConverter.convertDomain(occurrence.getEventId()),
                uuidConverter.convertDomain(occurrence.getOccurrenceId())
            ))
            .toList();

        Lists.partition(ids, Constants.DYNAMO_DB_DELETE_MAX_BATCH_SIZE)
            .forEach(batch -> repository.delete(uuidConverter.convertDomain(userId), batch));
    }
}
