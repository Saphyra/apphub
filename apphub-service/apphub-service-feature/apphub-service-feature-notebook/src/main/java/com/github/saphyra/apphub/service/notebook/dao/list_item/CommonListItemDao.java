package com.github.saphyra.apphub.service.notebook.dao.list_item;

import com.github.saphyra.apphub.lib.common_domain.DeleteByUserIdDao;
import com.github.saphyra.apphub.lib.common_domain.QuadWrapper;
import com.github.saphyra.apphub.lib.common_domain.TriWrapper;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.service.notebook.dao.list_item.checklist_item.ChecklistItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.checklist_item.ChecklistItemDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.list_item.content.ContentDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.list_item.ListItemDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.table.head.TableHead;
import com.github.saphyra.apphub.service.notebook.dao.list_item.table.head.TableHeadDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.table.row.TableRow;
import com.github.saphyra.apphub.service.notebook.dao.list_item.table.row.TableRowDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * A DAO that handles data with multiple types
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CommonListItemDao implements DeleteByUserIdDao {
    private final ListItemDao listItemDao;
    private final ChecklistItemDao checklistItemDao;
    private final ContentDao contentDao;
    private final TableHeadDao tableHeadDao;
    private final TableRowDao tableRowDao;
    private final CommonListItemRepository repository;
    private final UuidConverter uuidConverter;

    /**
     * Deletes all the records belong to the given listItem:
     *
     * <ul>
     *     <li>The list item itself</li>
     *     <li>{@link ChecklistItem}s</li>
     *     <li>{@link TableHead}s</li>
     *     <li>{@link TableRow}s</li>
     * </ul>
     */
    public void delete(ListItem listItem) {
        listItemDao.delete(listItem);
        repository.deleteByListItemId(uuidConverter.convertDomain(listItem.getListItemId()));
    }

    public void saveChecklist(ListItem listItem, List<ChecklistItem> checklistItems, List<Content> contents) {
        listItemDao.save(listItem);
        checklistItemDao.save(checklistItems);
        contentDao.save(listItem.getListItemId(), contents);
    }

    public TriWrapper<ListItem, List<ChecklistItem>, List<Content>> findChecklistValidated(UUID userId, UUID listItemId) {
        return new TriWrapper<>(
            listItemDao.findByIdValidated(userId, listItemId),
            checklistItemDao.getByListItemId(listItemId),
            contentDao.getByListItemId(listItemId)
        );
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
    public void editChecklist(UUID listItemId, List<ChecklistItem> deletedChecklistItems, List<ChecklistItem> newChecklistItems, List<ChecklistItem> modifiedChecklistItems, List<Content> contents) {
        checklistItemDao.delete(deletedChecklistItems);
        checklistItemDao.save(Stream.concat(newChecklistItems.stream(), modifiedChecklistItems.stream()).toList());
        contentDao.save(listItemId, contents);
    }

    public void saveTable(ListItem listItem, List<TableHead> tableHeads, List<TableRow> rows, List<Content> contents) {
        listItemDao.save(listItem);
        tableHeadDao.save(listItem.getListItemId(), tableHeads);
        tableRowDao.save(rows);
        contentDao.save(listItem.getListItemId(), contents);
    }

    public QuadWrapper<ListItem, List<TableHead>, List<TableRow>, List<Content>> findTableValidated(UUID userId, UUID listItemId) {
        return new QuadWrapper<>(
            listItemDao.findByIdValidated(userId, listItemId),
            tableHeadDao.findByListItemIdValidated(listItemId),
            tableRowDao.getByListItemId(listItemId),
            contentDao.getByListItemId(listItemId)
        );
    }

    @Override
    public void deleteByUserId(UUID userId) {
        listItemDao.getByUserId(userId)
            .forEach(this::delete);
    }
}
