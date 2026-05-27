package com.github.saphyra.apphub.service.notebook.service.table;

import com.github.saphyra.apphub.lib.common_domain.QuadWrapper;
import com.github.saphyra.apphub.service.notebook.dao.list_item.CommonListItemDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.list_item.content.ContentDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.list_item.ListItemDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.table.head.TableHead;
import com.github.saphyra.apphub.service.notebook.dao.list_item.table.head.TableHeadDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.table.row.TableRow;
import com.github.saphyra.apphub.service.notebook.dao.list_item.table.row.TableRowDao;
import com.github.saphyra.apphub.service.notebook.service.table.column_data.ColumnDataServiceProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class TableDeletionService {
    private final ListItemDao listItemDao;
    private final CommonListItemDao commonListItemDao;
    private final TableHeadDao tableHeadDao;
    private final TableRowDao tableRowDao;
    private final ContentDao contentDao;
    private final ColumnDataServiceProvider columnDataServiceProvider;

    public void delete(ListItem listItem) {
        QuadWrapper<ListItem, List<TableHead>, List<TableRow>, List<Content>> table = commonListItemDao.findTableValidated(listItem.getUserId(), listItem.getListItemId());

        table.getEntity3()
            .stream()
            .flatMap(tableRow -> tableRow.getColumns().stream())
            .forEach(tableColumn -> table.getEntity4()
                .stream()
                .filter(content -> content.contains(tableColumn.getColumnId()))
                .findAny()
                .ifPresent(content -> columnDataServiceProvider.getForType(tableColumn.getType())
                    .deleteData(content.get(tableColumn.getColumnId()))
                )
            );

        listItemDao.delete(table.getEntity1());
        tableHeadDao.delete(listItem.getListItemId());
        tableRowDao.delete(listItem.getListItemId(), table.getEntity3());
        contentDao.delete(listItem.getListItemId(), table.getEntity4());
    }
}
