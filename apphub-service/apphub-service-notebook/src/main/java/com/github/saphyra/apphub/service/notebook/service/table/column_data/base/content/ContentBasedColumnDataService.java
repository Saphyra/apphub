package com.github.saphyra.apphub.service.notebook.service.table.column_data.base.content;

import com.github.saphyra.apphub.api.notebook.model.table.ColumnType;
import com.github.saphyra.apphub.api.notebook.model.table.TableColumnModel;
import com.github.saphyra.apphub.api.notebook.model.table.TableFileUploadResponse;
import com.github.saphyra.apphub.service.notebook.dao.content.ContentDao;
import com.github.saphyra.apphub.service.notebook.dao.dimension.Dimension;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.service.table.column_data.base.ColumnDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
public abstract class ContentBasedColumnDataService implements ColumnDataService {
    protected final ColumnType columnType;
    protected final ContentDao contentDao;
    private final ContentBasedColumnProxy proxy;

    @Override
    public boolean canProcess(ColumnType columnType) {
        return columnType == this.columnType;
    }

    @Override
    public Optional<TableFileUploadResponse> save(UUID userId, UUID listItemId, UUID rowId, TableColumnModel model) {
        proxy.save(userId, listItemId, rowId, model.getColumnIndex(), stringifyContent(model.getData()), columnType);

        return Optional.empty();
    }

    protected String stringifyContent(Object data) {
        return data.toString();
    }

    @Override
    public Object getData(UUID columnId) {
        return contentDao.findByParentValidated(columnId)
            .getContent();
    }

    @Override
    public void delete(Dimension column) {
        proxy.delete(column);
    }

    @Override
    public Optional<TableFileUploadResponse> edit(ListItem listItem, UUID rowId, TableColumnModel model) {
        proxy.edit(model.getColumnId(), model.getColumnIndex(), stringifyContent(model.getData()));

        return Optional.empty();
    }

    @Override
    public void clone(ListItem clone, UUID rowId, Dimension originalColumn) {
        proxy.clone(clone.getUserId(), clone.getListItemId(), rowId, originalColumn.getDimensionId(), originalColumn.getIndex(), columnType);
    }
}
