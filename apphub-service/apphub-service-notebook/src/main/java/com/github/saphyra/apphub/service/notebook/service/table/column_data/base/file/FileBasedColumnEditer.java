package com.github.saphyra.apphub.service.notebook.service.table.column_data.base.file;

import com.github.saphyra.apphub.api.notebook.model.request.FileMetadata;
import com.github.saphyra.apphub.api.notebook.model.table.TableColumnModel;
import com.github.saphyra.apphub.api.notebook.model.table.TableFileUploadResponse;
import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import com.github.saphyra.apphub.service.notebook.dao.dimension.Dimension;
import com.github.saphyra.apphub.service.notebook.dao.dimension.DimensionDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.service.FileDeletionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
class FileBasedColumnEditer {
    private final DimensionDao dimensionDao;
    private final ObjectMapperWrapper objectMapperWrapper;
    private final FileDeletionService fileDeletionService;
    private final FileSaver fileSaver;

    public Optional<TableFileUploadResponse> edit(ListItem listItem, UUID rowId, TableColumnModel model) {
        Dimension column = dimensionDao.findByIdValidated(model.getColumnId());
        column.setIndex(model.getColumnIndex());
        dimensionDao.save(column);

        FileMetadata fileMetadata = objectMapperWrapper.convertValue(model.getData(), FileMetadata.class);
        if (isNull(fileMetadata.getStoredFileId())) {
            fileDeletionService.deleteFile(column.getDimensionId());
            return fileSaver.saveFile(listItem.getUserId(), rowId, model, column, fileMetadata);
        }

        return Optional.empty();
    }
}
