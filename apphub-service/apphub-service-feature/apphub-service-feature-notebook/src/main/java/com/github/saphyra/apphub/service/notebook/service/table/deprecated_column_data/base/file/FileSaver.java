package com.github.saphyra.apphub.service.notebook.service.table.deprecated_column_data.base.file;

import com.github.saphyra.apphub.api.feature.notebook.model.request.FileMetadata;
import com.github.saphyra.apphub.api.feature.notebook.model.table.TableColumnModel;
import com.github.saphyra.apphub.api.feature.notebook.model.table.TableFileUploadResponse;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_dimension.Dimension;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_dimension.DimensionDao;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_file.File;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_file.FileDao;
import com.github.saphyra.apphub.service.notebook.service.FileFactory;
import com.github.saphyra.apphub.service.notebook.service.StorageProxy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
@Deprecated(forRemoval = true)
class FileSaver {
    private final DimensionDao dimensionDao;
    private final StorageProxy storageProxy;
    private final FileDao fileDao;
    private final FileFactory fileFactory;

    Optional<TableFileUploadResponse> saveFile(UUID userId, UUID rowId, TableColumnModel model, Dimension column, FileMetadata fileMetadata) {
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
}
