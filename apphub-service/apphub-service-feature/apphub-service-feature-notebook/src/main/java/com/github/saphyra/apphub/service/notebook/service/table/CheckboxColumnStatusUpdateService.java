package com.github.saphyra.apphub.service.notebook.service.table;

import com.github.saphyra.apphub.api.feature.notebook.model.table.ColumnType;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.notebook.dao.list_item.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.list_item.content.ContentDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.content.ContentFactory;
import com.github.saphyra.apphub.service.notebook.dao.list_item.table.row.TableColumn;
import com.github.saphyra.apphub.service.notebook.dao.list_item.table.row.TableRow;
import com.github.saphyra.apphub.service.notebook.dao.list_item.table.row.TableRowDao;
import com.github.saphyra.apphub.service.notebook.service.table.column_data.ColumnDataServiceProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class CheckboxColumnStatusUpdateService {
    private final ColumnDataServiceProvider columnDataServiceProvider;
    private final ContentFactory contentFactory;
    private final ContentDao contentDao;
    private final TableRowDao tableRowDao;

    public void updateColumnStatus(UUID listItemId, UUID rowId, UUID columnId, Boolean status) {
        ValidationUtil.notNull(status, "status");

        TableRow row = tableRowDao.findByIdValidated(listItemId, rowId);

        TableColumn column = row.getColumns()
            .stream()
            .filter(tableColumn -> tableColumn.getColumnId().equals(columnId))
            .findAny()
            .orElseThrow(() -> ExceptionFactory.notFound("TableColumn not found by id " + columnId + " in TableRow " + rowId + " in ListItem " + listItemId));

        if (column.getType() != ColumnType.CHECKBOX) {
            throw ExceptionFactory.invalidParam("columnId", "not a " + ColumnType.CHECKBOX);
        }

        String data = columnDataServiceProvider.getForType(ColumnType.CHECKBOX)
            .serialize(status)
            .orElseThrow()
            .getEntity1();
        List<Content> contents = new ArrayList<>(contentDao.getByListItemId(listItemId));
        Content content = contents.stream()
            .filter(c -> c.contains(columnId))
            .findAny()
            .orElseThrow(() -> ExceptionFactory.notFound("Content not found for columnId " + columnId + " in ListItem " + listItemId));
        content.remove(columnId);

        Content newContent = contentFactory.create(listItemId, columnId, data);
        contents.add(newContent);

        contentDao.save(listItemId, contents);
    }
}
