package com.github.saphyra.apphub.service.notebook.service.table;

import com.github.saphyra.apphub.api.notebook.model.ItemType;
import com.github.saphyra.apphub.api.notebook.model.table.ColumnType;
import com.github.saphyra.apphub.api.notebook.model.table.TableColumnModel;
import com.github.saphyra.apphub.api.notebook.model.table.TableHeadModel;
import com.github.saphyra.apphub.api.notebook.model.table.TableResponse;
import com.github.saphyra.apphub.api.notebook.model.table.TableRowModel;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.notebook.dao.checked_item.CheckedItem;
import com.github.saphyra.apphub.service.notebook.dao.checked_item.CheckedItemDao;
import com.github.saphyra.apphub.service.notebook.dao.column_type.ColumnTypeDao;
import com.github.saphyra.apphub.service.notebook.dao.content.ContentDao;
import com.github.saphyra.apphub.service.notebook.dao.dimension.DimensionDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDao;
import com.github.saphyra.apphub.service.notebook.dao.table_head.TableHeadDao;
import com.github.saphyra.apphub.service.notebook.service.table.column_data.ColumnDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final ListItemDao listItemDao;
    private final TableHeadDao tableHeadDao;
    private final ContentDao contentDao;
    private final DimensionDao dimensionDao;
    private final CheckedItemDao checkedItemDao;
    private final ColumnTypeDao columnTypeDao;
    private final List<ColumnDataService> columnDataServices;

    public TableResponse getTable(UUID listItemId) {
        ListItem listItem = listItemDao.findByIdValidated(listItemId);

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
                    .build();
            })
            .collect(Collectors.toList());
    }

    private Object getDataForColumnType(UUID columnId, ColumnType columnType) {
        return columnDataServices.stream()
            .filter(columnDataService -> columnDataService.canProcess(columnType))
            .findAny()
            .orElseThrow(() -> ExceptionFactory.reportedException("ColumnDataService not found for columnType " + columnType))
            .getData(columnId);
    }
}
