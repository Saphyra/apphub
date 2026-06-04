package com.github.saphyra.apphub.service.notebook.service.table.edit;

import com.github.saphyra.apphub.api.feature.notebook.model.table.TableHeadModel;
import com.github.saphyra.apphub.service.notebook.dao.list_item.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.list_item.table.head.TableHead;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
class TableHeadDeletionService {
    boolean processTableHeadDeletion(List<TableHeadModel> models, List<TableHead> tableHeads, List<Content> contents) {
        log.info("Processing TableHead deletion... Original count: {}", tableHeads.size());
        List<UUID> toKeepIds = models.stream()
            .map(TableHeadModel::getTableHeadId)
            .filter(Objects::nonNull)
            .toList();
        List<TableHead> deleted = tableHeads.stream()
            .filter(tableHead -> !toKeepIds.contains(tableHead.getTableHeadId()))
            .toList();
        log.info("Deleting {} tableHeads...", deleted.size());
        deleted.forEach(tableHead -> {
            tableHeads.remove(tableHead);
            contents.forEach(content -> content.remove(tableHead.getTableHeadId()));
        });
        return !deleted.isEmpty();
    }
}
