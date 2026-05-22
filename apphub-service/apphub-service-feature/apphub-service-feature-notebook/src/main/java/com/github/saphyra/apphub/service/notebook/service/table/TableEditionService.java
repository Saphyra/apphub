package com.github.saphyra.apphub.service.notebook.service.table;

import com.github.saphyra.apphub.api.feature.notebook.model.ItemType;
import com.github.saphyra.apphub.api.feature.notebook.model.table.ColumnType;
import com.github.saphyra.apphub.api.feature.notebook.model.table.EditTableRequest;
import com.github.saphyra.apphub.api.feature.notebook.model.table.TableColumnModel;
import com.github.saphyra.apphub.api.feature.notebook.model.table.TableFileUploadResponse;
import com.github.saphyra.apphub.api.feature.notebook.model.table.TableHeadModel;
import com.github.saphyra.apphub.api.feature.notebook.model.table.TableRowModel;
import com.github.saphyra.apphub.lib.common_domain.QuadWrapper;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.notebook.dao.list_item.CommonListItemDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.list_item.content.ContentDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.content.ContentFactory;
import com.github.saphyra.apphub.service.notebook.dao.list_item.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.list_item.ListItemDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.table.head.TableHead;
import com.github.saphyra.apphub.service.notebook.dao.list_item.table.head.TableHeadDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.table.head.TableHeadFactory;
import com.github.saphyra.apphub.service.notebook.dao.list_item.table.row.TableColumn;
import com.github.saphyra.apphub.service.notebook.dao.list_item.table.row.TableColumnFactory;
import com.github.saphyra.apphub.service.notebook.dao.list_item.table.row.TableRow;
import com.github.saphyra.apphub.service.notebook.dao.list_item.table.row.TableRowDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.table.row.TableRowFactory;
import com.github.saphyra.apphub.service.notebook.service.StorageProxy;
import com.github.saphyra.apphub.service.notebook.service.table.column_data.ColumnDataServiceProvider;
import com.github.saphyra.apphub.service.notebook.service.table.validator.EditTableRequestValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
//TODO split
public class TableEditionService {
    private final EditTableRequestValidator editTableRequestValidator;
    private final ListItemDao listItemDao;
    private final UuidConverter uuidConverter;
    private final TableHeadFactory tableHeadFactory;
    private final ContentFactory contentFactory;
    private final ColumnDataServiceProvider columnDataServiceProvider;
    private final TableRowFactory tableRowFactory;
    private final TableColumnFactory tableColumnFactory;
    private final StorageProxy storageProxy;
    private final ContentDao contentDao;
    private final CommonListItemDao commonListItemDao;
    private final TableHeadDao tableHeadDao;
    private final TableRowDao tableRowDao;

    public List<TableFileUploadResponse> editTable(UUID userId, UUID listItemId, EditTableRequest request) {
        editTableRequestValidator.validate(userId, listItemId, request);

        QuadWrapper<ListItem, List<TableHead>, List<TableRow>, List<Content>> table = commonListItemDao.findTableValidated(userId, listItemId);
        ListItem listItem = table.getEntity1();
        List<TableHead> tableHeads = new ArrayList<>(table.getEntity2());
        List<TableRow> tableRows = new ArrayList<>(table.getEntity3());
        List<Content> contents = new ArrayList<>(table.getEntity4());

        processListItem(request, listItem);
        processTableHeads(listItemId, request.getTableHeads(), tableHeads, contents);
        List<TableFileUploadResponse> fileUploads = processTableRows(listItemId, request.getRows(), tableRows, contents);
        contentDao.save(listItemId, contents);

        return fileUploads;
    }

    private List<TableFileUploadResponse> processTableRows(UUID listItemId, List<TableRowModel> models, List<TableRow> tableRows, List<Content> contents) {
        log.info("Processing TableRows...");
        List<UUID> deletedFiles = new ArrayList<>();
        List<TableFileUploadResponse> fileUploads = new ArrayList<>();

        processTableRowDeletion(listItemId, models, tableRows, deletedFiles, contents);
        processTableRowAddition(listItemId, models, tableRows, fileUploads, contents);
        processTableRowModification(listItemId, models, tableRows, fileUploads, deletedFiles, contents);

        log.info("{} files were deleted.", deletedFiles.size());
        deletedFiles.forEach(storageProxy::deleteFile);
        log.info("{} files have to be uploaded.", fileUploads.size());

        return fileUploads;
    }

