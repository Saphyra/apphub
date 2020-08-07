package com.github.saphyra.apphub.service.notebook.service.table.edition.table_join;

import com.github.saphyra.apphub.lib.common_domain.KeyValuePair;
import com.github.saphyra.apphub.service.notebook.dao.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.content.ContentDao;
import com.github.saphyra.apphub.service.notebook.dao.table.join.TableJoin;
import com.github.saphyra.apphub.service.notebook.dao.table.join.TableJoinDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class EditTableTableJoinEditionService {
    private final ContentDao contentDao;
    private final TableJoinDao tableJoinDao;

    void process(List<List<KeyValuePair<String>>> columns, UUID listItemId) {
        Map<UUID, TableJoin> tableJoins = tableJoinDao.getByParent(listItemId)
            .stream()
            .collect(Collectors.toMap(TableJoin::getTableJoinId, Function.identity()));

        for (int rowIndex = 0; rowIndex < columns.size(); rowIndex++) {
            List<KeyValuePair<String>> row = columns.get(rowIndex);
            for (int columnIndex = 0; columnIndex < row.size(); columnIndex++) {
                KeyValuePair<String> column = row.get(columnIndex);

                if (!isNull(column.getKey())) {
                    TableJoin tableJoin = tableJoins.get(column.getKey());
                    tableJoin.setRowIndex(rowIndex);
                    tableJoin.setColumnIndex(columnIndex);
                    tableJoinDao.save(tableJoin);

                    Content content = contentDao.findByParentValidated(tableJoin.getTableJoinId());
                    content.setContent(column.getValue());
                    contentDao.save(content);
                }
            }
        }
    }
}
