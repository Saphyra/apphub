package com.github.saphyra.apphub.service.notebook.service.table_deprecated.edition.table_head;

import com.github.saphyra.apphub.api.notebook.model.request.EditTableHeadRequest;
import com.github.saphyra.apphub.service.notebook.dao.content.ContentDao;
import com.github.saphyra.apphub.service.notebook.dao.table_head.TableHead;
import com.github.saphyra.apphub.service.notebook.dao.table_head.TableHeadDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class EditTableTableHeadDeletionService {
    private final ContentDao contentDao;
    private final TableHeadDao tableHeadDao;

    void process(List<EditTableHeadRequest> tableHeads, UUID listItemId) {
        List<UUID> incomingTableHeadIds = tableHeads.stream()
            .map(EditTableHeadRequest::getTableHeadId)
            .toList();

        tableHeadDao.getByParent(listItemId)
            .stream()
            .map(TableHead::getTableHeadId)
            .filter(key -> !incomingTableHeadIds.contains(key))
            .forEach(tableHeadId -> {
                contentDao.deleteByParent(tableHeadId);
                tableHeadDao.deleteById(tableHeadId);
            });
    }
}
