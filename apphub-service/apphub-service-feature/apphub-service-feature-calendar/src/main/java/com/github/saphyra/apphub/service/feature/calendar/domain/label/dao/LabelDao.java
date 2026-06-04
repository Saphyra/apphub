package com.github.saphyra.apphub.service.feature.calendar.domain.label.dao;

import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class LabelDao {
    private final UuidConverter uuidConverter;
    private final LabelConverter converter;
    private final LabelRepository repository;

    public List<Label> getByLabelIds(UUID userId, List<UUID> labelIds) {
        String userIdString = uuidConverter.convertDomain(userId);

        return Lists.partition(labelIds, Constants.DYNAMO_DB_QUERY_MAX_BATCH_SIZE)
            .stream()
            .map(batch -> repository.getByLabelIds(userIdString, uuidConverter.convertDomain(batch)))
            .flatMap(List::stream)
            .map(converter::convertEntity)
            .toList();
    }

    public void save(UUID userId, Label label) {
        repository.save(uuidConverter.convertDomain(userId), converter.convertDomain(label));
    }

    public Label findByIdValidated(UUID userId, UUID labelId) {
        return converter.convertEntity(repository.findById(uuidConverter.convertDomain(userId), uuidConverter.convertDomain(labelId)))
            .orElseThrow(() -> ExceptionFactory.notFound("Label not found by id " + labelId));
    }

    public List<Label> getByUserId(UUID userId) {
        return converter.convertEntity(repository.getByUserId(uuidConverter.convertDomain(userId)));
    }

    public void delete(UUID userId, UUID labelId) {
        repository.delete(uuidConverter.convertDomain(userId), uuidConverter.convertDomain(labelId));
    }
}
