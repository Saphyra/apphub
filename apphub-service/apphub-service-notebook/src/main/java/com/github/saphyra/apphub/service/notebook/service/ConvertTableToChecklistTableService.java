package com.github.saphyra.apphub.service.notebook.service;

import com.github.saphyra.apphub.lib.common_domain.ErrorMessage;
import com.github.saphyra.apphub.lib.common_util.ErrorCode;
import com.github.saphyra.apphub.lib.exception.UnprocessableEntityException;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemType;
import com.github.saphyra.apphub.service.notebook.dao.table.join.TableJoin;
import com.github.saphyra.apphub.service.notebook.dao.table.join.TableJoinDao;
import com.github.saphyra.apphub.service.notebook.dao.table.row.ChecklistTableRow;
import com.github.saphyra.apphub.service.notebook.dao.table.row.ChecklistTableRowDao;
import com.github.saphyra.apphub.service.notebook.service.checklist_table.ChecklistTableRowFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Component
public class ConvertTableToChecklistTableService {
    private final ListItemDao listItemDao;
    private final TableJoinDao tableJoinDao;
    private final ChecklistTableRowFactory checklistTableRowFactory;
    private final ChecklistTableRowDao checklistTableRowDao;

    public void convert(UUID listItemId) {
        ListItem listItem = listItemDao.findByIdValidated(listItemId);

        if (listItem.getType() != ListItemType.TABLE) {
            throw new UnprocessableEntityException(new ErrorMessage(ErrorCode.INVALID_TYPE.name()), listItemId + " is not a table, it is " + listItem.getType());
        }

        listItem.setType(ListItemType.CHECKLIST_TABLE);
        listItemDao.save(listItem);

        int rowCount = tableJoinDao.getByParent(listItemId)
            .stream()
            .collect(Collectors.groupingBy(TableJoin::getRowIndex))
            .size();

        for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
            ChecklistTableRow checklistTableRow = checklistTableRowFactory.create(listItem.getUserId(), listItemId, rowIndex, false);
            checklistTableRowDao.save(checklistTableRow);
        }
    }
}
