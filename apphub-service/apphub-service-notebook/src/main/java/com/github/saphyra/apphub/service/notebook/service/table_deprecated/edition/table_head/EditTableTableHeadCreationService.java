package com.github.saphyra.apphub.service.notebook.service.table_deprecated.edition.table_head;

import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_domain.KeyValuePair;
import com.github.saphyra.apphub.service.notebook.dao.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.content.ContentDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.table_head.TableHead;
import com.github.saphyra.apphub.service.notebook.dao.table_head.TableHeadDao;
import com.github.saphyra.apphub.service.notebook.service.table_deprecated.TableHeadFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO remove
class EditTableTableHeadCreationService {
    private final ContentDao contentDao;
    private final TableHeadDao tableHeadDao;
    private final TableHeadFactory tableHeadFactory;

    void process(List<KeyValuePair<String>> columnNames, ListItem listItem) {
        for (int index = 0; index < columnNames.size(); index++) {
            KeyValuePair<String> request = columnNames.get(index);
            if (isNull(request.getKey())) {
                BiWrapper<TableHead, Content> biWrapper = tableHeadFactory.create(listItem.getListItemId(), request.getValue(), index, listItem.getUserId());
                tableHeadDao.save(biWrapper.getEntity1());
                contentDao.save(biWrapper.getEntity2());
            }
        }
    }
}
