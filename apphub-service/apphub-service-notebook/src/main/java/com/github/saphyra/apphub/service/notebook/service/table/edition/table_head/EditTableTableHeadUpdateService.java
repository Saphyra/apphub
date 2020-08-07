package com.github.saphyra.apphub.service.notebook.service.table.edition.table_head;

import com.github.saphyra.apphub.lib.common_domain.KeyValuePair;
import com.github.saphyra.apphub.service.notebook.dao.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.content.ContentDao;
import com.github.saphyra.apphub.service.notebook.dao.table.head.TableHead;
import com.github.saphyra.apphub.service.notebook.dao.table.head.TableHeadDao;
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
@Slf4j
@RequiredArgsConstructor
//TODO unit test
class EditTableTableHeadUpdateService {
    private final ContentDao contentDao;
    private final TableHeadDao tableHeadDao;

    public void process(List<KeyValuePair<String>> columnNames, UUID listItemId) {
        Map<UUID, TableHead> tableHeads = tableHeadDao.getByParent(listItemId)
            .stream()
            .collect(Collectors.toMap(TableHead::getTableHeadId, Function.identity()));

        for (int index = 0; index < columnNames.size(); index++) {
            KeyValuePair<String> request = columnNames.get(index);
            if (!isNull(request.getKey())) {
                TableHead tableHead = tableHeads.get(request.getKey());
                tableHead.setColumnIndex(index);
                tableHeadDao.save(tableHead);

                Content content = contentDao.findByParentValidated(tableHead.getTableHeadId());
                content.setContent(request.getValue());
                contentDao.save(content);
            }
        }
    }
}
