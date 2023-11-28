package com.github.saphyra.apphub.service.notebook.service.table.column_data;

import com.github.saphyra.apphub.api.notebook.model.request.FileMetadata;
import com.github.saphyra.apphub.api.notebook.model.table.ColumnType;
import com.github.saphyra.apphub.api.notebook.model.table.TableColumnModel;
import com.github.saphyra.apphub.api.notebook.model.table.TableFileUploadResponse;
import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.service.notebook.dao.column_type.ColumnTypeDao;
import com.github.saphyra.apphub.service.notebook.dao.column_type.ColumnTypeDto;
import com.github.saphyra.apphub.service.notebook.dao.column_type.ColumnTypeFactory;
import com.github.saphyra.apphub.service.notebook.dao.dimension.Dimension;
import com.github.saphyra.apphub.service.notebook.dao.dimension.DimensionDao;
import com.github.saphyra.apphub.service.notebook.dao.dimension.DimensionFactory;
import com.github.saphyra.apphub.service.notebook.dao.file.File;
import com.github.saphyra.apphub.service.notebook.dao.file.FileDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.service.FileFactory;
import com.github.saphyra.apphub.service.notebook.service.StorageProxy;
import com.github.saphyra.apphub.service.notebook.service.clone.FileCloneService;
import com.github.saphyra.apphub.service.notebook.service.validator.FileMetadataValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class FileColumnDataService implements ColumnDataService {
    private final DimensionFactory dimensionFactory;
    private final DimensionDao dimensionDao;
    private final ColumnTypeFactory columnTypeFactory;
    private final ColumnTypeDao columnTypeDao;
    private final ObjectMapperWrapper objectMapperWrapper;
    private final StorageProxy storageProxy;
    private final FileDao fileDao;
    private final FileFactory fileFactory;
    private final FileCloneService fileCloneService;
    private final FileMetadataValidator fileMetadataValidator;

    @Override
    public boolean canProcess(ColumnType columnType) {
        return columnType == ColumnType.FILE;
    }

    @Override
    public Optional<TableFileUploadResponse> save(UUID userId, UUID listItemId, UUID rowId, TableColumnModel model) {
        Dimension column = dimensionFactory.create(userId, rowId, model.getColumnIndex());
        dimensionDao.save(column);

        ColumnTypeDto columnTypeDto = columnTypeFactory.create(column.getDimensionId(), userId, ColumnType.FILE);
        columnTypeDao.save(columnTypeDto);

        FileMetadata fileMetadata = objectMapperWrapper.convertValue(model.getData(), FileMetadata.class);
        return saveFile(userId, rowId, model, column, fileMetadata);
    }

    @Override
    public Object getData(UUID columnId) {
        UUID storedFileId = fileDao.findByParentValidated(columnId)
            .getStoredFileId();
        return FileMetadata.builder()
            .storedFileId(storedFileId)
            .build();
    }

    @Override
    public void delete(Dimension column) {
        deleteFile(column.getDimensionId());
        columnTypeDao.deleteById(column.getDimensionId());
        dimensionDao.delete(column);
    }

    @Override
    public Optional<TableFileUploadResponse> edit(ListItem listItem, UUID rowId, TableColumnModel model) {
        Dimension column = dimensionDao.findByIdValidated(model.getColumnId());
        column.setIndex(model.getColumnIndex());
        dimensionDao.save(column);

        FileMetadata fileMetadata = objectMapperWrapper.convertValue(model.getData(), FileMetadata.class);
        if (isNull(fileMetadata.getStoredFileId())) {
            deleteFile(column.getDimensionId());
            return saveFile(listItem.getUserId(), rowId, model, column, fileMetadata);
        }

        return Optional.empty();
    }

    @Override
    public void clone(ListItem clone, UUID rowId, Dimension originalColumn) {
        Dimension clonedColumn = dimensionFactory.create(clone.getUserId(), rowId, originalColumn.getIndex());
        dimensionDao.save(clonedColumn);

        ColumnTypeDto columnTypeDto = columnTypeFactory.create(clonedColumn.getDimensionId(), clone.getUserId(), ColumnType.TEXT);
        columnTypeDao.save(columnTypeDto);

        File toClone = fileDao.findByParentValidated(originalColumn.getDimensionId());
        fileCloneService.cloneFile(clone.getUserId(), clonedColumn.getDimensionId(), toClone);
    }

    @Override
    public void validateData(Object data) {
        if (isNull(data)) {
            return;
        }
        FileMetadata request = ValidationUtil.parse(data, (d) -> objectMapperWrapper.convertValue(d, FileMetadata.class), "fileMetadata");

        fileMetadataValidator.validate(request);
    }

    private Optional<TableFileUploadResponse> saveFile(UUID userId, UUID rowId, TableColumnModel model, Dimension column, FileMetadata fileMetadata) {
        UUID fileId = storageProxy.createFile(fileMetadata.getFileName(), fileMetadata.getSize());

        File file = fileFactory.create(userId, column.getDimensionId(), fileId);
        fileDao.save(file);

        TableFileUploadResponse response = TableFileUploadResponse.builder()
            .rowIndex(dimensionDao.findByIdValidated(rowId).getIndex())
            .columnIndex(model.getColumnIndex())
            .storedFileId(fileId)
            .build();

        return Optional.of(response);
    }

    private void deleteFile(UUID columnId) {
        File file = fileDao.findByParentValidated(columnId);
        storageProxy.deleteFile(file.getStoredFileId());
        fileDao.delete(file);
    }
}
