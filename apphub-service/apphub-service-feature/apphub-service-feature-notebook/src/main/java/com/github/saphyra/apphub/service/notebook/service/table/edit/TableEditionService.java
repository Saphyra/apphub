package com.github.saphyra.apphub.service.notebook.service.table.edit;

import com.github.saphyra.apphub.api.feature.notebook.model.table.EditTableRequest;
import com.github.saphyra.apphub.api.feature.notebook.model.table.TableFileUploadResponse;
import com.github.saphyra.apphub.lib.common_domain.QuadWrapper;
import com.github.saphyra.apphub.service.notebook.dao.list_item.CommonListItemDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.list_item.content.ContentDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.list_item.ListItemDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.table.head.TableHead;
import com.github.saphyra.apphub.service.notebook.dao.list_item.table.row.TableRow;
import com.github.saphyra.apphub.service.notebook.service.table.validator.EditTableRequestValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class TableEditionService {
    private final EditTableRequestValidator editTableRequestValidator;
    private final ListItemDao listItemDao;
    private final ContentDao contentDao;
    private final CommonListItemDao commonListItemDao;
    private final TableHeadEditionService tableHeadEditionService;
    private final TableRowEditionService tableRowEditionService;

    public List<TableFileUploadResponse> editTable(UUID userId, UUID listItemId, EditTableRequest request) {
        editTableRequestValidator.validate(request);

        QuadWrapper<ListItem, List<TableHead>, List<TableRow>, List<Content>> table = commonListItemDao.findTableValidated(userId, listItemId);
        ListItem listItem = table.getEntity1();
        List<TableHead> tableHeads = new ArrayList<>(table.getEntity2());
        List<TableRow> tableRows = new ArrayList<>(table.getEntity3());
        List<Content> contents = new ArrayList<>(table.getEntity4());

        processListItem(request, listItem);
        tableHeadEditionService.processTableHeads(listItemId, request.getTableHeads(), tableHeads, contents);
        List<TableFileUploadResponse> fileUploads = tableRowEditionService.processTableRows(listItemId, request.getRows(), tableRows, contents);
        contentDao.save(listItemId, contents);

        return fileUploads;
    }

    private void processListItem(EditTableRequest request, ListItem listItem) {
        if (!listItem.getTitle().equals(request.getTitle())) {
            log.info("Updating ListITem title...");
            listItem.setTitle(request.getTitle());
            listItemDao.save(listItem);
        }
    }
}
