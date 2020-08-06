package com.github.saphyra.apphub.service.notebook.service.table.creation;

import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.service.notebook.dao.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.table.head.TableHead;
import com.github.saphyra.apphub.service.notebook.service.ContentFactory;
import com.github.saphyra.util.IdGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
//TODO unit test
class TableHeadFactory {
    private final ContentFactory contentFactory;
    private final IdGenerator idGenerator;

    List<BiWrapper<TableHead, Content>> create(ListItem listItem, List<String> columnNames, UUID userId) {
        List<BiWrapper<TableHead, Content>> result = new ArrayList<>();
        for (int columnIndex = 0; columnIndex < columnNames.size(); columnIndex++) {
            result.add(create(listItem.getListItemId(), columnNames.get(columnIndex), columnIndex, userId));
        }
        return result;
    }

    private BiWrapper<TableHead, Content> create(UUID listItemId, String columnName, int columnIndex, UUID userId) {
        TableHead tableHead = TableHead.builder()
            .tableHeadId(idGenerator.randomUUID())
            .userId(userId)
            .parent(listItemId)
            .columnIndex(columnIndex)
            .build();
        Content content = contentFactory.create(tableHead.getTableHeadId(), userId, columnName);
        return new BiWrapper<>(tableHead, content);
    }

}
