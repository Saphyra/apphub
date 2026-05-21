package com.github.saphyra.apphub.service.notebook.service.table.deprecated_column_data.base.content;

import com.github.saphyra.apphub.api.feature.notebook.model.table.ColumnType;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_column_type.ColumnTypeDao;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_column_type.ColumnTypeDto;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_column_type.ColumnTypeFactory;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_content.Content;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_content.ContentDao;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_dimension.Dimension;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_dimension.DimensionDao;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_dimension.DimensionFactory;
import com.github.saphyra.apphub.service.notebook.service.DeprecatedContentFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
@Deprecated(forRemoval = true)
class ContentBasedColumnSaver {
    private final DimensionFactory dimensionFactory;
    private final DeprecatedContentFactory contentFactory;
    private final ColumnTypeFactory columnTypeFactory;
    private final DimensionDao dimensionDao;
    private final ContentDao contentDao;
    private final ColumnTypeDao columnTypeDao;

    void save(UUID userId, UUID listItemId, UUID rowId, Integer columnIndex, String contentValue, ColumnType columnType) {
        Dimension column = dimensionFactory.create(userId, rowId, columnIndex);
        dimensionDao.save(column);

        Content content = contentFactory.create(listItemId, column.getDimensionId(), userId, contentValue);
        contentDao.save(content);

        ColumnTypeDto columnTypeDto = columnTypeFactory.create(column.getDimensionId(), userId, columnType);
        columnTypeDao.save(columnTypeDto);
    }
}
