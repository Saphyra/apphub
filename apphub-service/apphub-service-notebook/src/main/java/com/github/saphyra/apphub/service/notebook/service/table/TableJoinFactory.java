package com.github.saphyra.apphub.service.notebook.service.table;

import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.service.notebook.dao.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.table.join.TableJoin;
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
public class TableJoinFactory {
    private final IdGenerator idGenerator;
    private final ContentFactory contentFactory;

    public List<BiWrapper<TableJoin, Content>> create(UUID listItemId, List<List<String>> columns, UUID userId) {
        List<BiWrapper<TableJoin, Content>> result = new ArrayList<>();
        for (int rowIndex = 0; rowIndex < columns.size(); rowIndex++) {
            List<String> columnValues = columns.get(rowIndex);
            for (int columnIndex = 0; columnIndex < columnValues.size(); columnIndex++) {
                result.add(create(listItemId, columnValues.get(columnIndex), rowIndex, columnIndex, userId));
            }
        }

        return result;
    }

    public BiWrapper<TableJoin, Content> create(UUID listItemId, String columnContent, int rowIndex, int columnIndex, UUID userId) {
        TableJoin tableJoin = TableJoin.builder()
            .tableJoinId(idGenerator.randomUUID())
            .userId(userId)
            .parent(listItemId)
            .rowIndex(rowIndex)
            .columnIndex(columnIndex)
            .build();
        Content content = contentFactory.create(tableJoin.getTableJoinId(), userId, columnContent);
        return new BiWrapper<>(tableJoin, content);
    }
}
