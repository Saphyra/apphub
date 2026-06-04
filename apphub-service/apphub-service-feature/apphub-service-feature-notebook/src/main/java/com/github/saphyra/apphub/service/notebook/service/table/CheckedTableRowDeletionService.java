package com.github.saphyra.apphub.service.notebook.service.table;

import com.github.saphyra.apphub.lib.common_domain.QuadWrapper;
import com.github.saphyra.apphub.service.notebook.dao.list_item.CommonListItemDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.list_item.content.ContentDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.table.head.TableHead;
import com.github.saphyra.apphub.service.notebook.dao.list_item.table.row.TableRow;
import com.github.saphyra.apphub.service.notebook.dao.list_item.table.row.TableRowDao;
import com.github.saphyra.apphub.service.notebook.service.table.column_data.ColumnDataServiceProvider;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.apache.commons.lang3.BooleanUtils.isTrue;

@Component
@RequiredArgsConstructor
@Slf4j
public class CheckedTableRowDeletionService {
    private final ColumnDataServiceProvider columnDataServiceProvider;
    private final ContentDao contentDao;
    private final CommonListItemDao commonListItemDao;
    private final TableRowDao tableRowDao;

    @Transactional
    public void deleteCheckedRows(UUID userId, UUID listItemId) {
        QuadWrapper<ListItem, List<TableHead>, List<TableRow>, List<Content>> table = commonListItemDao.findTableValidated(userId, listItemId);

        List<TableRow> toDelete = table.getEntity3()
            .stream()
            .filter(tableRow -> isTrue(tableRow.getChecked()))
            .toList();

        List<Content> contents = new ArrayList<>(table.getEntity4());

        toDelete.stream()
            .flatMap(tableRow -> tableRow.getColumns().stream())
            .forEach(column -> findContent(column.getColumnId(), table.getEntity4())
                .ifPresent(content -> {
                    columnDataServiceProvider.getForType(column.getType())
                        .deleteData(content.get(column.getColumnId()));

                    content.remove(column.getColumnId());
                }));

        contentDao.save(listItemId, contents);
        tableRowDao.delete(listItemId, toDelete);
    }

    private Optional<Content> findContent(UUID key, List<Content> contents) {
        return contents.stream()
            .filter(content -> content.contains(key))
            .findAny();
    }
}
