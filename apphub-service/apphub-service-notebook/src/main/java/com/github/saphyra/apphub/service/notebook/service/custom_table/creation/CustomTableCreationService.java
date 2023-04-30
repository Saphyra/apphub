package com.github.saphyra.apphub.service.notebook.service.custom_table.creation;

import com.github.saphyra.apphub.api.notebook.model.request.CustomTableColumnRequest;
import com.github.saphyra.apphub.api.notebook.model.request.CustomTableRequest;
import com.github.saphyra.apphub.api.notebook.model.request.CustomTableRowRequest;
import com.github.saphyra.apphub.api.notebook.model.request.FileMetadata;
import com.github.saphyra.apphub.api.notebook.model.response.CustomTableCreatedResponse;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.notebook.dao.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.content.ContentDao;
import com.github.saphyra.apphub.service.notebook.dao.file.File;
import com.github.saphyra.apphub.service.notebook.dao.file.FileDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemType;
import com.github.saphyra.apphub.service.notebook.dao.table.head.TableHead;
import com.github.saphyra.apphub.service.notebook.dao.table.head.TableHeadDao;
import com.github.saphyra.apphub.service.notebook.dao.table.join.ColumnType;
import com.github.saphyra.apphub.service.notebook.dao.table.join.TableJoin;
import com.github.saphyra.apphub.service.notebook.dao.table.join.TableJoinDao;
import com.github.saphyra.apphub.service.notebook.dao.table.row.ChecklistTableRow;
import com.github.saphyra.apphub.service.notebook.dao.table.row.ChecklistTableRowDao;
import com.github.saphyra.apphub.service.notebook.service.ContentFactory;
import com.github.saphyra.apphub.service.notebook.service.FileFactory;
import com.github.saphyra.apphub.service.notebook.service.ListItemFactory;
import com.github.saphyra.apphub.service.notebook.service.StorageProxy;
import com.github.saphyra.apphub.service.notebook.service.checklist_table.ChecklistTableRowFactory;
import com.github.saphyra.apphub.service.notebook.service.table.TableHeadFactory;
import com.github.saphyra.apphub.service.notebook.service.table.TableJoinFactory;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO split
//TODO unit test
public class CustomTableCreationService {
    private final CustomTableRequestValidator customTableRequestValidator;
    private final ObjectMapperWrapper objectMapperWrapper;
    private final StorageProxy storageProxy;

    private final ListItemFactory listItemFactory;
    private final TableHeadFactory tableHeadFactory;
    private final TableJoinFactory tableJoinFactory;
    private final ChecklistTableRowFactory checklistTableRowFactory;
    private final ContentFactory contentFactory;
    private final FileFactory fileFactory;

    private final ListItemDao listItemDao;
    private final ContentDao contentDao;
    private final TableHeadDao tableHeadDao;
    private final ChecklistTableRowDao checklistTableRowDao;
    private final TableJoinDao tableJoinDao;
    private final FileDao fileDao;

    @Transactional
    public List<CustomTableCreatedResponse> create(UUID userId, CustomTableRequest request) {
        customTableRequestValidator.validate(request);

        List<CustomTableCreatedResponse> result = new ArrayList<>();

        ListItem listItem = listItemFactory.create(userId, request.getTitle(), request.getParent(), ListItemType.CUSTOM_TABLE);
        List<BiWrapper<TableHead, Content>> tableHeads = tableHeadFactory.create(listItem.getListItemId(), request.getColumnNames(), userId);

        request.getRows()
            .forEach(row -> createRow(userId, result, listItem, row));

        listItemDao.save(listItem);
        tableHeads.forEach(biWrapper -> {
            tableHeadDao.save(biWrapper.getEntity1());
            contentDao.save(biWrapper.getEntity2());
        });

        return result;
    }

    private void createRow(UUID userId, List<CustomTableCreatedResponse> result, ListItem listItem, CustomTableRowRequest row) {
        ChecklistTableRow checklistTableRow = checklistTableRowFactory.create(userId, listItem.getListItemId(), row.getRowIndex(), row.getChecked());
        checklistTableRowDao.save(checklistTableRow);

        row.getColumns()
            .forEach(column -> createColumn(userId, result, listItem, row, column));
    }

    private void createColumn(UUID userId, List<CustomTableCreatedResponse> result, ListItem listItem, CustomTableRowRequest row, CustomTableColumnRequest column) {
        ColumnType columnType = ColumnType.valueOf(column.getType());

        TableJoin tableJoin = tableJoinFactory.create(listItem.getListItemId(), row.getRowIndex(), column.getColumnIndex(), userId, columnType);
        tableJoinDao.save(tableJoin);

        switch (columnType) {
            case NUMBER, TEXT, CHECKBOX, COLOR, DATE, TIME, DATE_TIME, MONTH, RANGE, LINK -> {
                String contentValue = objectMapperWrapper.writeValueAsString(column.getValue());
                Content content = contentFactory.create(listItem.getListItemId(), tableJoin.getTableJoinId(), userId, contentValue);
                contentDao.save(content);
            }
            case IMAGE, FILE -> {
                FileMetadata createFileRequest = objectMapperWrapper.convertValue(column.getValue(), FileMetadata.class);
                UUID storedFileId;
                if (isNull(createFileRequest.getStoredFileId())) {
                    storedFileId = storageProxy.createFile(createFileRequest.getFileName(), createFileRequest.getSize());

                    CustomTableCreatedResponse response = CustomTableCreatedResponse.builder()
                        .rowIndex(row.getRowIndex())
                        .columnIndex(column.getColumnIndex())
                        .storedFileId(storedFileId)
                        .build();
                    result.add(response);
                } else {
                    storedFileId = createFileRequest.getStoredFileId();
                }
                File file = fileFactory.create(userId, tableJoin.getTableJoinId(), storedFileId);
                fileDao.save(file);
            }
            case EMPTY -> log.debug("No validation required");
            default -> throw ExceptionFactory.notLoggedException(HttpStatus.NOT_IMPLEMENTED, ErrorCode.GENERAL_ERROR, "Unhandled columnType " + columnType);
        }
    }
}
