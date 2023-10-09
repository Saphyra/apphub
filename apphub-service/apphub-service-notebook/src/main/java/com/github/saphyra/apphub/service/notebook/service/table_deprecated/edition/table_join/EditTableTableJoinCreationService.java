package com.github.saphyra.apphub.service.notebook.service.table_deprecated.edition.table_join;

import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_domain.KeyValuePair;
import com.github.saphyra.apphub.service.notebook.dao.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.content.ContentDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.table.join.TableJoin;
import com.github.saphyra.apphub.service.notebook.dao.table.join.TableJoinDao;
import com.github.saphyra.apphub.service.notebook.service.table_deprecated.TableJoinFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO remove
class EditTableTableJoinCreationService {
    private final ContentDao contentDao;
    private final TableJoinDao tableJoinDao;
    private final TableJoinFactory tableJoinFactory;

    void process(List<List<KeyValuePair<String>>> columns, ListItem listItem) {
        for (int rowIndex = 0; rowIndex < columns.size(); rowIndex++) {
            List<KeyValuePair<String>> row = columns.get(rowIndex);
            for (int columnIndex = 0; columnIndex < row.size(); columnIndex++) {
                KeyValuePair<String> column = row.get(columnIndex);

                if (isNull(column.getKey())) {
                    BiWrapper<TableJoin, Content> tableJoin = tableJoinFactory.create(listItem.getListItemId(), column.getValue(), rowIndex, columnIndex, listItem.getUserId());
                    tableJoinDao.save(tableJoin.getEntity1());
                    contentDao.save(tableJoin.getEntity2());
                }
            }
        }
    }
}