    private void processTableRowModification(UUID listItemId, List<TableRowModel> models, List<TableRow> tableRows, List<TableFileUploadResponse> fileUploads, List<UUID> deletedFiles, List<Content> contents) {
        log.info("Processing TableRowModification...");
        List<TableRowModel> existing = models.stream()
            .filter(model -> model.getItemType() == ItemType.EXISTING)
            .toList();

        for (TableRowModel model : existing) {
            TableRow tableRow = tableRows.stream()
                .filter(tr -> tr.getTableRowId().equals(model.getRowId()))
                .findAny()
                .orElseThrow(() -> ExceptionFactory.notFound("TableRow not found by id " + model.getRowId()));

            boolean rowModified = false;
            if (tableRow.getChecked() != model.getChecked()) {
                log.info("Updating checked status of TableRow {}", tableRow.getTableRowId());
                tableRow.setChecked(model.getChecked());
                rowModified = true;
            }

            if (tableRow.getIndex() != model.getRowIndex()) {
                log.info("Updating index of TableRow {}", tableRow.getTableRowId());
                tableRow.setIndex(model.getRowIndex());
                rowModified = true;
            }

            ArrayList<TableColumn> columns = new ArrayList<>(tableRow.getColumns());
            boolean columnsModified = processTableColumnModification(listItemId, model.getColumns(), columns, contents, deletedFiles, fileUploads, tableRow.getIndex());
            tableRow.setColumns(columns);

            if (rowModified || columnsModified) {
                log.info("Saving modified TableRow {}", tableRow.getTableRowId());
                tableRowDao.save(tableRow);
            }
        }
    }

    private boolean processTableColumnModification(
        UUID listItemId,
        List<TableColumnModel> models,
        List<TableColumn> columns,
        List<Content> contents,
        List<UUID> deletedFiles,
        List<TableFileUploadResponse> fileUploads,
        int rowIndex
    ) {
        log.info("Processing TableColumnModification...");
        boolean columnDeleted = processTableColumnDeletion(models, columns, contents, deletedFiles);
        boolean columnAdded = processTableColumnAddition(listItemId, models, columns, contents, fileUploads);
        boolean columnModified = processTableColumnEdition(listItemId, rowIndex, models, columns, contents, fileUploads, deletedFiles);

        return columnDeleted || columnAdded || columnModified;
    }

