package com.github.saphyra.apphub.service.notebook.service.clone;

import com.github.saphyra.apphub.api.feature.notebook.model.table.ColumnType;
import com.github.saphyra.apphub.lib.common_domain.QuadWrapper;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.service.notebook.dao.list_item.CommonListItemDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.list_item.content.ContentDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.content.ContentFactory;
import com.github.saphyra.apphub.service.notebook.dao.list_item.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.list_item.ListItemDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.list_item.ListItemFactory;
import com.github.saphyra.apphub.service.notebook.dao.list_item.table.row.TableColumn;
import com.github.saphyra.apphub.service.notebook.dao.list_item.table.row.TableColumnFactory;
import com.github.saphyra.apphub.service.notebook.dao.list_item.table.head.TableHead;
import com.github.saphyra.apphub.service.notebook.dao.list_item.table.head.TableHeadDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.table.head.TableHeadFactory;
import com.github.saphyra.apphub.service.notebook.dao.list_item.table.row.TableRow;
import com.github.saphyra.apphub.service.notebook.dao.list_item.table.row.TableRowDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.table.row.TableRowFactory;
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
    private final ContentDao contentDao;
    private final CommonListItemDao commonListItemDao;
    private final TableHeadDao tableHeadDao;
    private final TableRowDao tableRowDao;

    void cloneTable(UUID parent, ListItem toClone) {
        ListItem clone = listItemFactory.clone(parent, toClone);
        listItemDao.save(clone);

        QuadWrapper<ListItem, List<TableHead>, List<TableRow>, List<Content>> table = commonListItemDao.findTableValidated(toClone.getUserId(), toClone.getListItemId());
        Map<UUID, String> contentMap = table.getEntity4()
            .stream()
            .flatMap(c -> c.getContent().entrySet().stream())
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        List<Content> clonedContents = new ArrayList<>();

        cloneTableHeads(clone.getListItemId(), table.getEntity2(), contentMap, clonedContents);
        cloneTableRows(clone.getListItemId(), table.getEntity3(), contentMap, clonedContents);

        contentDao.save(clone.getListItemId(), clonedContents);
    }

    private void cloneTableRows(UUID listItemId, List<TableRow> rows, Map<UUID, String> contentMap, List<Content> clonedContents) {
        List<TableRow> clonedTableRows = rows.stream()
            .map(tableRow -> tableRowFactory.create(
                listItemId,
                tableRow.getIndex(),
                tableRow.getChecked(),
                cloneColumns(listItemId, tableRow.getColumns(), contentMap, clonedContents)
            ))
            .toList();

        tableRowDao.save(clonedTableRows);
    }

    private List<TableColumn> cloneColumns(UUID listItemId, List<TableColumn> columns, Map<UUID, String> contentMap, List<Content> clonedContents) {
        return columns.stream()
            .map(column -> {
                TableColumn clonedColumn = tableColumnFactory.create(column.getIndex(), column.getType());

                if (clonedColumn.getType() != ColumnType.EMPTY) {
                    Content clonedContent;

                    if (clonedColumn.getType().isFile()) {
                        UUID storedFileId = uuidConverter.convertEntity(contentMap.get(column.getColumnId()));
                        UUID clonedFileId = storageProxy.cloneFile(storedFileId);
                        clonedContent = contentFactory.create(listItemId, clonedColumn.getColumnId(), uuidConverter.convertDomain(clonedFileId));
                    } else {
                        clonedContent = contentFactory.create(listItemId, clonedColumn.getColumnId(), contentMap.get(column.getColumnId()));
                    }

                    clonedContents.add(clonedContent);
                }

                return clonedColumn;
            })
            .toList();
    }

    private void cloneTableHeads(UUID listItemId, List<TableHead> tableHeads, Map<UUID, String> contentMap, List<Content> clonedContents) {
        List<TableHead> clonedTableHeads = tableHeads.stream()
            .map(tableHead -> {
                TableHead clonedTableHead = tableHeadFactory.clone(tableHead);
                Content clonedContent = contentFactory.create(listItemId, clonedTableHead.getTableHeadId(), contentMap.get(tableHead.getTableHeadId()));

                clonedContents.add(clonedContent);

                return clonedTableHead;
            })
            .toList();

        tableHeadDao.save(listItemId, clonedTableHeads);
    }
}
