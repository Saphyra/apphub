package com.github.saphyra.apphub.service.notebook.service.clone;

import com.github.saphyra.apphub.api.notebook.model.ListItemType;
import com.github.saphyra.apphub.api.notebook.model.table.ColumnType;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.notebook.dao.checked_item.CheckedItem;
import com.github.saphyra.apphub.service.notebook.dao.checked_item.CheckedItemDao;
import com.github.saphyra.apphub.service.notebook.dao.checked_item.CheckedItemFactory;
import com.github.saphyra.apphub.service.notebook.dao.column_type.ColumnTypeDao;
import com.github.saphyra.apphub.service.notebook.dao.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.content.ContentDao;
import com.github.saphyra.apphub.service.notebook.dao.dimension.Dimension;
import com.github.saphyra.apphub.service.notebook.dao.dimension.DimensionDao;
import com.github.saphyra.apphub.service.notebook.dao.dimension.DimensionFactory;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.table_head.TableHead;
import com.github.saphyra.apphub.service.notebook.dao.table_head.TableHeadDao;
import com.github.saphyra.apphub.service.notebook.dao.table_head.TableHeadFactory;
import com.github.saphyra.apphub.service.notebook.service.ContentFactory;
import com.github.saphyra.apphub.service.notebook.service.table.column_data.ColumnDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO split
//TODO unit test
class TableCloneService {
    private final TableHeadDao tableHeadDao;
    private final TableHeadFactory tableHeadFactory;
    private final DimensionDao dimensionDao;
    private final DimensionFactory dimensionFactory;
    private final CheckedItemDao checkedItemDao;
    private final CheckedItemFactory checkedItemFactory;
    private final List<ColumnDataService> columnDataServices;
    private final ContentDao contentDao;
    private final ContentFactory contentFactory;
    private final ColumnTypeDao columnTypeDao;

    void clone(ListItem original, ListItem clone) {
        cloneTableHeads(original, clone);
        cloneRows(original, clone);
    }

    private void cloneTableHeads(ListItem original, ListItem clone) {
        tableHeadDao.getByParent(original.getListItemId())
            .forEach(tableHead -> cloneTableHead(clone, tableHead));
    }

    private void cloneTableHead(ListItem clone, TableHead tableHead) {
        TableHead tableHeadClone = tableHeadFactory.create(clone.getUserId(), clone.getListItemId(), tableHead.getColumnIndex());
        tableHeadDao.save(tableHeadClone);

        Content tableHeadContent = contentDao.findByParentValidated(tableHead.getTableHeadId());
        Content tableHeadContentClone = contentFactory.create(clone.getListItemId(), tableHeadClone.getTableHeadId(), clone.getUserId(), tableHeadContent.getContent());
        contentDao.save(tableHeadContentClone);
    }

    private void cloneRows(ListItem original, ListItem clone) {
        dimensionDao.getByExternalReference(original.getListItemId())
            .forEach(row -> cloneRow(clone, row));
    }

    private void cloneRow(ListItem clone, Dimension originalRow) {
        Dimension clonedRow = dimensionFactory.create(clone.getUserId(), clone.getListItemId(), originalRow.getIndex());
        dimensionDao.save(clonedRow);

        if (clone.getType() == ListItemType.CHECKLIST_TABLE) {
            CheckedItem originalCheckedItem = checkedItemDao.findByIdValidated(originalRow.getDimensionId());
            CheckedItem clonedCheckedItem = checkedItemFactory.create(clone.getUserId(), clonedRow.getDimensionId(), originalCheckedItem.getChecked());
            checkedItemDao.save(clonedCheckedItem);
        }

        cloneColumns(clone, originalRow, clonedRow);
    }

    private void cloneColumns(ListItem clone, Dimension originalRow, Dimension clonedRow) {
        dimensionDao.getByExternalReference(originalRow.getDimensionId())
            .forEach(column -> cloneColumn(clone, clonedRow.getDimensionId(), column));
    }

    private void cloneColumn(ListItem clone, UUID rowId, Dimension column) {
        ColumnType columnType = columnTypeDao.findByIdValidated(column.getDimensionId())
                .getType();

        columnDataServices.stream()
            .filter(columnDataService -> columnDataService.canProcess(columnType))
            .findAny()
            .orElseThrow(() -> ExceptionFactory.reportedException("ColumnDataService not found for columnType " + columnType))
            .clone(clone, rowId, column);
    }
}
