package com.github.saphyra.apphub.service.notebook.service.table.column_data;

import com.github.saphyra.apphub.api.notebook.model.table.ColumnType;
import com.github.saphyra.apphub.api.notebook.model.table.TableColumnModel;
import com.github.saphyra.apphub.api.notebook.model.table.TableFileUploadResponse;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class ColorColumnDataService implements ColumnDataService {
    private final DimensionFactory dimensionFactory;
    private final DimensionDao dimensionDao;
    private final ContentFactory contentFactory;
    private final ContentDao contentDao;
    private final ColumnTypeFactory columnTypeFactory;
    private final ColumnTypeDao columnTypeDao;

    @Override
    public boolean canProcess(ColumnType columnType) {
        return columnType == ColumnType.COLOR;
    }

    @Override
    public Optional<TableFileUploadResponse> save(UUID userId, UUID listItemId, UUID rowId, TableColumnModel model) {
        Dimension column = dimensionFactory.create(userId, rowId, model.getColumnIndex());
        dimensionDao.save(column);

        Content content = contentFactory.create(listItemId, column.getDimensionId(), userId, model.getData().toString());
        contentDao.save(content);

        ColumnTypeDto columnTypeDto = columnTypeFactory.create(column.getDimensionId(), userId, ColumnType.COLOR);
        columnTypeDao.save(columnTypeDto);

        return Optional.empty();
    }

    @Override
    public Object getData(UUID columnId) {
        return contentDao.findByParentValidated(columnId)
            .getContent();
    }

    @Override
    public void delete(Dimension column) {
        contentDao.deleteByParent(column.getDimensionId());
        columnTypeDao.deleteById(column.getDimensionId());
        dimensionDao.delete(column);
    }

    @Override
    public Optional<TableFileUploadResponse> edit(ListItem listItem, UUID rowId, TableColumnModel model) {
        Dimension column = dimensionDao.findByIdValidated(model.getColumnId());
        column.setIndex(model.getColumnIndex());
        dimensionDao.save(column);

        Content content = contentDao.findByParentValidated(column.getDimensionId());
        content.setContent(model.getData().toString());
        contentDao.save(content);

        return Optional.empty();
    }

    @Override
    public void clone(ListItem clone, UUID rowId, Dimension originalColumn) {
        Dimension clonedColumn = dimensionFactory.create(clone.getUserId(), rowId, originalColumn.getIndex());
        dimensionDao.save(clonedColumn);

        ColumnTypeDto columnTypeDto = columnTypeFactory.create(clonedColumn.getDimensionId(), clone.getUserId(), ColumnType.COLOR);
        columnTypeDao.save(columnTypeDto);

        Content originalContent = contentDao.findByParentValidated(originalColumn.getDimensionId());
        Content clonedContent = contentFactory.create(clone.getListItemId(), clonedColumn.getDimensionId(), clone.getUserId(), originalContent.getContent());
        contentDao.save(clonedContent);
    }

    @Override
    public void validateData(Object data) {
        String hexString = data.toString();
        ValidationUtil.length(hexString, 7, "colorValue");
        if (hexString.charAt(0) != '#') {
            throw ExceptionFactory.invalidParam("colorValue", "first character is not #");
        }

        ValidationUtil.parse(hexString, v -> Integer.parseInt(v.toString().substring(1), 16), "colorValue");
    }
}