package com.github.saphyra.apphub.service.notebook.service.clone;

import com.github.saphyra.apphub.api.feature.notebook.model.table.ColumnType;
import com.github.saphyra.apphub.lib.common_domain.QuadWrapper;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.service.notebook.dao.list_item.Content;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ContentFactory;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemFactory;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ParentType;
import com.github.saphyra.apphub.service.notebook.dao.list_item.TableColumn;
import com.github.saphyra.apphub.service.notebook.dao.list_item.TableColumnFactory;
import com.github.saphyra.apphub.service.notebook.dao.list_item.TableHead;
import com.github.saphyra.apphub.service.notebook.dao.list_item.TableHeadFactory;
import com.github.saphyra.apphub.service.notebook.dao.list_item.TableRow;
import com.github.saphyra.apphub.service.notebook.dao.list_item.TableRowFactory;
import com.github.saphyra.apphub.service.notebook.service.StorageProxy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
//TODO split
class TableCloneService {
    private final ListItemDao listItemDao;
    private final ListItemFactory listItemFactory;
    private final UuidConverter uuidConverter;
    private final TableHeadFactory tableHeadFactory;
    private final ContentFactory contentFactory;
    private final TableRowFactory tableRowFactory;
    private final TableColumnFactory tableColumnFactory;
    private final StorageProxy storageProxy;

    void cloneTable(UUID parent, ListItem toClone) {
        ListItem clone = listItemFactory.clone(parent, toClone);
        listItemDao.saveListItem(clone);

        QuadWrapper<ListItem, List<TableHead>, List<TableRow>, List<Content>> table = listItemDao.findTableValidated(toClone.getUserId(), toClone.getListItemId());
        Map<UUID, String> contentMap = table.getEntity4()
            .stream()
            .flatMap(c -> c.getContent().entrySet().stream())
            .collect(Collectors.toMap(entry -> uuidConverter.convertEntity(entry.getKey()), Map.Entry::getValue));

        List<Content> clonedContents = new ArrayList<>();

        cloneTableHeads(clone.getUserId(), clone.getListItemId(), table.getEntity2(), contentMap, clonedContents);
        cloneTableRows(clone.getUserId(), clone.getListItemId(), table.getEntity3(), contentMap, clonedContents);

        listItemDao.saveContents(clone.getUserId(), clone.getListItemId(), clonedContents);
    }

    private void cloneTableRows(UUID userId, UUID listItemId, List<TableRow> rows, Map<UUID, String> contentMap, List<Content> clonedContents) {
        List<TableRow> clonedTableRows = rows.stream()
            .map(tableRow -> tableRowFactory.create(
                userId,
                listItemId,
                tableRow.getIndex(),
                tableRow.getChecked(),
                cloneColumns(userId, listItemId, tableRow.getColumns(), contentMap, clonedContents)
            ))
            .toList();

        listItemDao.saveTableRows(userId, listItemId, clonedTableRows);
    }

    private List<TableColumn> cloneColumns(UUID userId, UUID listItemId, List<TableColumn> columns, Map<UUID, String> contentMap, List<Content> clonedContents) {
        return columns.stream()
            .map(column -> {
                TableColumn clonedColumn = tableColumnFactory.create(column.getIndex(), column.getType());

                if (clonedColumn.getType() != ColumnType.EMPTY) {
                    Content clonedContent;

                    if (clonedColumn.getType().isFile()) {
                        UUID storedFileId = uuidConverter.convertEntity(contentMap.get(column.getColumnId()));
                        UUID clonedFileId = storageProxy.cloneFile(storedFileId);
                        clonedContent = contentFactory.create(userId, listItemId, ParentType.TABLE_COLUMN, clonedColumn.getColumnId(), uuidConverter.convertDomain(clonedFileId));
                    } else {
                        clonedContent = contentFactory.create(userId, listItemId, ParentType.TABLE_COLUMN, clonedColumn.getColumnId(), contentMap.get(column.getColumnId()));
                    }

                    clonedContents.add(clonedContent);
                }

                return clonedColumn;
            })
            .toList();
    }

    private void cloneTableHeads(UUID userId, UUID listItemId, List<TableHead> tableHeads, Map<UUID, String> contentMap, List<Content> clonedContents) {
        List<TableHead> clonedTableHeads = tableHeads.stream()
            .map(tableHead -> {
                TableHead clonedTableHead = tableHeadFactory.clone(listItemId, tableHead);
                Content clonedContent = contentFactory.create(userId, listItemId, ParentType.TABLE_HEAD, clonedTableHead.getTableHeadId(), contentMap.get(tableHead.getTableHeadId()));

                clonedContents.add(clonedContent);

                return clonedTableHead;
            })
            .toList();

        listItemDao.saveTableHeads(userId, listItemId, clonedTableHeads);
    }
}
