package com.github.saphyra.apphub.service.feature.calendar.domain.label.dao;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class LabelDao {
    private final UuidConverter uuidConverter;
    private final LabelConverter converter;
    private final LabelRepository repository;

    public List<Label> getByLabelIds(UUID userId, List<UUID> labelIds) {
        return converter.convertEntity(repository.getByLabelIds(uuidConverter.convertDomain(userId), uuidConverter.convertDomain(labelIds)));
    }

    public void save(Label label) {
        repository.save(converter.convertDomain(label));
    }

    public Label findByIdValidated(UUID userId, UUID labelId) {
        return findById(userId, labelId)
            .orElseThrow(() -> ExceptionFactory.notFound("Label not found by id " + labelId));
    }

    public Optional<Label> findById(UUID userId, UUID labelId) {
        return converter.convertEntity(repository.findById(uuidConverter.convertDomain(userId), uuidConverter.convertDomain(labelId)));
    }

    public List<Label> getByUserId(UUID userId) {
        return converter.convertEntity(repository.getByUserId(uuidConverter.convertDomain(userId)));
    }

    public void delete(UUID userId, UUID labelId) {
        repository.delete(uuidConverter.convertDomain(userId), uuidConverter.convertDomain(labelId));
    }
}
