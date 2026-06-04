package com.github.saphyra.apphub.service.notebook.service.table.edit;

import com.github.saphyra.apphub.api.feature.notebook.model.ItemType;
import com.github.saphyra.apphub.api.feature.notebook.model.table.TableColumnModel;
import com.github.saphyra.apphub.api.feature.notebook.model.table.TableFileUploadResponse;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.service.notebook.dao.list_item.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.list_item.content.ContentFactory;
import com.github.saphyra.apphub.service.notebook.dao.list_item.table.row.TableColumn;
import com.github.saphyra.apphub.service.notebook.dao.list_item.table.row.TableColumnFactory;
import com.github.saphyra.apphub.service.notebook.service.table.column_data.ColumnDataServiceProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class TableColumnAdditionService {
    private final TableColumnFactory tableColumnFactory;
    private final ColumnDataServiceProvider columnDataServiceProvider;
    private final ContentFactory contentFactory;

    boolean processTableColumnAddition(UUID listItemId, int rowIndex, List<TableColumnModel> models, List<TableColumn> columns, List<Content> contents, List<TableFileUploadResponse> fileUploads) {
        log.info("Processing TableColumn addition... Initial size: {}", columns.size());
        List<TableColumnModel> toAdd = models.stream()
            .filter(model -> model.getItemType() == ItemType.NEW)
            .toList();
        log.info("{} TableColumns are added.", toAdd.size());

        toAdd.forEach(model -> {
            TableColumn tableColumn = tableColumnFactory.create(model.getColumnIndex(), model.getColumnType());

            columnDataServiceProvider.getForType(model.getColumnType())
                .serialize(model.getData())
                .ifPresent(data -> {
                    Content content = contentFactory.create(
                        listItemId,
                        tableColumn.getColumnId(),
                        data.getEntity1()
                    );
                    contents.add(content);

                    data.getEntity2()
                        .ifPresent(storedFileId -> {
                            TableFileUploadResponse fileUpload = TableFileUploadResponse.builder()
                                .rowIndex(rowIndex)
                                .columnIndex(model.getColumnIndex())
                                .storedFileId(storedFileId)
                                .build();
                            fileUploads.add(fileUpload);
                        });
                });

            columns.add(tableColumn);
        });

        return !toAdd.isEmpty();
    }

    List<TableColumn> createColumns(UUID listItemId, List<TableColumnModel> models, int rowIndex, List<TableFileUploadResponse> fileUploads, List<Content> contents) {
        List<TableColumn> columns = new ArrayList<>();

        for (TableColumnModel model : models) {
            TableColumn tableColumn = tableColumnFactory.create(model.getColumnIndex(), model.getColumnType());

            Optional<BiWrapper<String, Optional<UUID>>> maybeData = columnDataServiceProvider.getForType(model.getColumnType())
                .serialize(model.getData());
            maybeData.ifPresent(data -> {
                Content content = contentFactory.create(
                    listItemId,
                    tableColumn.getColumnId(),
                    data.getEntity1()
                );
                contents.add(content);

                data.getEntity2()
                    .ifPresent(storedFileId -> {
                        TableFileUploadResponse fileUpload = TableFileUploadResponse.builder()
                            .rowIndex(rowIndex)
                            .columnIndex(model.getColumnIndex())
                            .storedFileId(storedFileId)
                            .build();
                        fileUploads.add(fileUpload);
                    });
            });

            columns.add(tableColumn);
        }

        return columns;
    }
}
