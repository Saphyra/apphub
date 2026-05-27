package com.github.saphyra.apphub.service.notebook.service.table.edit;

import com.github.saphyra.apphub.api.feature.notebook.model.ItemType;
import com.github.saphyra.apphub.api.feature.notebook.model.table.TableHeadModel;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.notebook.dao.list_item.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.list_item.content.ContentFactory;
import com.github.saphyra.apphub.service.notebook.dao.list_item.table.head.TableHead;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
class TableHeadModificationService {
    private final ContentFactory contentFactory;

    boolean processTableHeadModification(UUID listItemId, List<TableHeadModel> models, List<TableHead> tableHeads, List<Content> contents) {
        log.info("Processing TableHead modifications...");
        List<TableHeadModel> existing = models.stream()
            .filter(tableHeadModel -> tableHeadModel.getType() == ItemType.EXISTING)
            .toList();

        boolean modified = false;
        for (TableHeadModel model : existing) {
            TableHead tableHead = tableHeads.stream()
                .filter(th -> th.getTableHeadId().equals(model.getTableHeadId()))
                .findAny()
                .orElseThrow(() -> ExceptionFactory.notFound("TableHead not found by id " + model.getTableHeadId()));
            Content content = getContent(contents, model.getTableHeadId())
                .orElseThrow(() -> ExceptionFactory.notFound("Content not found for TableHead with id " + model.getTableHeadId()));
            if (tableHead.getIndex() != model.getColumnIndex()) {
                log.info("Updating columnIndex of TableHead {}", tableHead.getTableHeadId());
                tableHead.setIndex(model.getColumnIndex());

                modified = true;
            }
            if (!content.get(model.getTableHeadId()).equals(model.getContent())) {
                log.info("Modifying content of TableHead {}", tableHead.getTableHeadId());
                content.remove(model.getTableHeadId());

                Content newContent = contentFactory.create(listItemId, model.getTableHeadId(), model.getContent());
                contents.add(newContent);

                modified = true;
            }
        }
        return modified;
    }

    private static Optional<Content> getContent(List<Content> contents, UUID key) {
        return contents.stream()
            .filter(c -> c.getContent().containsKey(key))
            .findAny();
    }
}
