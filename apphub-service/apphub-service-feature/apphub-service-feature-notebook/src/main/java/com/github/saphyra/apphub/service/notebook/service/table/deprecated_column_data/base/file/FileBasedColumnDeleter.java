package com.github.saphyra.apphub.service.notebook.service.table.deprecated_column_data.base.file;

import com.github.saphyra.apphub.service.notebook.dao.deprecated_column_type.ColumnTypeDao;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_dimension.Dimension;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_dimension.DimensionDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
@Deprecated(forRemoval = true)
class FileBasedColumnDeleter {
    //private final FileDeletionService fileDeletionService;
    private final DimensionDao dimensionDao;
    private final ColumnTypeDao columnTypeDao;

    void delete(Dimension column) {
        //fileDeletionService.deleteFile(column.getDimensionId());
        columnTypeDao.deleteById(column.getDimensionId());
        dimensionDao.delete(column);
    }
}
