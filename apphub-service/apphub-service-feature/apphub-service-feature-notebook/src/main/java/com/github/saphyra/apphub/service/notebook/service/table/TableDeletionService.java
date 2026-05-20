package com.github.saphyra.apphub.service.notebook.service.table;

import com.github.saphyra.apphub.lib.common_domain.QuadWrapper;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.service.notebook.dao.list_item.Content;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.TableColumn;
import com.github.saphyra.apphub.service.notebook.dao.list_item.TableHead;
import com.github.saphyra.apphub.service.notebook.dao.list_item.TableRow;
import com.github.saphyra.apphub.service.notebook.service.StorageProxy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class TableDeletionService {
    private final ListItemDao listItemDao;
    private final UuidConverter uuidConverter;
    private final StorageProxy storageProxy;

    public void delete(ListItem listItem) {
        QuadWrapper<ListItem, List<TableHead>, List<TableRow>, List<Content>> table = listItemDao.findTableValidated(listItem.getUserId(), listItem.getListItemId());

        table.getEntity3()
            .stream()
            .flatMap(tableRow -> tableRow.getColumns().stream())
            .filter(tableColumn -> tableColumn.getType().isFile())
            .map(TableColumn::getColumnId)
            .flatMap(columnId -> {
                    String key = uuidConverter.convertDomain(columnId);
                    return table.getEntity4()
                        .stream()
                        .filter(content -> content.contains(key))
                        .map(content -> content.get(key));
                })
            .map(uuidConverter::convertEntity)
            .forEach(storageProxy::deleteFile);

        listItemDao.delete(table.getEntity1());
        listItemDao.deleteTableHeads(listItem.getUserId(), listItem.getListItemId(), table.getEntity2());
        listItemDao.deleteTableRows(listItem.getUserId(), listItem.getListItemId(), table.getEntity3());
        listItemDao.deleteContents(listItem.getUserId(), listItem.getListItemId(), table.getEntity4());
    }
}
