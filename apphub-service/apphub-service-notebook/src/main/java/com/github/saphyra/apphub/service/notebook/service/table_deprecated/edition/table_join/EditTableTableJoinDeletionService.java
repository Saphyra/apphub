package com.github.saphyra.apphub.service.notebook.service.table_deprecated.edition.table_join;

import com.github.saphyra.apphub.api.notebook.model.request.EditTableJoinRequest;
import com.github.saphyra.apphub.service.notebook.dao.content.ContentDao;
import com.github.saphyra.apphub.service.notebook.dao.table.join.TableJoin;
import com.github.saphyra.apphub.service.notebook.dao.table.join.TableJoinDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class EditTableTableJoinDeletionService {
    private final ContentDao contentDao;
    private final TableJoinDao tableJoinDao;

    public void process(List<EditTableJoinRequest> incomingColumns, UUID listItemId) {
        List<UUID> incomingTableJoinIds = incomingColumns.stream()
            .map(EditTableJoinRequest::getTableJoinId)
            .toList();

        tableJoinDao.getByParent(listItemId)
            .stream()
            .map(TableJoin::getTableJoinId)
            .filter(tableJoinId -> !incomingTableJoinIds.contains(tableJoinId))
            .forEach(tableJoinId -> {
                tableJoinDao.deleteById(tableJoinId);
                contentDao.deleteByParent(tableJoinId);
            });
    }
}
