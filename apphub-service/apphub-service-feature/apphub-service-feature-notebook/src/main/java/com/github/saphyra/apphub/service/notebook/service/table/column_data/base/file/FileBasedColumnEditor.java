package com.github.saphyra.apphub.service.notebook.service.table.column_data.base.file;

import com.github.saphyra.apphub.api.feature.notebook.model.request.FileMetadata;
import com.github.saphyra.apphub.api.feature.notebook.model.table.TableColumnModel;
import com.github.saphyra.apphub.api.feature.notebook.model.table.TableFileUploadResponse;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_dimension.Dimension;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_dimension.DimensionDao;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_list_item.DeprecatedListItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.Optional;
import java.util.UUID;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
class FileBasedColumnEditor {
    private final DimensionDao dimensionDao;
    private final ObjectMapper objectMapper;
    private final FileDeletionService fileDeletionService;
    private final FileSaver fileSaver;

    public Optional<TableFileUploadResponse> edit(DeprecatedListItem listItem, UUID rowId, TableColumnModel model) {
        Dimension column = dimensionDao.findByIdValidated(model.getColumnId());
        column.setIndex(model.getColumnIndex());
        dimensionDao.save(column);

        FileMetadata fileMetadata = objectMapper.convertValue(model.getData(), FileMetadata.class);
        if (isNull(fileMetadata.getStoredFileId())) {
            fileDeletionService.deleteFile(column.getDimensionId());
            return fileSaver.saveFile(listItem.getUserId(), rowId, model, column, fileMetadata);
        }

        return Optional.empty();
    }
}
