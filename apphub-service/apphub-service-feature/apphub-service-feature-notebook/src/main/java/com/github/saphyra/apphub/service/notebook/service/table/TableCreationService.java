package com.github.saphyra.apphub.service.notebook.service.table;

import com.github.saphyra.apphub.api.feature.notebook.model.table.CreateTableRequest;
import com.github.saphyra.apphub.api.feature.notebook.model.table.TableColumnModel;
import com.github.saphyra.apphub.api.feature.notebook.model.table.TableFileUploadResponse;
import com.github.saphyra.apphub.api.feature.notebook.model.table.TableHeadModel;
import com.github.saphyra.apphub.api.feature.notebook.model.table.TableRowModel;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.notebook.dao.list_item.CommonListItemDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.list_item.content.ContentFactory;
import com.github.saphyra.apphub.service.notebook.dao.list_item.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.list_item.ListItemDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.list_item.ListItemFactory;
import com.github.saphyra.apphub.service.notebook.dao.list_item.content.ParentType;
import com.github.saphyra.apphub.service.notebook.dao.list_item.table.row.TableColumn;
import com.github.saphyra.apphub.service.notebook.dao.list_item.table.row.TableColumnFactory;
import com.github.saphyra.apphub.service.notebook.dao.list_item.table.head.TableHead;
import com.github.saphyra.apphub.service.notebook.dao.list_item.table.head.TableHeadFactory;
import com.github.saphyra.apphub.service.notebook.dao.list_item.table.row.TableRow;
import com.github.saphyra.apphub.service.notebook.dao.list_item.table.row.TableRowFactory;
import com.github.saphyra.apphub.service.notebook.service.table.column_data.ColumnDataServiceProvider;
import com.github.saphyra.apphub.service.notebook.service.table.validator.TableCreationRequestValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class TableCreationService {
    private final TableCreationRequestValidator tableCreationRequestValidator;
    private final ListItemFactory listItemFactory;
    private final ListItemDao listItemDao;
    private final TableHeadFactory tableHeadFactory;
    private final ContentFactory contentFactory;
    private final TableRowFactory tableRowFactory;
    private final TableColumnFactory tableColumnFactory;
    private final ColumnDataServiceProvider columnDataServiceProvider;
    private final UuidConverter uuidConverter;
    private final CommonListItemDao commonListItemDao;

    @Transactional
    public List<TableFileUploadResponse> create(UUID userId, CreateTableRequest request) {
        tableCreationRequestValidator.validate(userId, request);

        ListItem listItem = listItemFactory.create(userId, request.getParent(), request.getTitle(), request.getListItemType());

        List<Content> contents = new ArrayList<>();
        List<TableHead> tableHeads = getTableHeads(userId, listItem.getListItemId(), request.getTableHeads(), contents);
        List<TableRow> rows = createRows(userId, listItem.getListItemId(), request.getRows(), contents);

        commonListItemDao.saveTable(listItem, tableHeads, rows, contents);

        return collectFilesToUpload(rows, contents);
    }

    private List<TableHead> getTableHeads(UUID userId, UUID listItemId, List<TableHeadModel> tableHeads, List<Content> contents) {
        return tableHeads.stream()
            .map(model -> {
                TableHead tableHead = tableHeadFactory.create(model.getColumnIndex());

                Content content = contentFactory.create(userId, listItemId, ParentType.TABLE_HEAD, tableHead.getTableHeadId(), model.getContent());
                contents.add(content);

                return tableHead;
            })
            .toList();
    }

    private List<TableRow> createRows(UUID userId, UUID listItemId, List<TableRowModel> rows, List<Content> contents) {
        return rows.stream()
            .map(row -> {
                return tableRowFactory.create(
                    userId,
                    listItemId,
                    row.getRowIndex(),
                    row.getChecked(),
                    getColumns(userId, listItemId, row.getColumns(), contents)
                );
            })
            .toList();
    }

    private List<TableColumn> getColumns(UUID userId, UUID listItemId, List<TableColumnModel> columns, List<Content> contents) {
        return columns.stream()
            .map(model -> {
                TableColumn column = tableColumnFactory.create(model.getColumnIndex(), model.getColumnType());


                columnDataServiceProvider.getForType(model.getColumnType())
                    .serialize(model.getData())
                    .ifPresent(data -> {
                        Content content = contentFactory.create(userId, listItemId, ParentType.TABLE_COLUMN, column.getColumnId(), data);
                        contents.add(content);
                    });


                return column;
            })
            .toList();
    }

    private List<TableFileUploadResponse> collectFilesToUpload(List<TableRow> rows, List<Content> contents) {
        List<TableFileUploadResponse> result = new ArrayList<>();
        rows.forEach(row -> row.getColumns().forEach(column -> {
            if (column.getType().isFile()) {
                TableFileUploadResponse response = TableFileUploadResponse.builder()
                    .rowIndex(row.getIndex())
                    .columnIndex(column.getIndex())
                    .storedFileId(getStoredFileId(column.getColumnId(), contents))
                    .build();
                result.add(response);
            }
        }));

        return result;
    }

    private UUID getStoredFileId(UUID columnId, List<Content> contents) {
        String value = contents.stream()
            .flatMap(content -> content.getContent().entrySet().stream())
            .filter(entry -> entry.getKey().equals(uuidConverter.convertDomain(columnId)))
            .findFirst()
            .orElseThrow(() -> ExceptionFactory.reportedException("No content found for columnId " + columnId))
            .getValue();
        return uuidConverter.convertEntity(value);
    }
}
