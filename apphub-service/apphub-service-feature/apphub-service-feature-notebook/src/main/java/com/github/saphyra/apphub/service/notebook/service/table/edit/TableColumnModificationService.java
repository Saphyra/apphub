package com.github.saphyra.apphub.service.notebook.service.table.edit;

import com.github.saphyra.apphub.api.feature.notebook.model.ItemType;
import com.github.saphyra.apphub.api.feature.notebook.model.table.ColumnType;
import com.github.saphyra.apphub.api.feature.notebook.model.table.TableColumnModel;
import com.github.saphyra.apphub.api.feature.notebook.model.table.TableFileUploadResponse;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.notebook.dao.list_item.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.list_item.content.ContentFactory;
import com.github.saphyra.apphub.service.notebook.dao.list_item.table.row.TableColumn;
import com.github.saphyra.apphub.service.notebook.service.table.column_data.ColumnDataServiceProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class TableColumnModificationService {
    private final ColumnDataServiceProvider columnDataServiceProvider;
    private final ContentFactory contentFactory;

    boolean processTableColumnModification(
        UUID listItemId,
        int rowIndex,
        List<TableColumnModel> models,
        List<TableColumn> columns,
        List<Content> contents,
        List<TableFileUploadResponse> fileUploads
    ) {
        log.info("Processing TableColumn modification...");
        boolean modified = false;

        for (TableColumnModel model : models) {
            if (model.getItemType() == ItemType.EXISTING) {
                log.info("Modifying TableColumn {}", model.getColumnId());

                TableColumn column = columns.stream()
                    .filter(tableColumn -> tableColumn.getColumnId().equals(model.getColumnId()))
                    .findAny()
                    .orElseThrow(() -> ExceptionFactory.notFound("TableColumn not found by id " + model.getColumnId()));
                if (column.getIndex() != model.getColumnIndex()) {
                    log.info("Updating index of TableColumn {}", column.getColumnId());
                    column.setIndex(model.getColumnIndex());

                    modified = true;
                }

                Optional<BiWrapper<String, Optional<UUID>>> maybeData = columnDataServiceProvider.getForType(model.getColumnType())
                    .serialize(model.getData());
                if (model.getColumnType() == column.getType()) {
                    //ColumnType did not change
                    if (handleUnchangedColumnType(listItemId, rowIndex, contents, fileUploads, model, maybeData, column)) {
                        modified = true;
                    }
                } else { //Column type changed
                    handleColumnTypeChanged(listItemId, rowIndex, contents, fileUploads, model, column, maybeData);

                    modified = true;
                }
            }
        }

        return modified;
    }

    private void handleColumnTypeChanged(
        UUID listItemId,
        int rowIndex,
        List<Content> contents,
        List<TableFileUploadResponse> fileUploads,
        TableColumnModel model,
        TableColumn column,
        Optional<BiWrapper<String, Optional<UUID>>> maybeData
    ) {
        boolean modified;
        //Delete existing data if present
        getContent(contents, column.getColumnId())
            .ifPresent(content -> {
                columnDataServiceProvider.getForType(column.getType())
                    .deleteData(content.get(column.getColumnId()));
                content.remove(column.getColumnId());
            });

        //Save new data
        maybeData.ifPresent(newData -> {
            Content content = contentFactory.create(listItemId, column.getColumnId(), newData.getEntity1());
            contents.add(content);
            newData.getEntity2()
                .ifPresent(storedFileId -> {
                    TableFileUploadResponse fileUpload = TableFileUploadResponse.builder()
                        .rowIndex(rowIndex)
                        .columnIndex(column.getIndex())
                        .storedFileId(storedFileId)
                        .build();
                    fileUploads.add(fileUpload);
                });
        });
        column.setType(model.getColumnType());
    }

    private boolean handleUnchangedColumnType(
        UUID listItemId,
        int rowIndex,
        List<Content> contents,
        List<TableFileUploadResponse> fileUploads,
        TableColumnModel model,
        Optional<BiWrapper<String, Optional<UUID>>> maybeData,
        TableColumn column
    ) {
        if (model.getColumnType() == ColumnType.EMPTY) {
            log.debug("Column type remained the same, without content");
            return false;
        } else {
            //Same ColumnType with different value
            BiWrapper<String, Optional<UUID>> newData = maybeData.orElseThrow();
            Content existingContent = getContentValidated(contents, column.getColumnId());
            String existingData = existingContent.get(column.getColumnId());

            if (!newData.getEntity1().equals(existingData)) {
                Content newContent = contentFactory.create(listItemId, column.getColumnId(), newData.getEntity1());
                contents.add(newContent);

                //Handle file replacement
                if (column.getType().isFile()) {
                    columnDataServiceProvider.getForType(column.getType())
                        .deleteData(existingData);

                    TableFileUploadResponse fileUpload = TableFileUploadResponse.builder()
                        .rowIndex(rowIndex)
                        .columnIndex(column.getIndex())
                        .storedFileId(newData.getEntity2().orElseThrow())
                        .build();
                    fileUploads.add(fileUpload);
                }

                existingContent.remove(column.getColumnId());
            }

            return true;
        }
    }

    private Content getContentValidated(List<Content> contents, UUID key) {
        return getContent(contents, key)
            .orElseThrow(() -> ExceptionFactory.notFound("Content not found for key " + key));
    }

    private static Optional<Content> getContent(List<Content> contents, UUID key) {
        return contents.stream()
            .filter(c -> c.getContent().containsKey(key))
            .findAny();
    }
}
