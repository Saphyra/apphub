package com.github.saphyra.apphub.service.notebook.dao.list_item;

import com.github.saphyra.apphub.api.feature.notebook.model.ListItemType;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_domain.QuadWrapper;
import com.github.saphyra.apphub.lib.common_domain.TriWrapper;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class ListItemDao {
    public void save(ListItem listItem) {

    }

    public List<ListItem> getByUserIdAndParent(UUID userId, UUID parent) {
        return null;
    }

    public List<ListItem> getByUserIdAndType(UUID userId, ListItemType listItemType) {
        return null;
    }

    public Optional<ListItem> findById(UUID userId, UUID listItemId) {
        return null;
    }

    public ListItem findByIdValidated(UUID userId, UUID listItemId) {
        return findById(userId, listItemId)
            .orElseThrow(() -> ExceptionFactory.notLoggedException(HttpStatus.NOT_FOUND, ErrorCode.LIST_ITEM_NOT_FOUND, "ListItem not found with id " + listItemId));
    }

    public List<ListItem> getByUserId(UUID userId) {
        return null;
    }

    public List<Content> getContentsByUserId(UUID userId) {
        return null;
    }

    public List<ListItem> getByIds(UUID userId, List<UUID> listItemIds) {
        return null;
    }

    public ChecklistItem findChecklistItemValidated(UUID userId, UUID listItemId, UUID checklistItemId) {
        return null;
    }

    public void saveChecklist(ListItem listItem, List<ChecklistItem> checklistItems, List<Content> contents) {

    }

    public void deleteChecklist(UUID userId, UUID listItemId) {

    }

    public List<Content> getContents(UUID userId, UUID listItemId, ParentType parentType) {
        return null;
    }

    public void save(ChecklistItem checklistItem, List<Content> contents) {

    }

    public void saveContent(UUID userId, UUID listItemId, List<Content> contents) {

    }

    public void deleteChecklistItem(UUID userId, UUID listItemId, UUID checklistItemId) {
        //TODO delete Content too
    }

    public TriWrapper<ListItem, List<ChecklistItem>, List<Content>> findChecklistValidated(UUID userId, UUID listItemId) {
        return null;
    }

    public void saveChecklistItem(ChecklistItem checklistItem) {

    }

    public List<ChecklistItem> getChecklistItems(UUID userId, UUID listItemId) {
        return null;
    }

    public void deleteChecklistItems(List<ChecklistItem> checklistItems) {
        //TODO delete Content too
    }

    public void editChecklist(ListItem listItem, List<ChecklistItem> deletedChecklistItems, List<ChecklistItem> newChecklistItems, List<ChecklistItem> modifiedChecklistItems, List<Content> contents) {

    }

    public void saveChecklistItems(List<ChecklistItem> modifiedItems) {

    }

    public void saveTable(ListItem listItem, List<TableHead> tableHeads, List<TableRow> rows, List<Content> contents) {

    }

    public QuadWrapper<ListItem, List<TableHead>, List<TableRow>, List<Content>> findTableValidated(UUID userId, UUID listItemId) {
        return null;
    }

    public void save(UUID userId, UUID listItemId, List<TableHead> tableHeads) {

    }

    public void deleteTableRow(UUID userId, UUID listItemId, UUID tableRowId) {
    }

    public void save(TableRow tableRow) {

    }

    public void delete(ListItem listItem) {

    }

    public void deleteTableHeads(UUID userId, UUID listItemId, List<TableHead> tableHeads) {

    }

    public void deleteTableRows(UUID userId, UUID listItemId, List<TableRow> tableRows) {

    }

    public void deleteContents(UUID userId, UUID listItemId, List<Content> contents) {

    }

    public List<ListItem> getPinnedByUserId(UUID userId) {
        return null;
    }
}
