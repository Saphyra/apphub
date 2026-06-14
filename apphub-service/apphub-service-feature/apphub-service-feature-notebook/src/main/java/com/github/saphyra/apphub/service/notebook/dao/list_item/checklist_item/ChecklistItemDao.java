package com.github.saphyra.apphub.service.notebook.dao.list_item.checklist_item;

import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChecklistItemDao {
    private final ChecklistItemRepository repository;
    private final ChecklistItemConverter converter;
    private final UuidConverter uuidConverter;

    public ChecklistItem findByIdValidated(UUID listItemId, UUID checklistItemId) {
        return converter.convertEntity(repository.findById(
                uuidConverter.convertDomain(listItemId),
                uuidConverter.convertDomain(checklistItemId)
            ))
            .orElseThrow(() -> ExceptionFactory.notFound("ChecklistItem not found by id " + checklistItemId + " in ListItem " + listItemId));
    }

    public void delete(UUID listItemId, UUID checklistItemId) {
        repository.delete(
            uuidConverter.convertDomain(listItemId),
            uuidConverter.convertDomain(checklistItemId)
        );
    }

    public void save(ChecklistItem checklistItem) {
        repository.save(converter.convertDomain(checklistItem));
    }

    public List<ChecklistItem> getByListItemId(UUID listItemId) {
        return converter.convertEntity(repository.getByListItemId(uuidConverter.convertDomain(listItemId)));
    }

    public void delete(List<ChecklistItem> checklistItems) {
        List<BiWrapper<String, String>> ids = checklistItems.stream()
            .map(checklistItem -> new BiWrapper<>(
                uuidConverter.convertDomain(checklistItem.getListItemId()),
                uuidConverter.convertDomain(checklistItem.getChecklistItemId())
            ))
            .toList();

        repository.delete(ids);
    }

    public void save(List<ChecklistItem> checklistItems) {
        repository.save(converter.convertDomain(checklistItems));
    }
}
