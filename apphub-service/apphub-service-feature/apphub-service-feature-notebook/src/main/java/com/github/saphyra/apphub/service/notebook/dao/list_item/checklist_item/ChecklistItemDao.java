package com.github.saphyra.apphub.service.notebook.dao.list_item.checklist_item;

import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.google.common.collect.Lists;
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

        Lists.partition(ids, Constants.DYNAMO_DB_WRITE_MAX_BATCH_SIZE)
            .forEach(repository::delete);
    }

    public void save(List<ChecklistItem> checklistItems) {
        Lists.partition(checklistItems, Constants.DYNAMO_DB_WRITE_MAX_BATCH_SIZE)
            .stream()
            .map(converter::convertDomain)
            .forEach(repository::save);
    }
}
