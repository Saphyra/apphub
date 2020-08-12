package com.github.saphyra.apphub.service.notebook.service.table.edition.table_head;

import com.github.saphyra.apphub.lib.common_domain.KeyValuePair;
import com.github.saphyra.apphub.service.notebook.dao.content.ContentDao;
import com.github.saphyra.apphub.service.notebook.dao.table.head.TableHeadDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
class EditTableTableHeadDeletionService {
    private final ContentDao contentDao;
    private final TableHeadDao tableHeadDao;

    void process(List<KeyValuePair<String>> columnNames, UUID listItemId) {
        List<UUID> actualKeys = columnNames.stream()
            .map(KeyValuePair::getKey)
            .filter(key -> !isNull(key))
            .collect(Collectors.toList());

        tableHeadDao.getByParent(listItemId)
            .stream()
            .filter(tableHead -> !actualKeys.contains(tableHead.getTableHeadId()))
            .forEach(tableHead -> {
                contentDao.deleteByParent(tableHead.getTableHeadId());
                tableHeadDao.delete(tableHead);
            });
    }
}
