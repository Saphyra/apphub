package com.github.saphyra.apphub.service.notebook.service.table.creation;

import com.github.saphyra.apphub.api.notebook.model.table.CreateTableRequest;
import com.github.saphyra.apphub.api.notebook.model.table.TableHeadModel;
import com.github.saphyra.apphub.service.notebook.dao.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.content.ContentDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.table_head.TableHead;
import com.github.saphyra.apphub.service.notebook.dao.table_head.TableHeadDao;
import com.github.saphyra.apphub.service.notebook.dao.table_head.TableHeadFactory;
import com.github.saphyra.apphub.service.notebook.service.ContentFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class TableHeadCreationServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();
    private static final Integer COLUMN_INDEX = 24;
    private static final UUID TABLE_HEAD_ID = UUID.randomUUID();
    private static final String CONTENT = "content";

    @Mock
    private TableHeadFactory tableHeadFactory;

    @Mock
    private TableHeadDao tableHeadDao;

    @Mock
    private ContentFactory contentFactory;

    @Mock
    private ContentDao contentDao;

    @InjectMocks
    private TableHeadCreationService underTest;

    @Mock
    private CreateTableRequest request;

    @Mock
    private TableHeadModel model;

    @Mock
    private TableHead tableHead;

    @Mock
    private ListItem listItem;

    @Mock
    private Content content;

    @Test
    void saveTableHeads() {
        given(request.getTableHeads()).willReturn(List.of(model));
        given(listItem.getListItemId()).willReturn(LIST_ITEM_ID);
        given(model.getColumnIndex()).willReturn(COLUMN_INDEX);
        given(tableHeadFactory.create(USER_ID, LIST_ITEM_ID, COLUMN_INDEX)).willReturn(tableHead);
        given(tableHead.getTableHeadId()).willReturn(TABLE_HEAD_ID);
        given(model.getContent()).willReturn(CONTENT);
        given(contentFactory.create(LIST_ITEM_ID, TABLE_HEAD_ID, USER_ID, CONTENT)).willReturn(content);

        underTest.saveTableHeads(USER_ID, request, listItem);

        then(tableHeadDao).should().save(tableHead);
        then(contentDao).should().save(content);
    }
}