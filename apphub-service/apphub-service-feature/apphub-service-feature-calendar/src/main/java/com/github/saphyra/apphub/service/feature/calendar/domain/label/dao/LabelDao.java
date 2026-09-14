package com.github.saphyra.apphub.service.feature.calendar.domain.label.dao;

import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class LabelDao {
    private final UuidConverter uuidConverter;
    private final LabelConverter converter;
    private final LabelRepository repository;
    private final LabelCache labelCache;

    public List<Label> getByLabelIds(UUID userId, List<UUID> labelIds) {
        return getByUserId(userId)
            .entrySet()
            .stream()
            .filter(entry -> labelIds.contains(entry.getKey()))
            .map(Map.Entry::getValue)
            .toList();
    }

    public void save(Label label) {
        repository.save(converter.convertDomain(label));
        labelCache.invalidate(label.getUserId());
    }

    public Label findByIdValidated(UUID userId, UUID labelId) {
        return findById(userId, labelId)
            .orElseThrow(() -> ExceptionFactory.notFound("Label not found by id " + labelId));
    }

    public Optional<Label> findById(UUID userId, UUID labelId) {
        return Optional.ofNullable(getByUserId(userId).get(labelId));
    }

    public Map<UUID, Label> getByUserId(UUID userId) {
        return labelCache.get(
            userId,
            () -> converter.convertEntity(repository.getByUserId(uuidConverter.convertDomain(userId)))
                .stream()
                .collect(Collectors.toMap(Label::getLabelId, e -> e))
        );
    }

    public void delete(UUID userId, UUID labelId) {
        log.info("Deleting label {} of user {}.", labelId, userId);
        repository.delete(uuidConverter.convertDomain(userId), uuidConverter.convertDomain(labelId));
        labelCache.invalidate(userId);
    }

    public List<Label> getByIds(List<BiWrapper<UUID, UUID>> labelIds) {
        return labelIds.stream()
            .flatMap(labelId -> findById(labelId.getEntity1(), labelId.getEntity2()).stream())
            .toList();
    }

    public void invalidate(UUID userId) {
        labelCache.invalidate(userId);
    }
}
