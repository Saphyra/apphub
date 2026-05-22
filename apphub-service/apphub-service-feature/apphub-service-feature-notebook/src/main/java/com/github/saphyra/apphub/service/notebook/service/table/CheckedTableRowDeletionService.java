package com.github.saphyra.apphub.service.notebook.service.table;

import com.github.saphyra.apphub.lib.common_domain.QuadWrapper;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.service.notebook.dao.list_item.CommonListItemDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.list_item.content.ContentDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.table.head.TableHead;
import com.github.saphyra.apphub.service.notebook.dao.list_item.table.row.TableRow;
import com.github.saphyra.apphub.service.notebook.dao.list_item.table.row.TableRowDao;
import com.github.saphyra.apphub.service.notebook.service.StorageProxy;
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
//TODO unit test
public class CheckedTableRowDeletionService {
    private final UuidConverter uuidConverter;
    private final ColumnDataServiceProvider columnDataServiceProvider;
    private final StorageProxy storageProxy;
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
        List<UUID> deletedFiles = new ArrayList<>();

        toDelete.stream()
            .flatMap(tableRow -> tableRow.getColumns().stream())
            .forEach(column -> {
                String key = uuidConverter.convertDomain(column.getColumnId());
                findContent(key, table.getEntity4())
                    .ifPresent(content -> {
                        String data = content.get(key);
                        if (column.getType().isFile()) {
                            UUID storedFileId = columnDataServiceProvider.getForType(column.getType())
                                .deserialize(data, UUID.class);
                            deletedFiles.add(storedFileId);
                        }

                        content.remove(key);
                    });
            });

        deletedFiles.forEach(storageProxy::deleteFile);
        contentDao.save(userId, listItemId, contents);
        tableRowDao.delete(userId, listItemId, toDelete);
    }

    private Optional<Content> findContent(String key, List<Content> contents) {
        return contents.stream()
            .filter(content -> content.contains(key))
            .findAny();
    }
}