    private boolean processTableColumnEdition(
        UUID listItemId,
        int rowIndex,
        List<TableColumnModel> models,
        List<TableColumn> columns,
        List<Content> contents,
        List<TableFileUploadResponse> fileUploads,
        List<UUID> deletedFiles
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
                String key = uuidConverter.convertDomain(column.getColumnId());

                if (column.getIndex() != model.getColumnIndex()) {
                    log.info("Updating index of TableColumn {}", column.getColumnId());
                    column.setIndex(model.getColumnIndex());

                    modified = true;
                }

                Optional<String> maybeData = columnDataServiceProvider.getForType(model.getColumnType()).serialize(model.getData());
                //ColumnType did not change
                if (model.getColumnType() == column.getType()) {
                    //Column must have data
                    if (model.getColumnType() == ColumnType.EMPTY) {
                        //Column type remained the same, without content
                    } else {
                        String newData = maybeData.orElseThrow();
                        Content existingContent = getContentValidated(contents, key);
                        String existingData = existingContent.get(key);

                        //Same ColumnType with different value
                        if (!newData.equals(existingData)) {
                            Content newContent = contentFactory.create(listItemId, column.getColumnId(), newData);
                            contents.add(newContent);

                            //Handle file replacement
                            if (column.getType().isFile()) {
                                UUID existingStoredFileId = columnDataServiceProvider.getForType(column.getType()).deserialize(existingData, UUID.class);
                                deletedFiles.add(existingStoredFileId);

                                UUID newStoredFileId = columnDataServiceProvider.getForType(column.getType()).deserialize(newData, UUID.class);
                                TableFileUploadResponse fileUpload = TableFileUploadResponse.builder()
                                    .rowIndex(rowIndex)
                                    .columnIndex(column.getIndex())
                                    .storedFileId(newStoredFileId)
                                    .build();
                                fileUploads.add(fileUpload);
                            }

                            existingContent.remove(key);
                        }
                    }
                } else { //Column type changed
                    //Delete existing data if present
                    getContent(contents, key).ifPresent(content -> {
                        if (column.getType().isFile()) {
                            UUID existingStoredFileId = columnDataServiceProvider.getForType(column.getType()).deserialize(content.get(key), UUID.class);
                            deletedFiles.add(existingStoredFileId);
                        }
                        content.remove(key);
                    });

                    if (model.getColumnType() == ColumnType.EMPTY) {
                        //No save needed
                    } else {
                        Content content = contentFactory.create(listItemId, column.getColumnId(), maybeData.orElseThrow());
                        contents.add(content);

                        if (column.getType().isFile()) {
                            UUID storedFileId = columnDataServiceProvider.getForType(column.getType()).deserialize(content.get(key), UUID.class);
                            TableFileUploadResponse fileUpload = TableFileUploadResponse.builder()
                                .rowIndex(rowIndex)
                                .columnIndex(column.getIndex())
                                .storedFileId(storedFileId)
                                .build();
                            fileUploads.add(fileUpload);
                        }
                    }

                    column.setType(model.getColumnType());

                    modified = true;
                }
            }
        }

        return modified;
    }

    private boolean processTableColumnAddition(UUID listItemId, List<TableColumnModel> models, List<TableColumn> columns, List<Content> contents, List<TableFileUploadResponse> fileUploads) {
        log.info("Processing TableColumn addition... Initial size: {}", columns.size());
        List<TableColumnModel> toAdd = models.stream()
            .filter(model -> model.getItemType() == ItemType.NEW)
            .toList();
        log.info("{} TableColumns are added.", toAdd.size());

        toAdd.forEach(model -> {
            TableColumn tableColumn = tableColumnFactory.create(model.getColumnIndex(), model.getColumnType());

            Optional<String> maybeData = columnDataServiceProvider.getForType(model.getColumnType()).serialize(model.getData());
            maybeData.ifPresent(data -> {
                Content content = contentFactory.create(
                    listItemId,
                    tableColumn.getColumnId(),
                    data
                );
                contents.add(content);
            });

            if (model.getColumnType().isFile()) {
                String data = maybeData.orElseThrow();
                UUID storedFileId = columnDataServiceProvider.getForType(model.getColumnType())
                    .deserialize(data, UUID.class);
                TableFileUploadResponse fileUpload = TableFileUploadResponse.builder()
                    .columnIndex(model.getColumnIndex())
                    .storedFileId(storedFileId)
                    .build();
                fileUploads.add(fileUpload);
            }

            columns.add(tableColumn);
        });

        return !toAdd.isEmpty();
    }

    private boolean processTableColumnDeletion(List<TableColumnModel> models, List<TableColumn> columns, List<Content> contents, List<UUID> deletedFiles) {
        log.info("Processing TableColumn deletion... Original size: {}", columns.size());
        List<UUID> toKeepIds = models.stream()
            .map(TableColumnModel::getColumnId)
            .filter(Objects::nonNull)
            .toList();

        List<TableColumn> toDelete = columns.stream()
            .filter(tableColumn -> !toKeepIds.contains(tableColumn.getColumnId()))
            .toList();
        log.info("{} TableColumns are deleted.", toDelete.size());

        toDelete.forEach(tableColumn -> {
            String key = uuidConverter.convertDomain(tableColumn.getColumnId());

            if (tableColumn.getType().isFile()) {
                Content content = getContent(contents, key)
                    .orElseThrow(() -> ExceptionFactory.notFound("Content not found for TableColumn with id " + tableColumn.getColumnId()));

                UUID storedFileId = columnDataServiceProvider.getForType(tableColumn.getType()).deserialize(content.get(key), UUID.class);
                deletedFiles.add(storedFileId);
            }

            contents.forEach(content -> content.remove(key));
            columns.remove(tableColumn);
        });

        return !toDelete.isEmpty();
    }

    private void processTableRowAddition(UUID listItemId, List<TableRowModel> models, List<TableRow> tableRows, List<TableFileUploadResponse> fileUploads, List<Content> contents) {
        log.info("Processing TableRowAddition. Initial size: {}", tableRows.size());
        models.stream()
            .filter(model -> model.getItemType() == ItemType.NEW)
            .map(model -> tableRowFactory.create(
                listItemId,
                model.getRowIndex(),
                model.getChecked(),
                createColumns(listItemId, model.getColumns(), model.getRowIndex(), fileUploads, contents)
            ))
            .forEach(tableRow -> {
                tableRows.add(tableRow);
                tableRowDao.save(tableRow);
            });
    }

    private List<TableColumn> createColumns(UUID listItemId, List<TableColumnModel> models, int rowIndex, List<TableFileUploadResponse> fileUploads, List<Content> contents) {
        List<TableColumn> columns = new ArrayList<>();

        for (TableColumnModel model : models) {
            TableColumn tableColumn = tableColumnFactory.create(model.getColumnIndex(), model.getColumnType());

            Optional<String> maybeData = columnDataServiceProvider.getForType(model.getColumnType()).serialize(model.getData());
            maybeData.ifPresent(data -> {
                Content content = contentFactory.create(
                    listItemId,
                    tableColumn.getColumnId(),
                    data
                );
                contents.add(content);
            });

            if (model.getColumnType().isFile()) {
                String data = maybeData.orElseThrow();
                UUID storedFileId = columnDataServiceProvider.getForType(model.getColumnType())
                    .deserialize(data, UUID.class);
                TableFileUploadResponse fileUpload = TableFileUploadResponse.builder()
                    .rowIndex(rowIndex)
                    .columnIndex(model.getColumnIndex())
                    .storedFileId(storedFileId)
                    .build();
                fileUploads.add(fileUpload);
            }

            columns.add(tableColumn);
        }

        return columns;
    }

    private void processTableRowDeletion(UUID listItemId, List<TableRowModel> models, List<TableRow> tableRows, List<UUID> deletedFiles, List<Content> contents) {
        log.info("Processing Table Row Deletion. Original size: {}", tableRows.size());
        List<UUID> toKeepIds = models.stream()
            .map(TableRowModel::getRowId)
            .filter(Objects::nonNull)
            .toList();
        List<TableRow> deleted = tableRows.stream()
            .filter(tableRow -> !toKeepIds.contains(tableRow.getTableRowId()))
            .toList();
        log.info("{} TableRows are deleted.", deleted.size());

        deleted.forEach(tableRow -> {
            tableRow.getColumns()
                .forEach(tableColumn -> {
                    String key = uuidConverter.convertDomain(tableColumn.getColumnId());
                    Optional<Content> maybeContent = getContent(contents, key);
                    if (tableColumn.getType().isFile()) {
                        Content content = maybeContent.orElseThrow(() -> ExceptionFactory.notFound("Content not found for TableColumn with id " + tableColumn.getColumnId()));
                        UUID storedFileId = columnDataServiceProvider.getForType(tableColumn.getType()).deserialize(content.get(key), UUID.class);
                        deletedFiles.add(storedFileId);
                    }
                    maybeContent.ifPresent(content -> content.remove(key));
                });

            tableRows.remove(tableRow);
            tableRowDao.delete(listItemId, tableRow.getTableRowId());
        });
    }

    private void processTableHeads(UUID listItemId, List<TableHeadModel> models, List<TableHead> tableHeads, List<Content> contents) {
        log.info("Processing TableHead modifications...");
        boolean tableHeadsDeleted = processTableHeadDeletion(models, tableHeads, contents);
        boolean tableHeadsAdded = processTableHeadAddition(listItemId, models, tableHeads, contents);
        boolean tableHeadsModified = processTableHeadModification(listItemId, models, tableHeads, contents);

        if (tableHeadsDeleted || tableHeadsAdded || tableHeadsModified) {
            log.info("Saving modified TableHeads...");
            tableHeadDao.save(listItemId, tableHeads);
        }
    }

    private boolean processTableHeadModification(UUID listItemId, List<TableHeadModel> models, List<TableHead> tableHeads, List<Content> contents) {
        log.info("Processing TableHead modifications...");
        List<TableHeadModel> existing = models.stream()
            .filter(tableHeadModel -> tableHeadModel.getType() == ItemType.EXISTING)
            .toList();

        boolean modified = false;
        for (TableHeadModel model : existing) {
            TableHead tableHead = tableHeads.stream()
                .filter(th -> th.getTableHeadId().equals(model.getTableHeadId()))
                .findAny()
                .orElseThrow(() -> ExceptionFactory.notFound("TableHead not found by id " + model.getTableHeadId()));
            String key = uuidConverter.convertDomain(model.getTableHeadId());
            Content content = getContent(contents, key)
                .orElseThrow(() -> ExceptionFactory.notFound("Content not found for TableHead with id " + model.getTableHeadId()));
            if (tableHead.getIndex() != model.getColumnIndex()) {
                log.info("Updating columnIndex of TableHead {}", tableHead.getTableHeadId());
                tableHead.setIndex(model.getColumnIndex());

                modified = true;
            }
            if (!content.get(key).equals(model.getContent())) {
                log.info("Modifying content of TableHead {}", tableHead.getTableHeadId());
                content.remove(key);

                Content newContent = contentFactory.create(listItemId, model.getTableHeadId(), model.getContent());
                contents.add(newContent);

                modified = true;
            }
        }
        return modified;
    }

    private boolean processTableHeadAddition(UUID listItemId, List<TableHeadModel> models, List<TableHead> tableHeads, List<Content> contents) {
        log.info("Processing TableHead addition... Initial count: {}", tableHeads.size());
        List<TableHeadModel> toAdd = models.stream()
            .filter(tableHeadModel -> tableHeadModel.getType() == ItemType.NEW)
            .toList();
        log.info("Adding {} TableHeads...", toAdd.size());

        toAdd.forEach(tableHeadModel -> {
            TableHead tableHead = tableHeadFactory.create(tableHeadModel.getColumnIndex());
            tableHeads.add(tableHead);

            Content content = contentFactory.create(listItemId, tableHead.getTableHeadId(), tableHeadModel.getContent());
            contents.add(content);
        });

        return !toAdd.isEmpty();
    }

    private boolean processTableHeadDeletion(List<TableHeadModel> models, List<TableHead> tableHeads, List<Content> contents) {
        log.info("Processing TableHead deletion... Original count: {}", tableHeads.size());
        List<UUID> toKeepIds = models.stream()
            .map(TableHeadModel::getTableHeadId)
            .filter(Objects::nonNull)
            .toList();
        List<TableHead> deleted = tableHeads.stream()
            .filter(tableHead -> !toKeepIds.contains(tableHead.getTableHeadId()))
            .toList();
        log.info("Deleting {} tableHeads...", deleted.size());
        deleted.forEach(tableHead -> {
            tableHeads.remove(tableHead);
            String key = uuidConverter.convertDomain(tableHead.getTableHeadId());
            contents.forEach(content -> content.remove(key));
        });
        return !deleted.isEmpty();
    }

    private void processListItem(EditTableRequest request, ListItem listItem) {
        if (!listItem.getTitle().equals(request.getTitle())) {
            log.info("Updating ListITem title...");
            listItem.setTitle(request.getTitle());
            listItemDao.save(listItem);
        }
    }

    private Content getContentValidated(List<Content> contents, String key) {
        return getContent(contents, key)
            .orElseThrow(() -> ExceptionFactory.notFound("Content not found for key " + key));
    }

    private static Optional<Content> getContent(List<Content> contents, String key) {
        return contents.stream()
            .filter(c -> c.getContent().containsKey(key))
            .findAny();
    }
}
