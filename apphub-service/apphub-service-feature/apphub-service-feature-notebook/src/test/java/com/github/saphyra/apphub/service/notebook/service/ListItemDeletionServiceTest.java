package com.github.saphyra.apphub.service.notebook.service;

import com.github.saphyra.apphub.api.feature.notebook.model.ListItemType;
import com.github.saphyra.apphub.service.notebook.dao.list_item.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.list_item.ListItemDao;
import com.github.saphyra.apphub.service.notebook.dao.pin_group.PinGroupDao;
import com.github.saphyra.apphub.service.notebook.service.checklist.ChecklistDeletionService;
import com.github.saphyra.apphub.service.notebook.service.file.FileDeletionService;
import com.github.saphyra.apphub.service.notebook.service.table.TableDeletionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.inOrder;

@ExtendWith(MockitoExtension.class)
class ListItemDeletionServiceTest {
	private static final UUID LIST_ITEM_ID = UUID.randomUUID();
	private static final UUID CHILD_ID = UUID.randomUUID();
	private static final UUID USER_ID = UUID.randomUUID();

	@Mock
	private ListItemDao listItemDao;

	@Mock
	private TableDeletionService tableDeletionService;

	@Mock
	private ChecklistDeletionService checklistDeletionService;

	@Mock
	private PinGroupDao pinGroupDao;

	@Mock
	private FileDeletionService fileDeletionService;

	@InjectMocks
	private ListItemDeletionService underTest;

	@Mock
	private ListItem listItem;

	@Mock
	private ListItem child;

	@Test
	void deleteCategory() {
		givenListItemType(ListItemType.CATEGORY);
		given(listItemDao.getByUserIdAndParent(USER_ID, LIST_ITEM_ID)).willReturn(List.of(child));
		given(child.getType()).willReturn(ListItemType.TEXT);
		given(child.getListItemId()).willReturn(CHILD_ID);

		underTest.deleteListItem(LIST_ITEM_ID, USER_ID);

		InOrder inOrder = inOrder(listItemDao, pinGroupDao);
		inOrder.verify(listItemDao).delete(child);
		inOrder.verify(pinGroupDao).deleteListItemId(USER_ID, CHILD_ID);
		inOrder.verify(listItemDao).delete(listItem);
		inOrder.verify(pinGroupDao).deleteListItemId(USER_ID, LIST_ITEM_ID);
	}

	@Test
	void deleteChecklist() {
		givenListItemType(ListItemType.CHECKLIST);

		underTest.deleteListItem(LIST_ITEM_ID, USER_ID);

		then(checklistDeletionService).should().delete(listItem);
		then(pinGroupDao).should().deleteListItemId(USER_ID, LIST_ITEM_ID);
	}

	@ParameterizedTest
	@EnumSource(value = ListItemType.class, names = {"TEXT", "LINK", "ONLY_TITLE"})
	void deleteSimpleListItem(ListItemType type) {
		givenListItemType(type);

		underTest.deleteListItem(LIST_ITEM_ID, USER_ID);

		then(listItemDao).should().delete(listItem);
		then(pinGroupDao).should().deleteListItemId(USER_ID, LIST_ITEM_ID);
	}

	@ParameterizedTest
	@EnumSource(value = ListItemType.class, names = {"IMAGE", "FILE"})
	void deleteFile(ListItemType type) {
		givenListItemType(type);

		underTest.deleteListItem(LIST_ITEM_ID, USER_ID);

		then(fileDeletionService).should().deleteFile(listItem);
		then(pinGroupDao).should().deleteListItemId(USER_ID, LIST_ITEM_ID);
	}

	@ParameterizedTest
	@EnumSource(value = ListItemType.class, names = {"TABLE", "CHECKLIST_TABLE", "CUSTOM_TABLE"})
	void deleteTable(ListItemType type) {
		givenListItemType(type);

		underTest.deleteListItem(LIST_ITEM_ID, USER_ID);

		then(tableDeletionService).should().delete(listItem);
		then(pinGroupDao).should().deleteListItemId(USER_ID, LIST_ITEM_ID);
	}

	private void givenListItemType(ListItemType listItemType) {
		given(listItemDao.findByIdValidated(USER_ID, LIST_ITEM_ID)).willReturn(listItem);
		given(listItem.getType()).willReturn(listItemType);
		given(listItem.getListItemId()).willReturn(LIST_ITEM_ID);
	}

}