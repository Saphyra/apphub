package com.github.saphyra.apphub.service.notebook.service.table.column_saver;

import com.github.saphyra.apphub.api.notebook.model.table.ColumnType;
import com.github.saphyra.apphub.api.notebook.model.table.TableColumnModel;
import com.github.saphyra.apphub.api.notebook.model.table.TableFileUploadResponse;
import com.github.saphyra.apphub.service.notebook.dao.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.content.ContentDao;
import com.github.saphyra.apphub.service.notebook.dao.dimension.Dimension;
import com.github.saphyra.apphub.service.notebook.dao.dimension.DimensionDao;
import com.github.saphyra.apphub.service.notebook.dao.dimension.DimensionFactory;
import com.github.saphyra.apphub.service.notebook.service.ContentFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class TextColumnSaver implements ColumnSaver {
    private final DimensionFactory dimensionFactory;
    private final DimensionDao dimensionDao;
    private final ContentFactory contentFactory;
    private final ContentDao contentDao;

    @Override
    public boolean canProcess(ColumnType columnType) {
        return columnType == ColumnType.TEXT;
    }

    @Override
    public Optional<TableFileUploadResponse> save(UUID userId, UUID listItemId, Dimension row, TableColumnModel model) {
        Dimension column = dimensionFactory.create(userId, row.getDimensionId(), model.getColumnIndex());
        dimensionDao.save(column);

        Content content = contentFactory.create(listItemId, column.getDimensionId(), userId, model.getData().toString());
        contentDao.save(content);

        return Optional.empty();
    }
}
