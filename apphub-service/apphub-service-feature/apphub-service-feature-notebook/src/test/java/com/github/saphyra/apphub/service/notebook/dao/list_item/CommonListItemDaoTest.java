package com.github.saphyra.apphub.service.notebook.dao.list_item;

import com.github.saphyra.apphub.lib.common_domain.QuadWrapper;
import com.github.saphyra.apphub.lib.common_domain.TriWrapper;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.service.notebook.dao.list_item.checklist_item.ChecklistItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.checklist_item.ChecklistItemDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.list_item.content.ContentDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.list_item.ListItemDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.table.head.TableHead;
import com.github.saphyra.apphub.service.notebook.dao.list_item.table.head.TableHeadDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.table.row.TableRow;
import com.github.saphyra.apphub.service.notebook.dao.list_item.table.row.TableRowDao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class CommonListItemDaoTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();
    private static final String LIST_ITEM_ID_STRING = "list-item-id";

    @Mock
    private ListItemDao listItemDao;

    @Mock
    private ChecklistItemDao checklistItemDao;

    @Mock
    private ContentDao contentDao;

    @Mock
    private TableHeadDao tableHeadDao;

    @Mock
    private TableRowDao tableRowDao;

    @Mock
    private CommonListItemRepository repository;

    @Mock
    private UuidConverter uuidConverter;

    @InjectMocks
    private CommonListItemDao underTest;

    @Mock
    private ListItem listItem;

    @Mock
    private ChecklistItem checklistItem;

    @Mock
    private ChecklistItem newChecklistItem;

    @Mock
    private ChecklistItem modifiedChecklistItem;

    @Mock
    private Content content;

    @Mock
    private TableHead tableHead;

    @Mock
    private TableRow tableRow;

    @Test
    void delete() {
        given(listItem.getListItemId()).willReturn(LIST_ITEM_ID);
        given(uuidConverter.convertDomain(LIST_ITEM_ID)).willReturn(LIST_ITEM_ID_STRING);

        underTest.delete(listItem);

        then(listItemDao).should().delete(listItem);
        then(repository).should().deleteByListItemId(LIST_ITEM_ID_STRING);
    }

    @Test
    void saveChecklist() {
        List<ChecklistItem> checklistItems = List.of(checklistItem);
        List<Content> contents = List.of(content);

        given(listItem.getListItemId()).willReturn(LIST_ITEM_ID);

        underTest.saveChecklist(listItem, checklistItems, contents);

        then(listItemDao).should().save(listItem);
        then(checklistItemDao).should().save(checklistItems);
        then(contentDao).should().save(LIST_ITEM_ID, contents);
    }

    @Test
    void findChecklistValidated() {
        List<ChecklistItem> checklistItems = List.of(checklistItem);
        List<Content> contents = List.of(content);

        given(listItemDao.findByIdValidated(USER_ID, LIST_ITEM_ID)).willReturn(listItem);
        given(checklistItemDao.getByListItemId(LIST_ITEM_ID)).willReturn(checklistItems);
        given(contentDao.getByListItemId(LIST_ITEM_ID)).willReturn(contents);

        TriWrapper<ListItem, List<ChecklistItem>, List<Content>> result = underTest.findChecklistValidated(USER_ID, LIST_ITEM_ID);

        assertThat(result.getEntity1()).isEqualTo(listItem);
        assertThat(result.getEntity2()).isEqualTo(checklistItems);
        assertThat(result.getEntity3()).isEqualTo(contents);
    }

    @Test
    void editChecklist() {
        List<ChecklistItem> deletedChecklistItems = List.of(checklistItem);
        List<ChecklistItem> newChecklistItems = List.of(newChecklistItem);
        List<ChecklistItem> modifiedChecklistItems = List.of(modifiedChecklistItem);
        List<Content> contents = List.of(content);

        underTest.editChecklist(LIST_ITEM_ID, deletedChecklistItems, newChecklistItems, modifiedChecklistItems, contents);

        then(checklistItemDao).should().delete(deletedChecklistItems);
        then(contentDao).should().save(LIST_ITEM_ID, contents);

        ArgumentCaptor<List<ChecklistItem>> savedCaptor = ArgumentCaptor.forClass(List.class);
        then(checklistItemDao).should().save(savedCaptor.capture());
        assertThat(savedCaptor.getValue()).containsExactlyInAnyOrder(newChecklistItem, modifiedChecklistItem);
    }

    @Test
    void saveTable() {
        List<TableHead> tableHeads = List.of(tableHead);
        List<TableRow> rows = List.of(tableRow);
        List<Content> contents = List.of(content);

        given(listItem.getListItemId()).willReturn(LIST_ITEM_ID);

        underTest.saveTable(listItem, tableHeads, rows, contents);

        then(listItemDao).should().save(listItem);
        then(tableHeadDao).should().save(LIST_ITEM_ID, tableHeads);
        then(tableRowDao).should().save(rows);
        then(contentDao).should().save(LIST_ITEM_ID, contents);
    }

    @Test
    void findTableValidated() {
        List<TableHead> tableHeads = List.of(tableHead);
        List<TableRow> rows = List.of(tableRow);
        List<Content> contents = List.of(content);

        given(listItemDao.findByIdValidated(USER_ID, LIST_ITEM_ID)).willReturn(listItem);
        given(tableHeadDao.findByListItemIdValidated(LIST_ITEM_ID)).willReturn(tableHeads);
        given(tableRowDao.getByListItemId(LIST_ITEM_ID)).willReturn(rows);
        given(contentDao.getByListItemId(LIST_ITEM_ID)).willReturn(contents);

        QuadWrapper<ListItem, List<TableHead>, List<TableRow>, List<Content>> result = underTest.findTableValidated(USER_ID, LIST_ITEM_ID);

        assertThat(result.getEntity1()).isEqualTo(listItem);
        assertThat(result.getEntity2()).isEqualTo(tableHeads);
        assertThat(result.getEntity3()).isEqualTo(rows);
        assertThat(result.getEntity4()).isEqualTo(contents);
    }

    @Test
    void deleteByUserId() {
        given(listItemDao.getByUserId(USER_ID)).willReturn(List.of(listItem));
        given(listItem.getListItemId()).willReturn(LIST_ITEM_ID);
        given(uuidConverter.convertDomain(LIST_ITEM_ID)).willReturn(LIST_ITEM_ID_STRING);

        underTest.deleteByUserId(USER_ID);

        then(listItemDao).should().delete(listItem);
        then(repository).should().deleteByListItemId(LIST_ITEM_ID_STRING);
    }
}


