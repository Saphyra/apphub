package com.github.saphyra.apphub.service.notebook.service.table.column_data;

import com.github.saphyra.apphub.api.notebook.model.table.ColumnType;
import com.github.saphyra.apphub.api.notebook.model.table.TableColumnModel;
import com.github.saphyra.apphub.api.notebook.model.table.TableFileUploadResponse;
import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.service.notebook.dao.column_type.ColumnTypeDao;
import com.github.saphyra.apphub.service.notebook.dao.column_type.ColumnTypeDto;
import com.github.saphyra.apphub.service.notebook.dao.column_type.ColumnTypeFactory;
import com.github.saphyra.apphub.service.notebook.dao.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.content.ContentDao;
import com.github.saphyra.apphub.service.notebook.dao.dimension.Dimension;
import com.github.saphyra.apphub.service.notebook.dao.dimension.DimensionDao;
import com.github.saphyra.apphub.service.notebook.dao.dimension.DimensionFactory;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.service.ContentFactory;
import com.github.saphyra.apphub.service.notebook.service.table.dto.Range;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class RangeColumnDataService implements ColumnDataService {
    private final DimensionFactory dimensionFactory;
    private final ContentFactory contentFactory;
    private final ColumnTypeFactory columnTypeFactory;
    private final DimensionDao dimensionDao;
    private final ContentDao contentDao;
    private final ColumnTypeDao columnTypeDao;
    private final ObjectMapperWrapper objectMapperWrapper;

    @Override
    public boolean canProcess(ColumnType columnType) {
        return columnType == ColumnType.RANGE;
    }

    @Override
    public Optional<TableFileUploadResponse> save(UUID userId, UUID listItemId, UUID rowId, TableColumnModel model) {
        Dimension column = dimensionFactory.create(userId, rowId, model.getColumnIndex());
        dimensionDao.save(column);

        Content content = contentFactory.create(listItemId, column.getDimensionId(), userId, objectMapperWrapper.writeValueAsString(model.getData()));
        contentDao.save(content);

        ColumnTypeDto columnTypeDto = columnTypeFactory.create(column.getDimensionId(), userId, ColumnType.RANGE);
        columnTypeDao.save(columnTypeDto);

        return Optional.empty();
    }

    @Override
    public Object getData(UUID columnId) {
        String rawContent = contentDao.findByParentValidated(columnId)
            .getContent();
        return objectMapperWrapper.readValue(rawContent, Object.class);
    }

    @Override
    public void delete(Dimension column) {
        contentDao.deleteByParent(column.getDimensionId());
        columnTypeDao.deleteById(column.getDimensionId());
        dimensionDao.delete(column);
    }

    @Override
    public Optional<TableFileUploadResponse> edit(ListItem listItem, UUID rowId, TableColumnModel columnModel) {
        Dimension column = dimensionDao.findByIdValidated(columnModel.getColumnId());
        column.setIndex(columnModel.getColumnIndex());
        dimensionDao.save(column);

        Content content = contentDao.findByParentValidated(column.getDimensionId());
        content.setContent(objectMapperWrapper.writeValueAsString(columnModel.getData()));
        contentDao.save(content);

        return Optional.empty();
    }

    @Override
    public void clone(ListItem clone, UUID rowId, Dimension originalColumn) {
        Dimension clonedColumn = dimensionFactory.create(clone.getUserId(), rowId, originalColumn.getIndex());
        dimensionDao.save(clonedColumn);

        ColumnTypeDto columnTypeDto = columnTypeFactory.create(clonedColumn.getDimensionId(), clone.getUserId(), ColumnType.RANGE);
        columnTypeDao.save(columnTypeDto);

        Content originalContent = contentDao.findByParentValidated(originalColumn.getDimensionId());
        Content clonedContent = contentFactory.create(clone.getListItemId(), clonedColumn.getDimensionId(), clone.getUserId(), originalContent.getContent());
        contentDao.save(clonedContent);
    }

    @Override
    public void validateData(Object data) {
        Range range = ValidationUtil.parse(data, (d) -> objectMapperWrapper.convertValue(d, Range.class), "rangeData");
        ValidationUtil.atLeastExclusive(range.getStep(), 0, "range.step");
        ValidationUtil.notNull(range.getMin(), "range.min");
        ValidationUtil.atLeast(range.getMax(), range.getMin(), "range.max");
        ValidationUtil.betweenInclusive(range.getValue(), range.getMin(), range.getMax(), "range.value");
    }
}
