package com.github.saphyra.apphub.service.notebook.service.table.edit;

import com.github.saphyra.apphub.api.feature.notebook.model.ItemType;
import com.github.saphyra.apphub.api.feature.notebook.model.table.TableHeadModel;
import com.github.saphyra.apphub.service.notebook.dao.list_item.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.list_item.content.ContentFactory;
import com.github.saphyra.apphub.service.notebook.dao.list_item.table.head.TableHead;
import com.github.saphyra.apphub.service.notebook.dao.list_item.table.head.TableHeadFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class TableHeadAdditionService {
    private final TableHeadFactory tableHeadFactory;
    private final ContentFactory contentFactory;

    boolean processTableHeadAddition(UUID listItemId, List<TableHeadModel> models, List<TableHead> tableHeads, List<Content> contents) {
        log.info("Processing TableHead addition... Initial count: {}", tableHeads.size());
        List<TableHeadModel> toAdd = models.stream()
            .filter(tableHeadModel -> tableHeadModel.getType() == ItemType.NEW)
            .toList();
        log.info("Adding {} TableHeads...", toAdd.size());

        toAdd.forEach(tableHeadModel -> {
            TableHead tableHead = tableHeadFactory.create(tableHeadModel.getColumnIndex());
            tableHeads.add(tableHead);

            Content content = contentFactory.create(listItemId, tableHead.getTableHeadId(), tableHeadModel.getContent());
            contents.add(content);
        });

        return !toAdd.isEmpty();
    }
}
