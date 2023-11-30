package com.github.saphyra.apphub.service.notebook.service.table.column_data.util;

import com.github.saphyra.apphub.api.notebook.model.table.ColumnType;
import com.github.saphyra.apphub.service.notebook.dao.column_type.ColumnTypeDao;
import com.github.saphyra.apphub.service.notebook.dao.column_type.ColumnTypeDto;
import com.github.saphyra.apphub.service.notebook.dao.column_type.ColumnTypeFactory;
import com.github.saphyra.apphub.service.notebook.dao.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.content.ContentDao;
import com.github.saphyra.apphub.service.notebook.dao.dimension.Dimension;
import com.github.saphyra.apphub.service.notebook.dao.dimension.DimensionDao;
import com.github.saphyra.apphub.service.notebook.dao.dimension.DimensionFactory;
import com.github.saphyra.apphub.service.notebook.service.ContentFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class ContentBasedColumnTypeCloner {
    private final DimensionFactory dimensionFactory;
    private final ContentFactory contentFactory;
    private final ColumnTypeFactory columnTypeFactory;
    private final DimensionDao dimensionDao;
    private final ContentDao contentDao;
    private final ColumnTypeDao columnTypeDao;

    void clone(UUID userId, UUID clonedListItemId, UUID rowId, UUID originalColumnId, Integer columnIndex, ColumnType columnType) {
        Dimension clonedColumn = dimensionFactory.create(userId, rowId, columnIndex);
        dimensionDao.save(clonedColumn);

        ColumnTypeDto columnTypeDto = columnTypeFactory.create(clonedColumn.getDimensionId(), userId, columnType);
        columnTypeDao.save(columnTypeDto);

        Content originalContent = contentDao.findByParentValidated(originalColumnId);
        Content clonedContent = contentFactory.create(clonedListItemId, clonedColumn.getDimensionId(), userId, originalContent.getContent());
        contentDao.save(clonedContent);
    }
}
