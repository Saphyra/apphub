package com.github.saphyra.apphub.service.notebook.service.table.deprecated_column_data;

import com.github.saphyra.apphub.api.feature.notebook.model.table.ColumnType;
import com.github.saphyra.apphub.api.feature.notebook.model.table.TableColumnModel;
import com.github.saphyra.apphub.api.feature.notebook.model.table.TableFileUploadResponse;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_column_type.ColumnTypeDao;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_column_type.ColumnTypeDto;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_column_type.ColumnTypeFactory;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_dimension.Dimension;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_dimension.DimensionDao;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_dimension.DimensionFactory;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_list_item.DeprecatedListItem;
import com.github.saphyra.apphub.service.notebook.service.table.deprecated_column_data.base.DeprecatedColumnDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
@Deprecated(forRemoval = true)
class DeprecatedEmptyColumnDataService implements DeprecatedColumnDataService {
    private final DimensionFactory dimensionFactory;
    private final DimensionDao dimensionDao;
    private final ColumnTypeFactory columnTypeFactory;
    private final ColumnTypeDao columnTypeDao;

    @Override
    public boolean canProcess(ColumnType columnType) {
        return columnType == ColumnType.EMPTY;
    }

    @Override
    public Optional<TableFileUploadResponse> save(UUID userId, UUID listItemId, UUID rowId, TableColumnModel model) {
        Dimension column = dimensionFactory.create(userId, rowId, model.getColumnIndex());
        dimensionDao.save(column);

        ColumnTypeDto columnTypeDto = columnTypeFactory.create(column.getDimensionId(), userId, ColumnType.EMPTY);
        columnTypeDao.save(columnTypeDto);

        return Optional.empty();
    }

    @Override
    public Object getData(UUID columnId) {
        return null;
    }

    @Override
    public void delete(Dimension column) {
        columnTypeDao.deleteById(column.getDimensionId());
        dimensionDao.delete(column);
    }

    @Override
    public Optional<TableFileUploadResponse> edit(DeprecatedListItem listItem, UUID rowId, TableColumnModel columnModel) {
        Dimension column = dimensionDao.findByIdValidated(columnModel.getColumnId());
        column.setIndex(columnModel.getColumnIndex());
        dimensionDao.save(column);

        return Optional.empty();
    }

    @Override
    public void clone(DeprecatedListItem clone, UUID rowId, Dimension originalColumn) {
        Dimension clonedColumn = dimensionFactory.create(clone.getUserId(), rowId, originalColumn.getIndex());
        dimensionDao.save(clonedColumn);

        ColumnTypeDto columnTypeDto = columnTypeFactory.create(clonedColumn.getDimensionId(), clone.getUserId(), ColumnType.EMPTY);
        columnTypeDao.save(columnTypeDto);
    }

    @Override
    public void validateData(Object data) {

    }
}
