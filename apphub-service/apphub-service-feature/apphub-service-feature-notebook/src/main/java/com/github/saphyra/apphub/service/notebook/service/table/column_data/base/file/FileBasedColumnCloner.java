package com.github.saphyra.apphub.service.notebook.service.table.column_data.base.file;

import com.github.saphyra.apphub.api.feature.notebook.model.table.ColumnType;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_column_type.ColumnTypeDao;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_column_type.ColumnTypeDto;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_column_type.ColumnTypeFactory;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_dimension.Dimension;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_dimension.DimensionDao;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_dimension.DimensionFactory;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_file.File;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_file.FileDao;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_list_item.DeprecatedListItem;
import com.github.saphyra.apphub.service.notebook.service.clone.FileCloneService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class FileBasedColumnCloner {
    private final DimensionFactory dimensionFactory;
    private final DimensionDao dimensionDao;
    private final ColumnTypeFactory columnTypeFactory;
    private final ColumnTypeDao columnTypeDao;
    private final FileDao fileDao;
    private final FileCloneService fileCloneService;

    void clone(DeprecatedListItem clone, UUID rowId, Dimension originalColumn, ColumnType columnType) {
        Dimension clonedColumn = dimensionFactory.create(clone.getUserId(), rowId, originalColumn.getIndex());
        dimensionDao.save(clonedColumn);

        ColumnTypeDto columnTypeDto = columnTypeFactory.create(clonedColumn.getDimensionId(), clone.getUserId(), columnType);
        columnTypeDao.save(columnTypeDto);

        File toClone = fileDao.findByParentValidated(originalColumn.getDimensionId());
        fileCloneService.cloneFile(clone.getUserId(), clonedColumn.getDimensionId(), toClone);
    }
}
