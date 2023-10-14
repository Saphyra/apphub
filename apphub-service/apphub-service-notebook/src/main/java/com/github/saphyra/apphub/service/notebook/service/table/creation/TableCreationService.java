package com.github.saphyra.apphub.service.notebook.service.table.creation;

import com.github.saphyra.apphub.api.notebook.model.ListItemType;
import com.github.saphyra.apphub.api.notebook.model.table.*;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.notebook.dao.checked_item.CheckedItem;
import com.github.saphyra.apphub.service.notebook.dao.checked_item.CheckedItemDao;
import com.github.saphyra.apphub.service.notebook.dao.checked_item.CheckedItemFactory;
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
import com.github.saphyra.apphub.service.notebook.service.ListItemFactory;
import com.github.saphyra.apphub.service.notebook.service.table.column_data.ColumnDataService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO split
//TODO unit test
public class TableCreationService {
    private final TableCreationRequestValidator tableCreationRequestValidator;
    private final ListItemFactory listItemFactory;
    private final TableHeadFactory tableHeadFactory;
    private final TableHeadDao tableHeadDao;
    private final ContentFactory contentFactory;
    private final ContentDao contentDao;
    private final List<ColumnDataService> columnDataServices;
    private final DimensionFactory dimensionFactory;
    private final DimensionDao dimensionDao;
    private final CheckedItemFactory checkedItemFactory;
    private final CheckedItemDao checkedItemDao;

    @Transactional
    public List<TableFileUploadResponse> create(UUID userId, CreateTableRequest request) {
        tableCreationRequestValidator.validate(request);

        ListItem listItem = listItemFactory.create(userId, request.getTitle(), request.getParent(), request.getListItemType());

        request.getTableHeads()
                .forEach(tableHeadModel -> saveTableHead(userId, listItem.getListItemId(), tableHeadModel));

        return saveRows(userId, listItem.getListItemId(), request.getRows(), listItem.getType());
    }

    private void saveTableHead(UUID userId, UUID listItemId, TableHeadModel tableHeadModel) {
        TableHead tableHead = tableHeadFactory.create(userId, listItemId, tableHeadModel.getColumnIndex());
        tableHeadDao.save(tableHead);

        Content content = contentFactory.create(listItemId, tableHead.getTableHeadId(), userId, tableHeadModel.getContent());
        contentDao.save(content);
    }

    private List<TableFileUploadResponse> saveRows(UUID userId, UUID listItemId, List<TableRowModel> rows, ListItemType listItemType) {
        return rows.stream()
                .flatMap(tableRowModel -> saveRow(userId, listItemId, tableRowModel, listItemType))
                .toList();
    }

    private Stream<TableFileUploadResponse> saveRow(UUID userId, UUID listItemId, TableRowModel model, ListItemType listItemType) {
        Dimension row = dimensionFactory.create(userId, listItemId, model.getRowIndex());
        dimensionDao.save(row);

        if (listItemType == ListItemType.CHECKLIST_TABLE) {
            CheckedItem checkedItem = checkedItemFactory.create(userId, row.getDimensionId(), model.getChecked());
            checkedItemDao.save(checkedItem);
        }

        return saveColumns(userId, listItemId, row, model.getColumns()).stream();
    }

    private List<TableFileUploadResponse> saveColumns(UUID userId, UUID listItemId, Dimension row, List<TableColumnModel> columns) {
        return columns.stream()
                .map(tableColumnModel -> saveColumn(userId, listItemId, row, tableColumnModel))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }

    private Optional<TableFileUploadResponse> saveColumn(UUID userId, UUID listItemId, Dimension row, TableColumnModel tableColumnModel) {
        return columnDataServices.stream()
                .filter(columnDataService -> columnDataService.canProcess(tableColumnModel.getColumnType()))
                .findAny()
                .orElseThrow(() -> ExceptionFactory.reportedException(HttpStatus.NOT_IMPLEMENTED, "Unhandled columnType: " + tableColumnModel.getColumnType()))
                .save(userId, listItemId, row.getDimensionId(), tableColumnModel);
    }
}
