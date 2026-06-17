package com.github.saphyra.apphub.service.notebook.dao.pin_group;

import com.github.saphyra.apphub.lib.common_domain.DeleteByUserIdDao;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PinGroupDao implements DeleteByUserIdDao {
    private final PinGroupRepository repository;
    private final PinGroupConverter converter;
    private final UuidConverter uuidConverter;

    public void save(PinGroup pinGroup) {
        repository.save(converter.convertDomain(pinGroup));
    }

    public PinGroup findByIdValidated(UUID userId, UUID pinGroupId) {
        return converter.convertEntity(repository.findById(uuidConverter.convertDomain(userId), uuidConverter.convertDomain(pinGroupId)))
            .orElseThrow(() -> ExceptionFactory.notFound("PinGroup not found by id " + pinGroupId));
    }

    public List<PinGroup> getByUserId(UUID userId) {
        return converter.convertEntity(repository.getByUserId(uuidConverter.convertDomain(userId)));
    }

    public void delete(UUID userId, UUID pinGroupId) {
        repository.delete(uuidConverter.convertDomain(userId), uuidConverter.convertDomain(pinGroupId));
    }

    @Override
    public void deleteByUserId(UUID userId) {
        repository.deleteByUserId(uuidConverter.convertDomain(userId));
    }

    public void deleteListItemId(UUID userId, UUID listItemId) {
        List<PinGroup> toSave = getByUserId(userId)
            .stream()
            .filter(pinGroup -> pinGroup.getListItemIds().contains(listItemId))
            .peek(pinGroup -> pinGroup.removeListItem(listItemId))
            .toList();

        repository.save(converter.convertDomain(toSave));
    }
}
