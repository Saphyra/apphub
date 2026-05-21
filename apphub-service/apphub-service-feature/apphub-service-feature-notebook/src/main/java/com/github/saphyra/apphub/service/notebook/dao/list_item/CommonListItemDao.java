package com.github.saphyra.apphub.service.notebook.dao.list_item;

import com.github.saphyra.apphub.lib.common_domain.DeleteByUserIdDao;
import com.github.saphyra.apphub.lib.common_domain.QuadWrapper;
import com.github.saphyra.apphub.lib.common_domain.TriWrapper;
import com.github.saphyra.apphub.service.notebook.dao.list_item.checklist_item.ChecklistItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.list_item.content.ParentType;
import com.github.saphyra.apphub.service.notebook.dao.list_item.list_item.ListItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

/**
 * A DAO that handles data with multiple types
 */
@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class CommonListItemDao implements DeleteByUserIdDao {
    /**
     * Deletes all the records belong to the given listItem:
     *
     * <ul>
     *     <li>The list item itself</li>
     *     <li>{@link ChecklistItem}s</li>
     *     <li>{@link TableHead}s</li>
     *     <li>{@link TableRow}s</li>
     *     <li>Contents with given {@link ParentType}s</li>
     * </ul>
     */
    public void delete(ListItem listItem, List<ParentType> parentTypes) {

    }

    public void saveChecklist(ListItem listItem, List<ChecklistItem> checklistItems, List<Content> contents) {

    }

    public TriWrapper<ListItem, List<ChecklistItem>, List<Content>> findChecklistValidated(UUID userId, UUID listItemId) {
        return null;
    }

    /**
     * Edits the given checklist:
     *
     * <ul>
     *     <li>Save the {@link ListItem}</li>
     *     <li>Delete the provided {@link ChecklistItem}s</li>
     *     <li>Saves the new and modified {@link ChecklistItem}s</li>
     *     <li>Aggregates and saves the {@link Content}s</li>
     * </ul>
     */
    public void editChecklist(ListItem listItem, List<ChecklistItem> deletedChecklistItems, List<ChecklistItem> newChecklistItems, List<ChecklistItem> modifiedChecklistItems, List<Content> contents) {

    }

    public void saveTable(ListItem listItem, List<TableHead> tableHeads, List<TableRow> rows, List<Content> contents) {

    }

    public QuadWrapper<ListItem, List<TableHead>, List<TableRow>, List<Content>> findTableValidated(UUID userId, UUID listItemId) {
        return null;
    }

    @Override
    public void deleteByUserId(UUID userId) {

    }
}
