package com.github.saphyra.apphub.service.notebook.service.table.edit;

import com.github.saphyra.apphub.api.feature.notebook.model.ListItemType;
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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class TableEditionServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();

    @Mock
    private EditTableRequestValidator editTableRequestValidator;

    @Mock
    private ListItemDao listItemDao;

    @Mock
    private ContentDao contentDao;

    @Mock
    private CommonListItemDao commonListItemDao;

    @Mock
    private TableHeadEditionService tableHeadEditionService;

    @Mock
    private TableRowEditionService tableRowEditionService;

    @InjectMocks
    private TableEditionService underTest;

    @Test
    void editTable_titleChanged() {
        ListItem listItem = ListItem.builder()
            .listItemId(LIST_ITEM_ID)
            .userId(USER_ID)
            .type(ListItemType.TABLE)
            .title("old-title")
            .build();
        EditTableRequest request = EditTableRequest.builder()
            .title("new-title")
            .tableHeads(List.of())
            .rows(List.of())
            .build();
        List<Content> contents = List.of(Content.builder().listItemId(LIST_ITEM_ID).build());
        List<TableFileUploadResponse> fileUploads = List.of(TableFileUploadResponse.builder().rowIndex(1).build());

        given(commonListItemDao.findTableValidated(USER_ID, LIST_ITEM_ID)).willReturn(new QuadWrapper<>(listItem, List.<TableHead>of(), List.<TableRow>of(), contents));
        given(tableRowEditionService.processTableRows(LIST_ITEM_ID, request.getRows(), List.of(), contents)).willReturn(fileUploads);

        List<TableFileUploadResponse> result = underTest.editTable(USER_ID, LIST_ITEM_ID, request);

        assertThat(result).isEqualTo(fileUploads);
        assertThat(listItem.getTitle()).isEqualTo("new-title");
        then(editTableRequestValidator).should().validate(request);
        then(tableHeadEditionService).should().processTableHeads(LIST_ITEM_ID, request.getTableHeads(), List.of(), contents);
        then(listItemDao).should().save(listItem);
        then(contentDao).should().save(LIST_ITEM_ID, contents);
    }

    @Test
    void editTable_titleNotChanged() {
        ListItem listItem = ListItem.builder()
            .listItemId(LIST_ITEM_ID)
            .userId(USER_ID)
            .type(ListItemType.TABLE)
            .title("title")
            .build();
        EditTableRequest request = EditTableRequest.builder()
            .title("title")
            .tableHeads(List.of())
            .rows(List.of())
            .build();
        List<Content> contents = List.of(Content.builder().listItemId(LIST_ITEM_ID).build());

        given(commonListItemDao.findTableValidated(USER_ID, LIST_ITEM_ID)).willReturn(new QuadWrapper<>(listItem, List.<TableHead>of(), List.<TableRow>of(), contents));
        given(tableRowEditionService.processTableRows(LIST_ITEM_ID, request.getRows(), List.of(), contents)).willReturn(List.of());

        underTest.editTable(USER_ID, LIST_ITEM_ID, request);

        then(listItemDao).shouldHaveNoInteractions();
    }
}

