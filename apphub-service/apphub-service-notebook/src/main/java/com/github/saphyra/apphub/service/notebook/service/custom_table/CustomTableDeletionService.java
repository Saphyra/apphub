package com.github.saphyra.apphub.service.notebook.service.custom_table;

import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
import com.github.saphyra.apphub.service.notebook.dao.content.ContentDao;
import com.github.saphyra.apphub.service.notebook.dao.file.File;
import com.github.saphyra.apphub.service.notebook.dao.file.FileDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemType;
import com.github.saphyra.apphub.service.notebook.dao.table_head.TableHeadDao;
import com.github.saphyra.apphub.api.notebook.model.ColumnType;
import com.github.saphyra.apphub.service.notebook.dao.table.join.TableJoinDao;
import com.github.saphyra.apphub.service.notebook.dao.table.row.ChecklistTableRowDao;
import com.github.saphyra.apphub.service.notebook.service.StorageProxy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class CustomTableDeletionService {
    private final ListItemDao listItemDao;
    private final ContentDao contentDao;
    private final TableHeadDao tableHeadDao;
    private final ChecklistTableRowDao checklistTableRowDao;
    private final TableJoinDao tableJoinDao;
    private final FileDao fileDao;
    private final StorageProxy storageProxy;
    private final AccessTokenProvider accessTokenProvider;

    public void deleteForUser(UUID userId) {
        accessTokenProvider.set(AccessTokenHeader.builder().userId(userId).build());

        try {
            listItemDao.getByUserIdAndType(userId, ListItemType.CUSTOM_TABLE)
                .forEach(this::delete);
        } finally {
            accessTokenProvider.clear();
        }
    }

    public void delete(ListItem listItem) {
        tableJoinDao.getByParent(listItem.getListItemId())
            .stream()
            .filter(tableJoin -> tableJoin.getColumnType() == ColumnType.IMAGE || tableJoin.getColumnType() == ColumnType.FILE)
            .flatMap(tableJoin -> fileDao.findByParent(tableJoin.getTableJoinId()).stream())
            .map(File::getStoredFileId)
            .forEach(storageProxy::deleteFile);

        deleteWithoutFileDeletion(listItem);
    }

    public void deleteWithoutFileDeletion(ListItem listItem) {
        tableHeadDao.getByParent(listItem.getListItemId())
            .stream()
            .peek(tableHead -> contentDao.deleteByParent(tableHead.getTableHeadId()))
            .forEach(tableHeadDao::delete);

        checklistTableRowDao.deleteByParent(listItem.getListItemId());

        tableJoinDao.getByParent(listItem.getListItemId())
            .stream()
            .peek(tableJoin -> contentDao.deleteByParent(tableJoin.getTableJoinId()))
            .peek(tableJoin -> fileDao.deleteByParent(tableJoin.getTableJoinId()))
            .forEach(tableJoinDao::delete);

        listItemDao.delete(listItem);
    }
}
