package com.github.saphyra.apphub.service.notebook.service.table;

import com.github.saphyra.apphub.api.notebook.model.ItemType;
import com.github.saphyra.apphub.api.notebook.model.ListItemType;
import com.github.saphyra.apphub.api.notebook.model.table.*;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.notebook.dao.checked_item.CheckedItem;
import com.github.saphyra.apphub.service.notebook.dao.checked_item.CheckedItemDao;
import com.github.saphyra.apphub.service.notebook.dao.column_type.ColumnTypeDao;
import com.github.saphyra.apphub.service.notebook.dao.content.ContentDao;
import com.github.saphyra.apphub.service.notebook.dao.dimension.DimensionDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDao;
import com.github.saphyra.apphub.service.notebook.dao.table_head.TableHeadDao;
import com.github.saphyra.apphub.service.notebook.service.table.column_data.ColumnDataServiceFetcher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
//TODO split
public class TableQueryService {
    private static final List<ListItemType> TABLE_TYPES = List.of(ListItemType.TABLE, ListItemType.CHECKLIST_TABLE, ListItemType.CUSTOM_TABLE);

    private final ListItemDao listItemDao;
    private final TableHeadDao tableHeadDao;
    private final ContentDao contentDao;
    private final DimensionDao dimensionDao;
    private final CheckedItemDao checkedItemDao;
    private final ColumnTypeDao columnTypeDao;
    private final ColumnDataServiceFetcher columnDataServiceFetcher;

    public TableResponse getTable(UUID listItemId) {
        ListItem listItem = listItemDao.findByIdValidated(listItemId);

        if (!TABLE_TYPES.contains(listItem.getType())) {
            throw ExceptionFactory.notLoggedException(HttpStatus.CONFLICT, ErrorCode.LIST_ITEM_NOT_FOUND, listItemId + " is not a kind of table, it is " + listItem.getType());
        }

        return TableResponse.builder()
            .title(listItem.getTitle())
            .parent(listItem.getParent())
            .tableHeads(getTableHeads(listItemId))
            .rows(getRows(listItemId))
            .build();
    }

    private List<TableHeadModel> getTableHeads(UUID listItemId) {
        return tableHeadDao.getByParent(listItemId)
            .stream()
            .map(tableHead -> TableHeadModel.builder()
                .tableHeadId(tableHead.getTableHeadId())
                .columnIndex(tableHead.getColumnIndex())
                .content(contentDao.findByParentValidated(tableHead.getTableHeadId()).getContent())
                .type(ItemType.EXISTING)
                .build())
            .collect(Collectors.toList());
    }

    private List<TableRowModel> getRows(UUID listItemId) {
        return dimensionDao.getByExternalReference(listItemId)
            .stream()
            .map(row -> TableRowModel.builder()
                .rowId(row.getDimensionId())
                .rowIndex(row.getIndex())
                .checked(checkedItemDao.findById(row.getDimensionId()).map(CheckedItem::getChecked).orElse(null))
                .itemType(ItemType.EXISTING)
                .columns(getColumns(row.getDimensionId()))
                .build())
            .collect(Collectors.toList());
    }

    private List<TableColumnModel> getColumns(UUID rowId) {
        return dimensionDao.getByExternalReference(rowId)
            .stream()
            .map(column -> {
                ColumnType columnType = columnTypeDao.findByIdValidated(column.getDimensionId()).getType();
                return TableColumnModel.builder()
                    .columnId(column.getDimensionId())
                    .columnIndex(column.getIndex())
                    .columnType(columnType)
                    .data(getDataForColumnType(column.getDimensionId(), columnType))
                    .itemType(ItemType.EXISTING)
                    .build();
            })
            .collect(Collectors.toList());
    }

    private Object getDataForColumnType(UUID columnId, ColumnType columnType) {
        return columnDataServiceFetcher.findColumnDataService(columnType)
            .getData(columnId);
    }
}
