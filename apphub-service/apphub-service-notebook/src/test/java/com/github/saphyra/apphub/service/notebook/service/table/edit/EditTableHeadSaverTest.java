package com.github.saphyra.apphub.service.notebook.service.table.edit;

import com.github.saphyra.apphub.api.notebook.model.ItemType;
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
class EditTableHeadSaverTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();
    private static final Integer COLUMN_INDEX = 234;
    private static final UUID TABLE_HEAD_ID = UUID.randomUUID();
    private static final String CONTENT = "content";

    @Mock
    private TableHeadDao tableHeadDao;

    @Mock
    private TableHeadFactory tableHeadFactory;

    @Mock
    private ContentDao contentDao;

    @Mock
    private ContentFactory contentFactory;

    @InjectMocks
    private EditTableHeadSaver underTest;

    @Mock
    private ListItem listItem;

    @Mock
    private TableHeadModel model;

    @Mock
    private TableHead tableHead;

    @Mock
    private Content content;

    @Test
    void saveNew() {
        given(model.getType()).willReturn(ItemType.NEW);
        given(model.getColumnIndex()).willReturn(COLUMN_INDEX);
        given(model.getContent()).willReturn(CONTENT);
        given(listItem.getUserId()).willReturn(USER_ID);
        given(listItem.getListItemId()).willReturn(LIST_ITEM_ID);
        given(tableHead.getTableHeadId()).willReturn(TABLE_HEAD_ID);
        given(tableHeadFactory.create(USER_ID, LIST_ITEM_ID, COLUMN_INDEX)).willReturn(tableHead);
        given(contentFactory.create(LIST_ITEM_ID, TABLE_HEAD_ID, USER_ID, CONTENT)).willReturn(content);

        underTest.saveTableHeads(listItem, List.of(model));

        then(tableHeadDao).should().save(tableHead);
        then(contentDao).should().save(content);
    }

    @Test
    void saveExisting() {
        given(model.getType()).willReturn(ItemType.EXISTING);
        given(model.getColumnIndex()).willReturn(COLUMN_INDEX);
        given(model.getContent()).willReturn(CONTENT);
        given(model.getTableHeadId()).willReturn(TABLE_HEAD_ID);
        given(tableHeadDao.findByIdValidated(TABLE_HEAD_ID)).willReturn(tableHead);
        given(contentDao.findByParentValidated(TABLE_HEAD_ID)).willReturn(content);

        underTest.saveTableHeads(listItem, List.of(model));

        then(tableHead).should().setColumnIndex(COLUMN_INDEX);
        then(tableHeadDao).should().save(tableHead);
        then(content).should().setContent(CONTENT);
        then(contentDao).should().save(content);
    }
}