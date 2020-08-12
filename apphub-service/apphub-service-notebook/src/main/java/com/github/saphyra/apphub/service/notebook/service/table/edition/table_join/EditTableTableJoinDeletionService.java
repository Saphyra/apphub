package com.github.saphyra.apphub.service.notebook.service.table.edition.table_join;

import com.github.saphyra.apphub.lib.common_domain.KeyValuePair;
import com.github.saphyra.apphub.service.notebook.dao.content.ContentDao;
import com.github.saphyra.apphub.service.notebook.dao.table.join.TableJoinDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
class EditTableTableJoinDeletionService {
    private final ContentDao contentDao;
    private final TableJoinDao tableJoinDao;

    public void process(List<List<KeyValuePair<String>>> columns, UUID listItemId) {
        List<UUID> columnIds = columns.stream()
            .flatMap(Collection::stream)
            .map(KeyValuePair::getKey)
            .filter(id -> !isNull(id))
            .collect(Collectors.toList());

        tableJoinDao.getByParent(listItemId)
            .stream()
            .filter(tableJoin -> !columnIds.contains(tableJoin.getTableJoinId()))
            .forEach(tableJoin -> {
                contentDao.deleteByParent(tableJoin.getTableJoinId());
                tableJoinDao.delete(tableJoin);
            });
    }
}
