package com.github.saphyra.apphub.service.notebook.service.table;

import com.github.saphyra.apphub.api.feature.notebook.model.table.CreateTableRequest;
import com.github.saphyra.apphub.api.feature.notebook.model.table.TableColumnModel;
import com.github.saphyra.apphub.api.feature.notebook.model.table.TableFileUploadResponse;
import com.github.saphyra.apphub.api.feature.notebook.model.table.TableHeadModel;
import com.github.saphyra.apphub.api.feature.notebook.model.table.TableRowModel;
import com.github.saphyra.apphub.service.notebook.dao.list_item.CommonListItemDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.list_item.content.ContentFactory;
import com.github.saphyra.apphub.service.notebook.dao.list_item.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.list_item.ListItemFactory;
import com.github.saphyra.apphub.service.notebook.dao.list_item.table.head.TableHead;
import com.github.saphyra.apphub.service.notebook.dao.list_item.table.head.TableHeadFactory;
import com.github.saphyra.apphub.service.notebook.dao.list_item.table.row.TableColumn;
import com.github.saphyra.apphub.service.notebook.dao.list_item.table.row.TableColumnFactory;
import com.github.saphyra.apphub.service.notebook.dao.list_item.table.row.TableRow;
import com.github.saphyra.apphub.service.notebook.dao.list_item.table.row.TableRowFactory;
import com.github.saphyra.apphub.service.notebook.service.table.column_data.ColumnDataServiceProvider;
import com.github.saphyra.apphub.service.notebook.service.table.validator.TableCreationRequestValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class TableCreationService {
    private final TableCreationRequestValidator tableCreationRequestValidator;
    private final ListItemFactory listItemFactory;
    private final TableHeadFactory tableHeadFactory;
    private final ContentFactory contentFactory;
    private final TableRowFactory tableRowFactory;
    private final TableColumnFactory tableColumnFactory;
    private final ColumnDataServiceProvider columnDataServiceProvider;
    private final CommonListItemDao commonListItemDao;

    public List<TableFileUploadResponse> create(UUID userId, CreateTableRequest request) {
        tableCreationRequestValidator.validate(userId, request);

        ListItem listItem = listItemFactory.create(userId, request.getParent(), request.getTitle(), request.getListItemType());

        List<Content> contents = new ArrayList<>();
        List<TableFileUploadResponse> fileUploads = new ArrayList<>();
        List<TableHead> tableHeads = getTableHeads(listItem.getListItemId(), request.getTableHeads(), contents);
        List<TableRow> rows = createRows(listItem.getListItemId(), request.getRows(), contents, fileUploads);

        commonListItemDao.saveTable(listItem, tableHeads, rows, contents);

        return fileUploads;
    }

    private List<TableHead> getTableHeads(UUID listItemId, List<TableHeadModel> tableHeads, List<Content> contents) {
        return tableHeads.stream()
            .map(model -> {
                TableHead tableHead = tableHeadFactory.create(model.getColumnIndex());

                Content content = contentFactory.create(listItemId, tableHead.getTableHeadId(), model.getContent());
                contents.add(content);

                return tableHead;
            })
            .toList();
    }

    private List<TableRow> createRows(UUID listItemId, List<TableRowModel> rows, List<Content> contents, List<TableFileUploadResponse> fileUploads) {
        return rows.stream()
            .map(row -> tableRowFactory.create(
                listItemId,
                row.getRowIndex(),
                row.getChecked(),
                getColumns(listItemId, row.getRowIndex(), row.getColumns(), contents, fileUploads)
            ))
            .toList();
    }

    private List<TableColumn> getColumns(UUID listItemId, int rowIndex, List<TableColumnModel> columns, List<Content> contents, List<TableFileUploadResponse> fileUploads) {
        return columns.stream()
            .map(model -> {
                TableColumn column = tableColumnFactory.create(model.getColumnIndex(), model.getColumnType());

                columnDataServiceProvider.getForType(model.getColumnType())
                    .serialize(model.getData())
                    .ifPresent(data -> {
                        Content content = contentFactory.create(listItemId, column.getColumnId(), data.getEntity1());
                        contents.add(content);

                        data.getEntity2()
                            .ifPresent(storedFileId -> fileUploads.add(TableFileUploadResponse.builder()
                                .rowIndex(rowIndex)
                                .columnIndex(model.getColumnIndex())
                                .storedFileId(storedFileId)
                                .build()));
                    });

                return column;
            })
            .toList();
    }
}
